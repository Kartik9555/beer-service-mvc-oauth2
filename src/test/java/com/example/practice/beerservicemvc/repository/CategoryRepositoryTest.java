package com.example.practice.beerservicemvc.repository;

import com.example.practice.beerservicemvc.entities.Beer;
import com.example.practice.beerservicemvc.entities.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class CategoryRepositoryTest {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    BeerRepository beerRepository;

    @MockBean
    private JwtDecoder jwtDecoder;

    Beer testBeer;


    @BeforeEach
    void setUp() {
        testBeer = beerRepository.findAll().get(0);
    }


    @Transactional
    @Test
    void testCategory() {
        Category category = categoryRepository.save(Category.builder()
            .description("ALES")
            .build());

        testBeer.addCategory(category);
        Beer savedBeer = beerRepository.save(testBeer);
        System.out.println(savedBeer.getName());
    }
}