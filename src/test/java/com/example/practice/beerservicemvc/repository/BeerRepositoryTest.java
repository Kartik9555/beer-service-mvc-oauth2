package com.example.practice.beerservicemvc.repository;

import com.example.practice.beerservicemvc.bootstrap.BootstrapData;
import com.example.practice.beerservicemvc.entities.Beer;
import com.example.practice.beerservicemvc.model.BeerStyle;
import com.example.practice.beerservicemvc.service.impl.BeerCsvServiceImpl;
import jakarta.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import({BootstrapData.class, BeerCsvServiceImpl.class})
class BeerRepositoryTest {

    @Autowired
    private BeerRepository beerRepository;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Test
    void testFindBeerByBeerName() {
        Page<Beer> beers = beerRepository.findBeerByNameIsLikeIgnoreCase("%IPA%", null);
        System.out.println(beers.getContent().size());
        assertThat(beers.getContent().size()).isEqualTo(336);
    }


    @Test
    void testFindBeerByBeerStyle() {
        Page<Beer> beers = beerRepository.findBeerByBeerStyle(BeerStyle.IPA, null);
        System.out.println(beers.getContent().size());
        assertThat(beers.getContent().size()).isEqualTo(548);
    }


    @Test
    void testSaveBeerNameTooLong() {
        assertThrows(ConstraintViolationException.class, () -> {
            beerRepository.save(Beer.builder()
                .name("New Beer 1234566788123456678812345667881234566788123456678812345667881234566788")
                .beerStyle(BeerStyle.PALE_ALE)
                .upc("123345")
                .price(new BigDecimal("12.33"))
                .build());

            beerRepository.flush();
        });
    }


    @Test
    void testSaveBeer() {
        Beer savedBeer = beerRepository.save(Beer.builder()
            .name("New Beer")
            .beerStyle(BeerStyle.PALE_ALE)
            .upc("123345")
            .price(new BigDecimal("12.33"))
            .build());

        beerRepository.flush();

        assertThat(savedBeer).isNotNull();
        assertThat(savedBeer.getId()).isNotNull();

    }
}