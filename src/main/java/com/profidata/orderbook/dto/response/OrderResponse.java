package com.profidata.orderbook.dto.response;

import java.math.BigDecimal;

/** DTO for order responses. */
public record OrderResponse(
    String id,
    String investmentCcy,
    Boolean buy,
    String counterCcy,
    BigDecimal limit,
    String validUntil) {}
