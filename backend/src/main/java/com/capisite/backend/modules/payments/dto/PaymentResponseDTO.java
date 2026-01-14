package com.capisite.backend.modules.payments.dto;

import com.capisite.backend.modules.payments.Payment;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentResponseDTO(
    UUID id,
    String status,
    String paymentUrl,
    BigDecimal amount,
    String externalReference
)

{
    public static PaymentResponseDTO from(Payment payment) {
        return new PaymentResponseDTO(
                payment.getId(),
                payment.getStatus().toString(),
                payment.getPaymentUrl(),
                payment.getAmount(),
                payment.getExternalReference()
        );
    }
}