package com.example.practice.beerservicemvc.mapper;

import com.example.practice.beerservicemvc.entities.Beer;
import com.example.practice.beerservicemvc.model.BeerDTO;
import org.mapstruct.Mapper;

@Mapper
public interface BeerMapper {
    Beer beerDtoToBeer(BeerDTO dto);

    BeerDTO beerToBeerDto(Beer beer);
}
