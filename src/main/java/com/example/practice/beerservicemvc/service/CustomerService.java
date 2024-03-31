package com.example.practice.beerservicemvc.service;

import com.example.practice.beerservicemvc.model.CustomerDTO;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerService {

    Optional<CustomerDTO> findById(UUID customerId);

    List<CustomerDTO> findAll();

    CustomerDTO addCustomer(CustomerDTO customer);

    Optional<CustomerDTO> updateCustomer(UUID customerId, CustomerDTO customer);

    Boolean deleteById(UUID customerId);

    Optional<CustomerDTO> patchCustomer(UUID customerId, CustomerDTO customer);
}
