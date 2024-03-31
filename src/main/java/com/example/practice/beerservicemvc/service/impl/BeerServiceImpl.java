package com.example.practice.beerservicemvc.service.impl;

import com.example.practice.beerservicemvc.model.BeerDTO;
import com.example.practice.beerservicemvc.model.BeerStyle;
import com.example.practice.beerservicemvc.service.BeerService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class BeerServiceImpl implements BeerService {

    private Map<UUID, BeerDTO> beerMap;


    public BeerServiceImpl() {
        beerMap = new HashMap<>();
        BeerDTO beer1 = BeerDTO.builder()
            .id(UUID.randomUUID())
            .version(1)
            .name("Galaxy Cat")
            .beerStyle(BeerStyle.PALE_ALE)
            .upc("12345")
            .price(new BigDecimal("12.99"))
            .quantityOnHand(122)
            .createdDate(LocalDateTime.now())
            .updatedDate(LocalDateTime.now())
            .build();

        BeerDTO beer2 = BeerDTO.builder()
            .id(UUID.randomUUID())
            .version(1)
            .name("Crank")
            .beerStyle(BeerStyle.PALE_ALE)
            .upc("123456222")
            .price(new BigDecimal("11.99"))
            .quantityOnHand(392)
            .createdDate(LocalDateTime.now())
            .updatedDate(LocalDateTime.now())
            .build();

        BeerDTO beer3 = BeerDTO.builder()
            .id(UUID.randomUUID())
            .version(1)
            .name("Sunshine City")
            .beerStyle(BeerStyle.IPA)
            .upc("12356")
            .price(new BigDecimal("13.99"))
            .quantityOnHand(144)
            .createdDate(LocalDateTime.now())
            .updatedDate(LocalDateTime.now())
            .build();
        beerMap.put(beer1.getId(), beer1);
        beerMap.put(beer2.getId(), beer2);
        beerMap.put(beer3.getId(), beer3);
    }


    @Override
    public Optional<BeerDTO> getBeerById(UUID id) {
        log.debug("Get Beer by Id - in service. Id: " + id.toString());

        return Optional.of(beerMap.get(id));
    }


    @Override
    public Page<BeerDTO> listBeers(String beerName, BeerStyle beerStyle, Boolean showInventory, Integer pageNumber, Integer pageSize) {
        return new PageImpl<>(new ArrayList<>(beerMap.values()));
    }


    @Override
    public BeerDTO addBeer(BeerDTO beer) {
        log.debug("Save Beer - in service.");
        BeerDTO savedBeer = BeerDTO.builder()
            .id(UUID.randomUUID())
            .createdDate(LocalDateTime.now())
            .updatedDate(LocalDateTime.now())
            .version(1)
            .name(beer.getName())
            .beerStyle(beer.getBeerStyle())
            .quantityOnHand(beer.getQuantityOnHand())
            .upc(beer.getUpc())
            .build();
        beerMap.put(savedBeer.getId(), savedBeer);
        return savedBeer;
    }


    @Override
    public Optional<BeerDTO> updateBeer(UUID beerId, BeerDTO beer) {
        BeerDTO existing = beerMap.get(beerId);
        existing.setUpc(beer.getUpc());
        existing.setPrice(beer.getPrice());
        existing.setQuantityOnHand(beer.getQuantityOnHand());
        existing.setName(beer.getName());

        return Optional.of(existing);
    }


    @Override
    public Boolean deleteById(UUID beerId) {
        beerMap.remove(beerId);
        return true;
    }


    @Override
    public Optional<BeerDTO> patchBeer(UUID beerId, BeerDTO beer) {
        BeerDTO existing = beerMap.get(beerId);
        if (StringUtils.hasText(beer.getName())) {
            existing.setName(beer.getName());
        }

        if (StringUtils.hasText(beer.getUpc())) {
            existing.setUpc(beer.getUpc());
        }

        if (beer.getBeerStyle() != null) {
            existing.setBeerStyle(beer.getBeerStyle());
        }

        if (beer.getPrice() != null) {
            existing.setPrice(beer.getPrice());
        }

        if (beer.getQuantityOnHand() != null) {
            existing.setQuantityOnHand(beer.getQuantityOnHand());
        }
        return Optional.of(existing);
    }
}
