package com.capisite.backend.infra.asaas.dto;

public record AsaasCustomerRequestDTO(
    String name,
    String cpfCnpj
) {}