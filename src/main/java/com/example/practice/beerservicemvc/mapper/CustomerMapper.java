package com.example.practice.beerservicemvc.mapper;

import com.example.practice.beerservicemvc.entities.Customer;
import com.example.practice.beerservicemvc.model.CustomerDTO;
import org.mapstruct.Mapper;

@Mapper
public interface CustomerMapper {
    Customer customerDtoToCustomer(CustomerDTO dto);

    CustomerDTO customerToCustomerDto(Customer customer);
}
