package com.capisite.backend.modules.gifts.dto;

public record UpdateGiftDTO(
    String name,
    String description,
    Double price,
    String image
) {}