package com.capisite.backend.modules.payments.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public record CreateDonationDTO(

        @NotBlank(message = "O nome é obrigatório")
        String name,

        @NotBlank(message = "O documento é obrigatório")
        String document,

        String email,

        @NotNull(message = "O valor é obrigatório")
        BigDecimal amount,

        @Pattern(regexp = "PIX|BOLETO|CREDIT_CARD|UNDEFINED", message = "Tipo de pagamento inválido")
        String billingType,

        String message,

        String clientIp,

        @Valid
        CreditCardDetailsDTO creditCardDetails

) {}