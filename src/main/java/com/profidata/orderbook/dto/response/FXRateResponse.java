package com.profidata.orderbook.dto.response;

import java.math.BigDecimal;

/** DTO for FX rate responses. */
public record FXRateResponse(CurrencyPairResponse ccyPair, BigDecimal bid, BigDecimal ask) {}
