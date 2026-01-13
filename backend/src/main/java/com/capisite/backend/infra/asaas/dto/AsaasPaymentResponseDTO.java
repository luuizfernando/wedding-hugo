package com.capisite.backend.infra.asaas.dto;

public record AsaasPaymentResponseDTO(
    String id,
    String status,
    String invoiceUrl,
    String bankSlipUrl,
    String description
){}