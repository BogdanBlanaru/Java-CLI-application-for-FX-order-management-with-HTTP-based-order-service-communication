package com.profidata.orderbook.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** Domain entity representing an FX order. */
public record Order(
    String id,
    String investmentCcy,
    Boolean buy,
    String counterCcy,
    BigDecimal limit,
    String validUntil) {
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

  public static Order createNew(
      String investmentCcy, boolean buy, String counterCcy, BigDecimal limit, String validUntil) {
    return new Order(null, investmentCcy, buy, counterCcy, limit, validUntil);
  }

  public CurrencyPair getCurrencyPair() {
    return new CurrencyPair(investmentCcy, counterCcy);
  }

  public boolean isBuy() {
    return Boolean.TRUE.equals(buy);
  }

  public boolean isSell() {
    return !isBuy();
  }

  public String getOrderType() {
    return isBuy() ? "buy" : "sell";
  }

  public BigDecimal calculateDistanceFromMarket(BigDecimal marketRate) {
    if (limit == null || marketRate == null) {
      return BigDecimal.ZERO;
    }
    return limit.subtract(marketRate).abs();
  }

  public boolean isValid() {
    try {
      LocalDate validDate = LocalDate.parse(validUntil, DATE_FORMATTER);
      return !validDate.isBefore(LocalDate.now());
    } catch (Exception e) {
      return false;
    }
  }
}
