package com.example.practice.beerservicemvc.service.impl;

import com.example.practice.beerservicemvc.mapper.CustomerMapper;
import com.example.practice.beerservicemvc.model.CustomerDTO;
import com.example.practice.beerservicemvc.repository.CustomerRepository;
import com.example.practice.beerservicemvc.service.CustomerService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Primary
@Service
@RequiredArgsConstructor
public class CustomerServiceJPAImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;


    @Override
    public Optional<CustomerDTO> findById(UUID customerId) {
        return Optional.ofNullable(
            customerMapper.customerToCustomerDto(
                customerRepository.findById(customerId)
                    .orElse(null))
        );
    }


    @Override
    public List<CustomerDTO> findAll() {
        return customerRepository.findAll()
            .stream()
            .map(customerMapper::customerToCustomerDto)
            .collect(Collectors.toList());
    }


    @Override
    public CustomerDTO addCustomer(CustomerDTO customer) {
        return customerMapper.customerToCustomerDto(
            customerRepository.save(customerMapper.customerDtoToCustomer(customer))
        );
    }


    @Override
    public Optional<CustomerDTO> updateCustomer(UUID customerId, CustomerDTO customer) {
        AtomicReference<Optional<CustomerDTO>> atomicReference = new AtomicReference<>();
        customerRepository.findById(customerId).ifPresentOrElse(foundCustomer -> {
                foundCustomer.setName(customer.getName());
                atomicReference.set(
                    Optional.of(customerMapper.customerToCustomerDto(customerRepository.save(foundCustomer)))
                );
            }, () -> atomicReference.set(Optional.empty())
        );
        return atomicReference.get();
    }


    @Override
    public Boolean deleteById(UUID customerId) {
        if (customerRepository.existsById(customerId)) {
            customerRepository.deleteById(customerId);
            return true;
        }
        return false;
    }


    @Override
    public Optional<CustomerDTO> patchCustomer(UUID customerId, CustomerDTO customer) {
        AtomicReference<Optional<CustomerDTO>> atomicReference = new AtomicReference<>();
        customerRepository.findById(customerId).ifPresentOrElse(foundCustomer -> {
                if (StringUtils.hasText(customer.getName())) {
                    foundCustomer.setName(customer.getName());
                }
                atomicReference.set(
                    Optional.of(customerMapper.customerToCustomerDto(customerRepository.save(foundCustomer)))
                );
            }, () -> atomicReference.set(Optional.empty())
        );
        return atomicReference.get();
    }
}
