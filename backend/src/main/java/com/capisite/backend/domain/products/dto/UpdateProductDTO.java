package com.capisite.backend.domain.products.dto;

public record UpdateProductDTO(
    String name,
    String description,
    Double price,
    String image
) {}