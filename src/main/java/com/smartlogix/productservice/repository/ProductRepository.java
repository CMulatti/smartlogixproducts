package com.smartlogix.productservice.repository;

import com.smartlogix.productservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

//By xtending JpaRepository, Spring Data JPA gives us findAll(), findById(), save(), deleteById()

public interface ProductRepository extends JpaRepository<Product, Long> {
    // here we could add any extra methods, but we are fine with the ones that come with it so far.
}

