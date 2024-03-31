package com.example.practice.beerservicemvc.controller;

import com.example.practice.beerservicemvc.entities.Customer;
import com.example.practice.beerservicemvc.exception.NotFoundException;
import com.example.practice.beerservicemvc.mapper.CustomerMapper;
import com.example.practice.beerservicemvc.model.CustomerDTO;
import com.example.practice.beerservicemvc.repository.CustomerRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class CustomerControllerIT {
    @Autowired
    CustomerController customerController;

    @Autowired
    CustomerMapper customerMapper;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Rollback
    @Transactional
    @Test
    void testPatchCustomerIdNotFound() {
        assertThrows(NotFoundException.class, () -> customerController.patchCustomer(UUID.randomUUID(), CustomerDTO.builder().build()));
    }


    @Rollback
    @Transactional
    @Test
    void testPatchCustomer() {
        Customer customer = customerRepository.findAll().get(0);
        CustomerDTO dto = customerMapper.customerToCustomerDto(customer);
        dto.setId(null);
        dto.setVersion(null);
        String newName = "New Name";
        dto.setName(newName);
        ResponseEntity<HttpStatus> responseEntity = customerController.patchCustomer(customer.getId(), dto);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));

        Customer savedCustomer = customerRepository.findById(customer.getId()).get();
        assertThat(savedCustomer.getName()).isEqualTo(newName);

    }


    @Test
    void testDeleteByIdNotFound() {
        assertThrows(NotFoundException.class, () -> customerController.deleteById(UUID.randomUUID()));
    }


    @Rollback
    @Transactional
    @Test
    void testDeleteById() {
        Customer customer = customerRepository.findAll().get(0);
        ResponseEntity<HttpStatus> responseEntity = customerController.deleteById(customer.getId());
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));
        assertThat(customerRepository.findById(customer.getId())).isEmpty();
    }


    @Rollback
    @Transactional
    @Test
    void testUpdateCustomerIdNotFound() {
        assertThrows(NotFoundException.class, () -> customerController.updateCustomer(UUID.randomUUID(), CustomerDTO.builder().build()));
    }


    @Rollback
    @Transactional
    @Test
    void testUpdateCustomer() {
        Customer customer = customerRepository.findAll().get(0);
        CustomerDTO dto = customerMapper.customerToCustomerDto(customer);
        dto.setId(null);
        dto.setVersion(null);
        String newName = "New Name";
        dto.setName(newName);
        ResponseEntity<HttpStatus> responseEntity = customerController.updateCustomer(customer.getId(), dto);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));

        Customer savedCustomer = customerRepository.findById(customer.getId()).get();
        assertThat(savedCustomer.getName()).isEqualTo(newName);

    }


    @Rollback
    @Transactional
    @Test
    void testSaveCustomer() {
        CustomerDTO customerDTO = CustomerDTO.builder()
            .name("Test Name")
            .build();
        ResponseEntity<CustomerDTO> responseEntity = customerController.addCustomer(customerDTO);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));
        assertThat(responseEntity.getHeaders().getLocation()).isNotNull();
        String[] locationUUID = responseEntity.getHeaders().getLocation().getPath().split("/");
        UUID savedUUID = UUID.fromString(locationUUID[4]);

        Customer customer = customerRepository.findById(savedUUID).get();
        assertThat(customer).isNotNull();
    }


    @Autowired
    CustomerRepository customerRepository;


    @Test
    void testGetAllCustomers() {
        List<CustomerDTO> customers = customerController.getAllCustomers();
        assertThat(customers.size()).isEqualTo(3);
    }


    @Rollback
    @Transactional
    @Test
    void testEmptyList() {
        customerRepository.deleteAll();
        List<CustomerDTO> customers = customerController.getAllCustomers();
        assertThat(customers.size()).isEqualTo(0);
    }


    @Test
    void testGetCustomerById() {
        Customer customer = customerRepository.findAll().get(0);
        CustomerDTO dto = customerController.getCustomerById(customer.getId());
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(customer.getId());
    }


    @Test
    void testCustomerIdNotFound() {
        assertThrows(NotFoundException.class, () -> customerController.getCustomerById(UUID.randomUUID()));
    }
}