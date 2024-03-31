package com.example.practice.beerservicemvc.controller;

import com.example.practice.beerservicemvc.config.SecurityConfig;
import com.example.practice.beerservicemvc.model.BeerDTO;
import com.example.practice.beerservicemvc.service.BeerService;
import com.example.practice.beerservicemvc.service.impl.BeerServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static com.example.practice.beerservicemvc.controller.BeerController.BEER_PATH;
import static com.example.practice.beerservicemvc.controller.BeerController.BEER_PATH_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BeerController.class)
@Import(SecurityConfig.class)
class BeerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    BeerService beerService;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Captor
    ArgumentCaptor<UUID> uuidArgumentCaptor;

    @Captor
    ArgumentCaptor<BeerDTO> beerArgumentCaptor;

    BeerServiceImpl beerServiceImpl;

    public static final SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwt = jwt().jwt(jwt -> {
        jwt.claims(claim -> {
                claim.put("scope", "message-read");
                claim.put("scope", "message-write");
            })
            .subject("oidc-client")
            .notBefore(Instant.now().minusSeconds(5L));
    });


    @BeforeEach
    void setUp() {
        beerServiceImpl = new BeerServiceImpl();
    }


    @Test
    void getBeerById() throws Exception {
        BeerDTO beer = beerServiceImpl.listBeers(null, null, false, 1, 25).getContent().get(0);

        given(beerService.getBeerById(beer.getId())).willReturn(Optional.of(beer));

        mockMvc.perform(get(BEER_PATH_ID, beer.getId())
                .with(jwt)
                .accept(APPLICATION_JSON)
            ).andExpect(status().isOk())
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.id", is(beer.getId().toString())))
            .andExpect(jsonPath("$.name", is(beer.getName())));

    }


    @Test
    void getBeerByIdWhenNotFound() throws Exception {
        BeerDTO beer = beerServiceImpl.listBeers(null, null, false, 1, 25).getContent().get(0);
        given(beerService.getBeerById(any(UUID.class))).willReturn(Optional.empty());

        mockMvc.perform(get(BEER_PATH_ID, beer.getId())
            .with(jwt)
            .accept(APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }


    @Test
    void listBeers() throws Exception {
        given(beerService.listBeers(any(), any(), any(), any(), any())).willReturn(beerServiceImpl.listBeers(null, null, false, 1, 25));

        mockMvc.perform(get(BEER_PATH)
                .with(jwt)
                .accept(APPLICATION_JSON)
            ).andExpect(status().isOk())
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.content.length()", is(3)));
    }


    @Test
    void addBeerNullBeerName() throws Exception {
        BeerDTO beer = BeerDTO.builder().build();

        given(beerService.addBeer(any(BeerDTO.class))).willReturn(beerServiceImpl.listBeers(null, null, false, 1, 25).getContent().get(1));

        MvcResult mvcResult = mockMvc.perform(post(BEER_PATH)
                .with(jwt)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(beer))
            ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.length()", is(6)))
            .andReturn();

        System.out.println(mvcResult.getResponse().getContentAsString());
    }


    @Test
    void addBeer() throws Exception {
        BeerDTO beer = beerServiceImpl.listBeers(null, null, false, 1, 25).getContent().get(0);
        beer.setId(null);
        beer.setVersion(null);

        given(beerService.addBeer(any(BeerDTO.class))).willReturn(beerServiceImpl.listBeers(null, null, false, 1, 25).getContent().get(1));

        mockMvc.perform(post(BEER_PATH)
                .with(jwt)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(beer))
            ).andExpect(status().isCreated())
            .andExpect(header().exists(HttpHeaders.LOCATION));
    }


    @Test
    void updateBeerEmptyBeerName() throws Exception {
        BeerDTO beer = beerServiceImpl.listBeers(null, null, false, 1, 25).getContent().get(0);
        beer.setName("");

        given(beerService.updateBeer(any(), any())).willReturn(Optional.of(beer));
        mockMvc.perform(put(BEER_PATH_ID, beer.getId())
                .with(jwt)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(beer))
            ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.length()", is(1)));

    }


    @Test
    void updateBeer() throws Exception {
        BeerDTO beer = beerServiceImpl.listBeers(null, null, false, 1, 25).getContent().get(0);

        given(beerService.updateBeer(any(), any())).willReturn(Optional.of(beer));
        mockMvc.perform(put(BEER_PATH_ID, beer.getId())
            .with(jwt)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(beer))
        ).andExpect(status().isNoContent());

        verify(beerService).updateBeer(any(UUID.class), any(BeerDTO.class));
    }


    @Test
    void deleteById() throws Exception {
        BeerDTO beer = beerServiceImpl.listBeers(null, null, false, 1, 25).getContent().get(0);
        given(beerService.deleteById(any())).willReturn(true);

        mockMvc.perform(delete(BEER_PATH_ID, beer.getId())
            .with(jwt)
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
        ).andExpect(status().isNoContent());

        verify(beerService).deleteById(uuidArgumentCaptor.capture());

        assertThat(beer.getId()).isEqualTo(uuidArgumentCaptor.getValue());
    }


    @Test
    void patchBeer() throws Exception {
        BeerDTO beer = beerServiceImpl.listBeers(null, null, false, 1, 25).getContent().get(0);
        given(beerService.patchBeer(any(), any())).willReturn(Optional.of(beer));

        Map<String, String> bearMap = new HashMap<>();
        bearMap.put("name", "New name");

        mockMvc.perform(patch(BEER_PATH_ID, beer.getId())
            .with(jwt)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(bearMap))
        ).andExpect(status().isNoContent());

        verify(beerService).patchBeer(uuidArgumentCaptor.capture(), beerArgumentCaptor.capture());

        assertThat(beer.getId()).isEqualTo(uuidArgumentCaptor.getValue());
        assertThat(bearMap.get("name")).isEqualTo(beerArgumentCaptor.getValue().getName());
    }
}