package com.profidata.orderbook.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

/** DTO for order creation requests. */
public record CreateOrderRequest(
    @NotBlank String investmentCcy,
    @NotNull Boolean buy,
    @NotBlank String counterCcy,
    @Positive BigDecimal limit,
    @NotBlank String validUntil) {}
