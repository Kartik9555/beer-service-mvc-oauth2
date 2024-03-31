package com.example.practice.beerservicemvc.repository;

import com.example.practice.beerservicemvc.entities.Beer;
import com.example.practice.beerservicemvc.model.BeerStyle;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeerRepository extends JpaRepository<Beer, UUID> {
    Page<Beer> findBeerByNameIsLikeIgnoreCase(String name, Pageable page);

    Page<Beer> findBeerByBeerStyle(BeerStyle beerStyle, Pageable page);

    Page<Beer> findBeerByNameIsLikeIgnoreCaseAndAndBeerStyle(String name, BeerStyle beerStyle, Pageable page);
}
