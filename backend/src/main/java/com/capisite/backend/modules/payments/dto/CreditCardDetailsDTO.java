package com.capisite.backend.modules.payments.dto;

public record CreditCardDetailsDTO(
        String holderName,
        String number,
        String expiryMonth,
        String expiryYear,
        String ccv
) {}