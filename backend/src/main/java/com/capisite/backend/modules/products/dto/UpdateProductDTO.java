package com.capisite.backend.modules.products.dto;

public record UpdateProductDTO(
    String name,
    String description,
    Double price,
    String image
) {}