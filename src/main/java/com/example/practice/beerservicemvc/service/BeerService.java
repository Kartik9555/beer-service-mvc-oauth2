package com.example.practice.beerservicemvc.service;

import com.example.practice.beerservicemvc.model.BeerDTO;
import com.example.practice.beerservicemvc.model.BeerStyle;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;

public interface BeerService {

    Optional<BeerDTO> getBeerById(UUID id);

    Page<BeerDTO> listBeers(String beerName, BeerStyle beerStyle, Boolean showInventory, Integer pageNumber, Integer pageSize);

    BeerDTO addBeer(BeerDTO beer);

    Optional<BeerDTO> updateBeer(UUID beerId, BeerDTO beer);

    Boolean deleteById(UUID beerId);

    Optional<BeerDTO> patchBeer(UUID beerId, BeerDTO beer);
}
