package com.example.practice.beerservicemvc.repository;

import com.example.practice.beerservicemvc.entities.Customer;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
}
