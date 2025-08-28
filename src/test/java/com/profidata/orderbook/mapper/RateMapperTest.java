package com.profidata.orderbook.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.profidata.orderbook.dto.response.CurrencyPairResponse;
import com.profidata.orderbook.dto.response.FXRateResponse;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class RateMapperTest {

  private final RateMapper mapper = RateMapper.INSTANCE;

  @Test
  void shouldMapFXRateResponseToEntity() {
    var response =
        new FXRateResponse(
            new CurrencyPairResponse("EUR", "USD"), new BigDecimal("1.19"), new BigDecimal("1.21"));

    var rate = mapper.fromApiResponse(response);

    assertThat(rate.ccyPair().ccy1()).isEqualTo("EUR");
    assertThat(rate.ccyPair().ccy2()).isEqualTo("USD");
    assertThat(rate.bid()).isEqualTo(new BigDecimal("1.190000"));
    assertThat(rate.ask()).isEqualTo(new BigDecimal("1.210000"));
  }

  @Test
  void shouldMapCurrencyPairResponseToEntity() {
    var response = new CurrencyPairResponse("EUR", "USD");

    var pair = mapper.currencyPairFromApiResponse(response);

    assertThat(pair.ccy1()).isEqualTo("EUR");
    assertThat(pair.ccy2()).isEqualTo("USD");
  }
}
