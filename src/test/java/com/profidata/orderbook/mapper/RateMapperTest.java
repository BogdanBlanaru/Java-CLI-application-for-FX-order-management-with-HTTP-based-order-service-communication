package com.profidata.orderbook.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.profidata.orderbook.domain.CurrencyPair;
import com.profidata.orderbook.domain.FXRate;
import com.profidata.orderbook.dto.response.CurrencyPairResponse;
import com.profidata.orderbook.dto.response.FXRateResponse;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RateMapperTest {

  private RateMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new RateMapper();
  }

  @Test
  void shouldMapFXRateResponseToEntity() {
    // Given
    var response =
        new FXRateResponse(
            new CurrencyPairResponse("EUR", "USD"), new BigDecimal("1.19"), new BigDecimal("1.21"));

    // When
    var rate = mapper.fromApiResponse(response);

    // Then
    assertThat(rate.ccyPair().ccy1()).isEqualTo("EUR");
    assertThat(rate.ccyPair().ccy2()).isEqualTo("USD");
    assertThat(rate.bid()).isEqualTo(new BigDecimal("1.190000"));
    assertThat(rate.ask()).isEqualTo(new BigDecimal("1.210000"));
  }

  @Test
  void shouldMapFXRateToResponse() {
    // Given
    var rate =
        new FXRate(new CurrencyPair("EUR", "USD"), new BigDecimal("1.19"), new BigDecimal("1.21"));

    // When
    var response = mapper.toResponse(rate);

    // Then
    assertThat(response.ccyPair().ccy1()).isEqualTo("EUR");
    assertThat(response.ccyPair().ccy2()).isEqualTo("USD");
    assertThat(response.bid()).isEqualTo(new BigDecimal("1.190000"));
    assertThat(response.ask()).isEqualTo(new BigDecimal("1.210000"));
  }

  @Test
  void shouldMapCurrencyPairResponseToEntity() {
    // Given
    var response = new CurrencyPairResponse("EUR", "USD");

    // When
    var pair = mapper.currencyPairFromApiResponse(response);

    // Then
    assertThat(pair.ccy1()).isEqualTo("EUR");
    assertThat(pair.ccy2()).isEqualTo("USD");
  }

  @Test
  void shouldMapCurrencyPairToResponse() {
    // Given
    var pair = new CurrencyPair("EUR", "USD");

    // When
    var response = mapper.currencyPairToResponse(pair);

    // Then
    assertThat(response.ccy1()).isEqualTo("EUR");
    assertThat(response.ccy2()).isEqualTo("USD");
  }
}
