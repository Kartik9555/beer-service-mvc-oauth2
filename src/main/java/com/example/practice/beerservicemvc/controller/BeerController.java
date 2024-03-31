package com.example.practice.beerservicemvc.controller;

import com.example.practice.beerservicemvc.exception.NotFoundException;
import com.example.practice.beerservicemvc.model.BeerDTO;
import com.example.practice.beerservicemvc.model.BeerStyle;
import com.example.practice.beerservicemvc.service.BeerService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class BeerController {
    static final String BEER_PATH = "/api/v1/beer";
    static final String BEER_PATH_ID = BEER_PATH + "/{beerId}";

    private final BeerService beerService;


    @GetMapping(BEER_PATH)
    public Page<BeerDTO> listBeers(
        @RequestParam(required = false, value = "name") String beerName,
        @RequestParam(required = false, value = "beerStyle") BeerStyle beerStyle,
        @RequestParam(required = false, value = "showInventory") Boolean showInventory,
        @RequestParam(required = false, value = "pageNumber") Integer pageNumber,
        @RequestParam(required = false, value = "pageSize") Integer pageSize
    ) {
        return beerService.listBeers(beerName, beerStyle, showInventory, pageNumber, pageSize);
    }


    @GetMapping(BEER_PATH_ID)
    public BeerDTO getBeerById(@PathVariable("beerId") UUID beerId) {
        log.debug("Get Beer By Id - in controller");
        return beerService.getBeerById(beerId).orElseThrow(NotFoundException::new);
    }


    @PostMapping(BEER_PATH)
    public ResponseEntity<HttpStatus> addBeer(@Validated @RequestBody BeerDTO beer) {
        BeerDTO savedBeer = beerService.addBeer(beer);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, BEER_PATH + "/" + savedBeer.getId());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }


    @PutMapping(BEER_PATH_ID)
    public ResponseEntity<HttpStatus> updateBeer(@PathVariable("beerId") UUID beerId, @Validated @RequestBody BeerDTO beer) {
        if (beerService.updateBeer(beerId, beer).isEmpty()) {
            throw new NotFoundException();
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @DeleteMapping(BEER_PATH_ID)
    public ResponseEntity<HttpStatus> deleteById(@PathVariable("beerId") UUID beerId) {
        if (!beerService.deleteById(beerId)) {
            throw new NotFoundException();
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @PatchMapping(BEER_PATH_ID)
    public ResponseEntity<HttpStatus> patchBeer(@PathVariable("beerId") UUID beerId, @RequestBody BeerDTO beer) {
        if (beerService.patchBeer(beerId, beer).isEmpty()) {
            throw new NotFoundException();
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
