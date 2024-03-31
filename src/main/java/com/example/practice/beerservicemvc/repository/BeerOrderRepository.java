package com.example.practice.beerservicemvc.repository;

import com.example.practice.beerservicemvc.entities.BeerOrder;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeerOrderRepository extends JpaRepository<BeerOrder, UUID> {
}
