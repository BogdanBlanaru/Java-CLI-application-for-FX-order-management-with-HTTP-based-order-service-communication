package com.profidata.orderbook.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Unit tests for CurrencyPair domain entity. */
class CurrencyPairTest {

  @Test
  void shouldCreateValidCurrencyPair() {
    var pair = new CurrencyPair("EUR", "USD");

    assertThat(pair.ccy1()).isEqualTo("EUR");
    assertThat(pair.ccy2()).isEqualTo("USD");
    assertThat(pair.toString()).isEqualTo("EUR/USD");
  }

  @Test
  void shouldCreateFromSlashString() {
    var pair = CurrencyPair.fromString("EUR/USD");

    assertThat(pair).isEqualTo(new CurrencyPair("EUR", "USD"));
  }

  @Test
  void shouldCreateFromConcatString() {
    var pair = CurrencyPair.fromString("EURUSD");

    assertThat(pair).isEqualTo(new CurrencyPair("EUR", "USD"));
  }

  @Test
  void shouldCreateInverse() {
    var pair = new CurrencyPair("EUR", "USD");
    var inverse = pair.inverse();

    assertThat(inverse.ccy1()).isEqualTo("USD");
    assertThat(inverse.ccy2()).isEqualTo("EUR");
  }

  @Test
  void shouldRejectSameCurrency() {
    assertThrows(IllegalArgumentException.class, () -> new CurrencyPair("EUR", "EUR"));
  }

  @Test
  void shouldCheckContainsCurrency() {
    var pair = new CurrencyPair("EUR", "USD");

    assertThat(pair.contains("EUR")).isTrue();
    assertThat(pair.contains("USD")).isTrue();
    assertThat(pair.contains("GBP")).isFalse();
    assertThat(pair.contains(null)).isFalse();
  }

  @Test
  void shouldMatchWithInverse() {
    var pair1 = new CurrencyPair("EUR", "USD");
    var pair2 = new CurrencyPair("USD", "EUR");

    assertThat(pair1.matches(pair2)).isTrue();
    assertThat(pair2.matches(pair1)).isTrue();
  }

  @Test
  void shouldRejectInvalidFromString() {
    assertThrows(IllegalArgumentException.class, () -> CurrencyPair.fromString("INVALID"));
    assertThrows(IllegalArgumentException.class, () -> CurrencyPair.fromString("EUR/USD/GBP"));
    assertThrows(IllegalArgumentException.class, () -> CurrencyPair.fromString(""));
    assertThrows(IllegalArgumentException.class, () -> CurrencyPair.fromString(null));
  }
}
