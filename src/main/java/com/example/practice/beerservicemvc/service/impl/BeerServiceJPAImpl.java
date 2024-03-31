package com.example.practice.beerservicemvc.service.impl;

import com.example.practice.beerservicemvc.entities.Beer;
import com.example.practice.beerservicemvc.mapper.BeerMapper;
import com.example.practice.beerservicemvc.model.BeerDTO;
import com.example.practice.beerservicemvc.model.BeerStyle;
import com.example.practice.beerservicemvc.repository.BeerRepository;
import com.example.practice.beerservicemvc.service.BeerService;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Primary
@Service
@RequiredArgsConstructor
public class BeerServiceJPAImpl implements BeerService {
    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 50;
    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper;


    @Override
    public Optional<BeerDTO> getBeerById(UUID id) {
        return Optional.ofNullable(
            beerMapper.beerToBeerDto(beerRepository.findById(id)
                .orElse(null))
        );
    }


    @Override
    public Page<BeerDTO> listBeers(
        String beerName, BeerStyle beerStyle, Boolean showInventory,
        Integer pageNumber, Integer pageSize) {
        Page<Beer> beerPage;
        PageRequest pageRequest = buildPageRequest(pageNumber, pageSize);
        if (StringUtils.hasText(beerName) && beerStyle == null) {
            beerPage = findBeersByBeerName(beerName, pageRequest);
        }
        else if (!StringUtils.hasText(beerName) && beerStyle != null) {
            beerPage = findBeersByBeerStyle(beerStyle, pageRequest);
        }
        else if (StringUtils.hasText(beerName) && beerStyle != null) {
            beerPage = findBeersByBeerNameAndBeerStyle(beerName, beerStyle, pageRequest);
        }
        else {
            beerPage = beerRepository.findAll(pageRequest);
        }

        if (showInventory != null && !showInventory) {
            beerPage.forEach(beer -> beer.setQuantityOnHand(null));
        }

        return beerPage.map(beerMapper::beerToBeerDto);
    }


    public PageRequest buildPageRequest(Integer pageNumber, Integer pageSize) {
        int queryPageSize;
        int queryPageNumber = (pageNumber != null && pageNumber > 0) ? pageNumber - 1 : DEFAULT_PAGE_NUMBER;

        if (pageSize != null) {
            queryPageSize = (pageSize > 1000) ? 1000 : pageSize;
        }
        else {
            queryPageSize = DEFAULT_PAGE_SIZE;
        }
        Sort sort = Sort.by(Sort.Order.asc("name"));
        return PageRequest.of(queryPageNumber, queryPageSize, sort);
    }


    private Page<Beer> findBeersByBeerNameAndBeerStyle(String beerName, BeerStyle beerStyle, PageRequest pageRequest) {
        return beerRepository.findBeerByNameIsLikeIgnoreCaseAndAndBeerStyle("%" + beerName + "%", beerStyle, pageRequest);
    }


    private Page<Beer> findBeersByBeerStyle(BeerStyle beerStyle, PageRequest pageRequest) {
        return beerRepository.findBeerByBeerStyle(beerStyle, pageRequest);
    }


    private Page<Beer> findBeersByBeerName(String beerName, PageRequest pageRequest) {
        return beerRepository.findBeerByNameIsLikeIgnoreCase("%" + beerName + "%", pageRequest);
    }


    @Override
    public BeerDTO addBeer(BeerDTO beer) {
        return beerMapper.beerToBeerDto(beerRepository.save(beerMapper.beerDtoToBeer(beer)));
    }


    @Override
    public Optional<BeerDTO> updateBeer(UUID beerId, BeerDTO beer) {
        AtomicReference<Optional<BeerDTO>> atomicReference = new AtomicReference<>();
        beerRepository.findById(beerId).ifPresentOrElse(foundBeer -> {
            foundBeer.setName(beer.getName());
            foundBeer.setBeerStyle(beer.getBeerStyle());
            foundBeer.setPrice(beer.getPrice());
            foundBeer.setUpc(beer.getUpc());
            atomicReference.set(
                Optional.of(beerMapper.beerToBeerDto(beerRepository.save(foundBeer)))
            );
        }, () -> atomicReference.set(Optional.empty()));
        return atomicReference.get();
    }


    @Override
    public Boolean deleteById(UUID beerId) {
        if (beerRepository.existsById(beerId)) {
            beerRepository.deleteById(beerId);
            return true;
        }
        return false;
    }


    @Override
    public Optional<BeerDTO> patchBeer(UUID beerId, BeerDTO beerDTO) {
        AtomicReference<Optional<BeerDTO>> atomicReference = new AtomicReference<>();
        beerRepository.findById(beerId).ifPresentOrElse(foundBeer -> {
            if (StringUtils.hasText(beerDTO.getName())) {
                foundBeer.setName(beerDTO.getName());
            }

            if (StringUtils.hasText(beerDTO.getUpc())) {
                foundBeer.setUpc(beerDTO.getUpc());
            }

            if (beerDTO.getBeerStyle() != null) {
                foundBeer.setBeerStyle(beerDTO.getBeerStyle());
            }

            if (beerDTO.getPrice() != null) {
                foundBeer.setPrice(beerDTO.getPrice());
            }

            if (beerDTO.getQuantityOnHand() != null) {
                foundBeer.setQuantityOnHand(beerDTO.getQuantityOnHand());
            }
            atomicReference.set(
                Optional.of(beerMapper.beerToBeerDto(beerRepository.save(foundBeer)))
            );
        }, () -> atomicReference.set(Optional.empty()));
        return atomicReference.get();
    }
}
