package com.capisite.backend.modules.payments.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentResponseDTO(
    UUID id,
    String status,
    String paymentUrl,
    BigDecimal amount,
    String externalReference
) {}