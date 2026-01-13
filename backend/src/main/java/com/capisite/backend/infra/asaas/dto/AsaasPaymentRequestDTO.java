package com.capisite.backend.infra.asaas.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AsaasPaymentRequestDTO(
    String customer,
    String billingType,
    BigDecimal value,
    LocalDate dueDate,
    String description
) {}