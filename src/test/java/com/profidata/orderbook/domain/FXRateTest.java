package com.profidata.orderbook.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

/** Unit tests for FXRate domain entity. */
class FXRateTest {

  @Test
  void shouldCreateValidFXRate() {
    var pair = new CurrencyPair("EUR", "USD");
    var rate = new FXRate(pair, new BigDecimal("1.19"), new BigDecimal("1.21"));

    assertThat(rate.ccyPair()).isEqualTo(pair);
    assertThat(rate.bid()).isEqualTo(new BigDecimal("1.190000"));
    assertThat(rate.ask()).isEqualTo(new BigDecimal("1.210000"));
  }

  @Test
  void shouldCalculateMidPrice() {
    var pair = new CurrencyPair("EUR", "USD");
    var rate = new FXRate(pair, new BigDecimal("1.19"), new BigDecimal("1.21"));

    assertThat(rate.getMidPrice()).isEqualTo(new BigDecimal("1.200000"));
  }

  @Test
  void shouldCalculateSpread() {
    var pair = new CurrencyPair("EUR", "USD");
    var rate = new FXRate(pair, new BigDecimal("1.19"), new BigDecimal("1.21"));

    assertThat(rate.getSpread()).isEqualTo(new BigDecimal("0.020000"));
  }

  @Test
  void shouldCalculateSpreadPercentage() {
    var pair = new CurrencyPair("EUR", "USD");
    var rate = new FXRate(pair, new BigDecimal("1.19"), new BigDecimal("1.21"));

    var spreadPct = rate.getSpreadPercentage();
    assertThat(spreadPct.doubleValue())
        .isCloseTo(1.6667, org.assertj.core.data.Offset.offset(0.001));
  }

  @Test
  void shouldRejectInvalidBidAsk() {
    var pair = new CurrencyPair("EUR", "USD");

    assertThrows(
        IllegalArgumentException.class,
        () -> new FXRate(pair, new BigDecimal("1.21"), new BigDecimal("1.19")));
  }

  @Test
  void shouldGetRateForBuyOrder() {
    var pair = new CurrencyPair("EUR", "USD");
    var rate = new FXRate(pair, new BigDecimal("1.19"), new BigDecimal("1.21"));

    assertThat(rate.getRateForOrder(true)).isEqualTo(new BigDecimal("1.210000"));
  }

  @Test
  void shouldGetRateForSellOrder() {
    var pair = new CurrencyPair("EUR", "USD");
    var rate = new FXRate(pair, new BigDecimal("1.19"), new BigDecimal("1.21"));

    assertThat(rate.getRateForOrder(false)).isEqualTo(new BigDecimal("1.190000"));
  }

  @Test
  void shouldCreateInverseRate() {
    var pair = new CurrencyPair("EUR", "USD");
    var rate = new FXRate(pair, new BigDecimal("1.20"), new BigDecimal("1.22"));
    var inverse = rate.inverse();

    assertThat(inverse.ccyPair()).isEqualTo(pair.inverse());
    assertThat(inverse.bid().doubleValue())
        .isCloseTo(0.8197, org.assertj.core.data.Offset.offset(0.001));
    assertThat(inverse.ask().doubleValue())
        .isCloseTo(0.8333, org.assertj.core.data.Offset.offset(0.001));
  }

  @Test
  void shouldValidateRate() {
    var pair = new CurrencyPair("EUR", "USD");
    var validRate = new FXRate(pair, new BigDecimal("1.19"), new BigDecimal("1.21"));

    assertThat(validRate.isValid()).isTrue();
  }

  @Test
  void shouldMatchCurrencyPair() {
    var pair1 = new CurrencyPair("EUR", "USD");
    var pair2 = new CurrencyPair("USD", "EUR");
    var rate = new FXRate(pair1, new BigDecimal("1.19"), new BigDecimal("1.21"));

    assertThat(rate.matchesCurrencyPair(pair1)).isTrue();
    assertThat(rate.matchesCurrencyPair(pair2)).isTrue();
  }
}
