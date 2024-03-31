package com.example.practice.beerservicemvc.service.impl;

import com.example.practice.beerservicemvc.model.BeerCSVRecord;
import com.example.practice.beerservicemvc.service.BeerCsvService;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.util.ResourceUtils;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BeerCsvServiceImplTest {

    @Autowired
    BeerCsvService beerCsvService;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Test
    void convertCSV() throws FileNotFoundException {
        File file = ResourceUtils.getFile("classpath:csvdata/beers.csv");
        List<BeerCSVRecord> records = beerCsvService.convertCSV(file);
        System.out.println(records.size());

        assertThat(records.size()).isNotZero();
    }
}