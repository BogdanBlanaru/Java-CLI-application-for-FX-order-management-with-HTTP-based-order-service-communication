package com.profidata.orderbook.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

/** Unit tests for Order domain entity. */
class OrderTest {

  @Test
  void shouldCreateValidOrder() {
    var order = Order.createNew("EUR", true, "USD", new BigDecimal("1.20"), "31.12.2025");

    assertThat(order.investmentCcy()).isEqualTo("EUR");
    assertThat(order.isBuy()).isTrue();
    assertThat(order.counterCcy()).isEqualTo("USD");
    assertThat(order.limit()).isEqualTo(new BigDecimal("1.20"));
    assertThat(order.validUntil()).isEqualTo("31.12.2025");
  }

  @Test
  void shouldValidateOrderDate() {
    var order = Order.createNew("EUR", true, "USD", new BigDecimal("1.20"), "31.12.2025");

    assertThat(order.isValid()).isTrue();
  }

  @Test
  void shouldCalculateDistanceFromMarket() {
    var order = Order.createNew("EUR", true, "USD", new BigDecimal("1.20"), "31.12.2025");

    var distance = order.calculateDistanceFromMarket(new BigDecimal("1.25"));
    assertThat(distance).isEqualTo(new BigDecimal("0.05"));
  }

  @Test
  void shouldIdentifyBuyOrder() {
    var buyOrder = Order.createNew("EUR", true, "USD", new BigDecimal("1.20"), "31.12.2025");

    assertThat(buyOrder.isBuy()).isTrue();
    assertThat(buyOrder.isSell()).isFalse();
    assertThat(buyOrder.getOrderType()).isEqualTo("buy");
  }

  @Test
  void shouldIdentifySellOrder() {
    var sellOrder = Order.createNew("EUR", false, "USD", new BigDecimal("1.20"), "31.12.2025");

    assertThat(sellOrder.isBuy()).isFalse();
    assertThat(sellOrder.isSell()).isTrue();
    assertThat(sellOrder.getOrderType()).isEqualTo("sell");
  }

  @Test
  void shouldCreateCurrencyPair() {
    var order = Order.createNew("EUR", true, "USD", new BigDecimal("1.20"), "31.12.2025");

    var pair = order.getCurrencyPair();
    assertThat(pair.ccy1()).isEqualTo("EUR");
    assertThat(pair.ccy2()).isEqualTo("USD");
  }
}
