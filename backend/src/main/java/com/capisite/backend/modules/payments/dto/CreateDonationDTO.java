package com.capisite.backend.modules.payments.dto;

import java.math.BigDecimal;

import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;

public record CreateDonationDTO(
    
    @NotNull(value = "O nome é obrigatório")
    String name,

    @NotNull(value = "O documento é obrigatório")
    String document,

    String email,

    @NotNull(value = "O valor é obrigatório")
    BigDecimal amount,

    @Pattern(value = "PIX|BOLETO|CREDIT_CARD")
    String billingType,

    String message
    
) {}