package com.capisite.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.capisite.backend.domain.products.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}