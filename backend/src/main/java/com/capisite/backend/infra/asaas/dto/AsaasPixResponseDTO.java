package com.capisite.backend.infra.asaas.dto;

public record AsaasPixResponseDTO(
    String encodedImage,
    String payload,
    String expirationDate
) {}