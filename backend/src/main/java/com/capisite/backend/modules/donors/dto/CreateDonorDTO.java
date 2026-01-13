package com.capisite.backend.modules.donors.dto;

public record CreateDonorDTO(
    String name,
    String email,
    String document
) {}