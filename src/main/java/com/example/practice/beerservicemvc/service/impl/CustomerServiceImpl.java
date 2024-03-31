package com.example.practice.beerservicemvc.service.impl;

import com.example.practice.beerservicemvc.model.CustomerDTO;
import com.example.practice.beerservicemvc.service.CustomerService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {

    private Map<UUID, CustomerDTO> customers;


    public CustomerServiceImpl() {
        customers = new HashMap<>();
        CustomerDTO customer1 = CustomerDTO.builder()
            .name("Max Muller")
            .id(UUID.randomUUID())
            .version(1)
            .email("max.muller@gmail.com")
            .createdDate(LocalDateTime.now())
            .lastModifiedDate(LocalDateTime.now())
            .build();

        CustomerDTO customer2 = CustomerDTO.builder()
            .name("Henry Ford")
            .id(UUID.randomUUID())
            .version(1)
            .email("henry.ford@yahoo.com")
            .createdDate(LocalDateTime.now())
            .lastModifiedDate(LocalDateTime.now())
            .build();

        CustomerDTO customer3 = CustomerDTO.builder()
            .name("Pam Hilton")
            .id(UUID.randomUUID())
            .version(1)
            .email("paris.hilton@gmail.com")
            .createdDate(LocalDateTime.now())
            .lastModifiedDate(LocalDateTime.now())
            .build();
        customers.put(customer1.getId(), customer1);
        customers.put(customer2.getId(), customer2);
        customers.put(customer3.getId(), customer3);
    }


    @Override
    public Optional<CustomerDTO> findById(UUID customerId) {
        log.debug("Find customers by ID - in service. Id: " + customerId.toString());
        return Optional.of(customers.get(customerId));
    }


    @Override
    public List<CustomerDTO> findAll() {
        log.debug("Find all customers - in service.");
        return new ArrayList<>(customers.values());
    }


    @Override
    public CustomerDTO addCustomer(CustomerDTO customer) {
        log.debug("Add new customer - in service.");
        CustomerDTO savedCustomer = CustomerDTO.builder()
            .name(customer.getName())
            .id(UUID.randomUUID())
            .version(1)
            .createdDate(LocalDateTime.now())
            .lastModifiedDate(LocalDateTime.now())
            .build();
        customers.put(savedCustomer.getId(), savedCustomer);
        return savedCustomer;
    }


    @Override
    public Optional<CustomerDTO> updateCustomer(UUID customerId, CustomerDTO customer) {
        log.debug("Update existing customer - in service.");
        CustomerDTO existing = customers.get(customerId);
        existing.setName(customer.getName());
        customers.put(existing.getId(), customer);
        return Optional.of(existing);
    }


    @Override
    public Boolean deleteById(UUID customerId) {
        log.debug("Delete existing customer - in service.");
        customers.remove(customerId);
        return true;
    }


    @Override
    public Optional<CustomerDTO> patchCustomer(UUID customerId, CustomerDTO customer) {
        log.debug("Patch existing customer - in service.");
        CustomerDTO existing = customers.get(customerId);
        if (StringUtils.hasText(customer.getName())) {
            existing.setName(customer.getName());
        }
        return Optional.of(existing);
    }
}
