package com.example.practice.beerservicemvc.controller;

import com.example.practice.beerservicemvc.config.SecurityConfig;
import com.example.practice.beerservicemvc.model.CustomerDTO;
import com.example.practice.beerservicemvc.service.CustomerService;
import com.example.practice.beerservicemvc.service.impl.CustomerServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.MockMvc;

import static com.example.practice.beerservicemvc.controller.BeerControllerTest.jwt;
import static com.example.practice.beerservicemvc.controller.CustomerController.CUSTOMER_PATH;
import static com.example.practice.beerservicemvc.controller.CustomerController.CUSTOMER_PATH_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
@Import(SecurityConfig.class)
class CustomerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CustomerService customerService;

    @MockBean
    private JwtDecoder jwtDecoder;

    CustomerServiceImpl customerServiceImpl;

    @Captor
    ArgumentCaptor<UUID> uuidArgumentCaptor;

    @Captor
    ArgumentCaptor<CustomerDTO> customerArgumentCaptor;


    @BeforeEach
    void setUp() {
        customerServiceImpl = new CustomerServiceImpl();
    }


    @Test
    void getAllCustomers() throws Exception {
        given(customerService.findAll()).willReturn(customerServiceImpl.findAll());

        mockMvc.perform(get(CUSTOMER_PATH)
                .with(jwt)
                .accept(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.length()", is(3)));
    }


    @Test
    void getCustomerById() throws Exception {
        CustomerDTO customer = customerServiceImpl.findAll().get(0);
        given(customerService.findById(customer.getId())).willReturn(Optional.of(customer));

        mockMvc.perform(get(CUSTOMER_PATH_ID, customer.getId())
                .with(jwt)
                .accept(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.id", is(customer.getId().toString())))
            .andExpect(jsonPath("$.name", is(customer.getName())));
    }


    @Test
    void addCustomer() throws Exception {
        CustomerDTO customer = customerServiceImpl.findAll().get(0);
        customer.setId(null);
        customer.setVersion(null);

        given(customerService.addCustomer(any(CustomerDTO.class))).willReturn(customerServiceImpl.findAll().get(1));

        mockMvc.perform(post(CUSTOMER_PATH)
                .with(jwt)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer))
            ).andExpect(status().isCreated())
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(header().exists(HttpHeaders.LOCATION));
    }


    @Test
    void updateCustomer() throws Exception {
        CustomerDTO customer = customerServiceImpl.findAll().get(0);
        given(customerService.updateCustomer(any(), any())).willReturn(Optional.of(customer));

        mockMvc.perform(put(CUSTOMER_PATH_ID, customer.getId())
            .with(jwt)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(customer))
        ).andExpect(status().isNoContent());

        verify(customerService).updateCustomer(uuidArgumentCaptor.capture(), any(CustomerDTO.class));
        assertThat(customer.getId()).isEqualTo(uuidArgumentCaptor.getValue());
    }


    @Test
    void deleteById() throws Exception {
        CustomerDTO customer = customerServiceImpl.findAll().get(0);
        given(customerService.deleteById(any())).willReturn(true);

        mockMvc.perform(delete(CUSTOMER_PATH_ID, customer.getId())
            .with(jwt)
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
        ).andExpect(status().isNoContent());

        verify(customerService).deleteById(uuidArgumentCaptor.capture());

        assertThat(customer.getId()).isEqualTo(uuidArgumentCaptor.getValue());
    }


    @Test
    void patchCustomer() throws Exception {
        CustomerDTO customer = customerServiceImpl.findAll().get(0);
        given(customerService.patchCustomer(any(), any())).willReturn(Optional.of(customer));

        Map<String, String> customerMap = new HashMap<>();
        customerMap.put("name", "New name");

        mockMvc.perform(patch(CUSTOMER_PATH_ID, customer.getId())
            .with(jwt)
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(customerMap))
        ).andExpect(status().isNoContent());

        verify(customerService).patchCustomer(uuidArgumentCaptor.capture(), customerArgumentCaptor.capture());

        assertThat(customer.getId()).isEqualTo(uuidArgumentCaptor.getValue());
        assertThat(customerMap.get("name")).isEqualTo(customerArgumentCaptor.getValue().getName());
    }
}