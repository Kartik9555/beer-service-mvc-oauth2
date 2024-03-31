package com.example.practice.beerservicemvc.repository;

import com.example.practice.beerservicemvc.entities.Category;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
}
