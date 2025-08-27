package com.profidata.orderbook.domain;

import java.util.Objects;

/** Domain entity representing a currency pair. */
public record CurrencyPair(String ccy1, String ccy2) {

  public CurrencyPair {
    if (ccy1 != null) {
      ccy1 = ccy1.toUpperCase().trim();
    }
    if (ccy2 != null) {
      ccy2 = ccy2.toUpperCase().trim();
    }

    if (Objects.equals(ccy1, ccy2)) {
      throw new IllegalArgumentException(
          "Currency pair cannot have the same currency for both sides");
    }
  }

  public static CurrencyPair fromString(String pairString) {
    if (pairString == null || pairString.trim().isEmpty()) {
      throw new IllegalArgumentException("Currency pair string cannot be null or empty");
    }

    String normalized = pairString.trim().toUpperCase();

    if (normalized.contains("/")) {
      String[] parts = normalized.split("/");
      if (parts.length != 2) {
        throw new IllegalArgumentException("Invalid currency pair format: " + pairString);
      }
      return new CurrencyPair(parts[0].trim(), parts[1].trim());
    }

    if (normalized.length() == 6) {
      return new CurrencyPair(normalized.substring(0, 3), normalized.substring(3, 6));
    }

    throw new IllegalArgumentException("Invalid currency pair format: " + pairString);
  }

  public CurrencyPair inverse() {
    return new CurrencyPair(ccy2, ccy1);
  }

  public boolean contains(String currency) {
    if (currency == null) {
      return false;
    }
    String normalizedCurrency = currency.toUpperCase().trim();
    return ccy1.equals(normalizedCurrency) || ccy2.equals(normalizedCurrency);
  }

  public boolean matches(CurrencyPair other) {
    if (other == null) {
      return false;
    }
    return this.equals(other) || this.equals(other.inverse());
  }

  @Override
  public String toString() {
    return ccy1 + "/" + ccy2;
  }
}
