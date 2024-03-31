package com.example.practice.beerservicemvc.controller;

import com.example.practice.beerservicemvc.exception.NotFoundException;
import com.example.practice.beerservicemvc.model.CustomerDTO;
import com.example.practice.beerservicemvc.service.CustomerService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class CustomerController {
    static final String CUSTOMER_PATH = "/api/v1/customer";
    static final String CUSTOMER_PATH_ID = CUSTOMER_PATH + "/{customerId}";

    private final CustomerService customerService;


    @GetMapping(CUSTOMER_PATH)
    public List<CustomerDTO> getAllCustomers() {
        log.debug("Find all customers - in controller.");
        return customerService.findAll();
    }


    @GetMapping(CUSTOMER_PATH_ID)
    public CustomerDTO getCustomerById(@PathVariable("customerId") UUID customerId) {
        log.debug("Find customers by ID - in controller.");
        return customerService.findById(customerId).orElseThrow(NotFoundException::new);
    }


    @PostMapping(CUSTOMER_PATH)
    public ResponseEntity<CustomerDTO> addCustomer(@RequestBody CustomerDTO customer) {
        log.debug("Add customers - in controller.");
        CustomerDTO savedCustomer = customerService.addCustomer(customer);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, CUSTOMER_PATH + "/" + savedCustomer.getId());
        return new ResponseEntity<>(savedCustomer, headers, HttpStatus.CREATED);
    }


    @PutMapping(CUSTOMER_PATH_ID)
    public ResponseEntity<HttpStatus> updateCustomer(@PathVariable("customerId") UUID customerId, @RequestBody CustomerDTO customer) {
        if (customerService.updateCustomer(customerId, customer).isEmpty()) {
            throw new NotFoundException();
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @DeleteMapping(CUSTOMER_PATH_ID)
    public ResponseEntity<HttpStatus> deleteById(@PathVariable("customerId") UUID customerId) {
        if (!customerService.deleteById(customerId)) {
            throw new NotFoundException();
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @PatchMapping(CUSTOMER_PATH_ID)
    public ResponseEntity<HttpStatus> patchCustomer(@PathVariable("customerId") UUID customerId, @RequestBody CustomerDTO customer) {
        if (customerService.patchCustomer(customerId, customer).isEmpty()) {
            throw new NotFoundException();
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
