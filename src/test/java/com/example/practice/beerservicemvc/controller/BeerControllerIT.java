package com.example.practice.beerservicemvc.controller;

import com.example.practice.beerservicemvc.entities.Beer;
import com.example.practice.beerservicemvc.exception.NotFoundException;
import com.example.practice.beerservicemvc.mapper.BeerMapper;
import com.example.practice.beerservicemvc.model.BeerDTO;
import com.example.practice.beerservicemvc.model.BeerStyle;
import com.example.practice.beerservicemvc.repository.BeerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static com.example.practice.beerservicemvc.controller.BeerController.BEER_PATH_ID;
import static com.example.practice.beerservicemvc.controller.BeerControllerTest.jwt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class BeerControllerIT {
    @Autowired
    BeerController beerController;

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    BeerMapper beerMapper;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WebApplicationContext wac;

    MockMvc mockMvc;

    @MockBean
    private JwtDecoder jwtDecoder;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
            .apply(springSecurity())
            .build();
    }


    @Test
    void patchBeerNameTooLong() throws Exception {
        Beer beer = beerRepository.findAll().get(0);

        Map<String, String> bearMap = new HashMap<>();
        bearMap.put("name", "New name 123456789123456789123456789123456789123456789123456789123456789123456789123456789123456789123456789123456789");

        MvcResult mvcResult = mockMvc.perform(patch(BEER_PATH_ID, beer.getId())
                .with(jwt)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bearMap))
            ).andExpect(status().isBadRequest())
            .andReturn();

        System.out.println(mvcResult.getResponse().getContentAsString());
    }


    @Test
    void testPatchBeerNotFound() {
        assertThrows(NotFoundException.class, () -> beerController.patchBeer(UUID.randomUUID(), BeerDTO.builder().build()));
    }


    @Rollback
    @Transactional
    @Test
    void testPatchBeer() {
        Beer beer = beerRepository.findAll().get(0);
        BeerDTO beerDTO = beerMapper.beerToBeerDto(beer);
        beerDTO.setId(null);
        beerDTO.setVersion(null);
        String updatedName = "new name";
        beerDTO.setName(updatedName);
        ResponseEntity<HttpStatus> responseEntity = beerController.patchBeer(beer.getId(), beerDTO);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));
        Beer savedBeer = beerRepository.findById(beer.getId()).get();
        assertThat(savedBeer.getName()).isEqualTo(updatedName);
    }


    @Rollback
    @Transactional
    @Test
    void testDeleteByIdNotFound() {
        assertThrows(NotFoundException.class, () -> beerController.deleteById(UUID.randomUUID()));
    }


    @Rollback
    @Transactional
    @Test
    void testDeleteById() {
        Beer beer = beerRepository.findAll().get(0);
        ResponseEntity<HttpStatus> responseEntity = beerController.deleteById(beer.getId());
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));
        assertThat(beerRepository.findById(beer.getId())).isEmpty();
    }


    @Test
    void testUpdateBeerNotFound() {
        assertThrows(NotFoundException.class, () -> beerController.updateBeer(UUID.randomUUID(), BeerDTO.builder().build()));
    }


    @Rollback
    @Transactional
    @Test
    void testUpdateBeer() {
        Beer beer = beerRepository.findAll().get(0);
        BeerDTO beerDTO = beerMapper.beerToBeerDto(beer);
        beerDTO.setId(null);
        beerDTO.setVersion(null);
        String updatedName = "new name";
        beerDTO.setName(updatedName);
        ResponseEntity<HttpStatus> responseEntity = beerController.updateBeer(beer.getId(), beerDTO);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));
        Beer savedBeer = beerRepository.findById(beer.getId()).get();
        assertThat(savedBeer.getName()).isEqualTo(updatedName);
    }


    @Rollback
    @Transactional
    @Test
    void testSaveNewBeer() {
        BeerDTO dto = BeerDTO.builder()
            .name("New beer")
            .build();

        ResponseEntity<HttpStatus> responseEntity = beerController.addBeer(dto);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));
        assertThat(responseEntity.getHeaders().getLocation()).isNotNull();

        String[] locationUUID = responseEntity.getHeaders().getLocation().getPath().split("/");
        UUID savedUUID = UUID.fromString(locationUUID[4]);

        Beer beer = beerRepository.findById(savedUUID).get();

        assertThat(beer).isNotNull();
    }


    @Test
    void testGetById() {
        Beer beer = beerRepository.findAll().get(0);
        BeerDTO dto = beerController.getBeerById(beer.getId());
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(beer.getId());
    }


    @Test
    void testBeerIdNotFound() {
        assertThrows(NotFoundException.class, () -> beerController.getBeerById(UUID.randomUUID()));
    }


    @Test
    void testListBeersByName() throws Exception {
        mockMvc.perform(get(BeerController.BEER_PATH)
                .with(jwt)
                .queryParam("name", "ALE")
                .queryParam("pageSize", "800")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
            ).andExpect(status().isOk())
            .andExpect(jsonPath("$.content.size()", is(636)));
    }


    @Test
    void testListBeersByBeerStyleAndShowInventoryTruePage2() throws Exception {
        mockMvc.perform(get(BeerController.BEER_PATH)
                .with(jwt)
                .queryParam("name", "ALE")
                .queryParam("beerStyle", BeerStyle.ALE.name())
                .queryParam("showInventory", "true")
                .queryParam("pageNumber", "2")
                .queryParam("pageSize", "50")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
            ).andExpect(status().isOk())
            .andExpect(jsonPath("$.content.size()", is(50)))
            .andExpect(jsonPath("$.content.[0].quantityOnHand").value(IsNull.notNullValue()));
    }


    @Test
    void testListBeersByBeerStyleAndShowInventoryTrue() throws Exception {
        mockMvc.perform(get(BeerController.BEER_PATH)
                .with(jwt)
                .queryParam("name", "ALE")
                .queryParam("beerStyle", BeerStyle.ALE.name())
                .queryParam("showInventory", "true")
                .queryParam("pageSize", "800")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
            ).andExpect(status().isOk())
            .andExpect(jsonPath("$.content.size()", is(251)))
            .andExpect(jsonPath("$.content.[0].quantityOnHand").value(IsNull.notNullValue()));
    }


    @Test
    void testListBeersByBeerStyleAndShowInventoryFalse() throws Exception {
        mockMvc.perform(get(BeerController.BEER_PATH)
                .with(jwt)
                .queryParam("name", "ALE")
                .queryParam("beerStyle", BeerStyle.ALE.name())
                .queryParam("showInventory", "false")
                .queryParam("pageSize", "800")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
            ).andExpect(status().isOk())
            .andExpect(jsonPath("$.content.size()", is(251)))
            .andExpect(jsonPath("$.content.[0].quantityOnHand").value(IsNull.nullValue()));
    }


    @Test
    void testListBeersByNameAndBeerStyle() throws Exception {
        mockMvc.perform(get(BeerController.BEER_PATH)
                .with(jwt)
                .queryParam("name", "ALE")
                .queryParam("beerStyle", BeerStyle.ALE.name())
                .queryParam("pageSize", "800")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
            ).andExpect(status().isOk())
            .andExpect(jsonPath("$.content.size()", is(251)));
    }


    @Test
    void testListBeersByBeerStyle() throws Exception {
        mockMvc.perform(get(BeerController.BEER_PATH)
                .with(jwt)
                .queryParam("beerStyle", BeerStyle.ALE.name())
                .queryParam("pageSize", "800")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
            ).andExpect(status().isOk())
            .andExpect(jsonPath("$.content.size()", is(400)));
    }

    @Test
    void testNoAuth() throws Exception {
        mockMvc.perform(get(BeerController.BEER_PATH)
                .queryParam("beerStyle", BeerStyle.ALE.name())
                .queryParam("pageSize", "800")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
            ).andExpect(status().isUnauthorized());
    }


    @Test
    void testListBeers() {
        Page<BeerDTO> beers = beerController.listBeers(null, null, false, 1, 1000);
        assertThat(beers.getContent().size()).isEqualTo(1000);
    }


    @Rollback
    @Transactional
    @Test
    void testEmptyList() {
        beerRepository.deleteAll();
        Page<BeerDTO> beers = beerController.listBeers(null, null, false, 1, 50);
        assertThat(beers.getContent().size()).isEqualTo(0);
    }
}