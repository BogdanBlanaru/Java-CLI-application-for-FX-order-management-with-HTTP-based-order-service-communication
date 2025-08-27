package com.profidata.orderbook.mapper;

import com.profidata.orderbook.domain.CurrencyPair;
import com.profidata.orderbook.domain.FXRate;
import com.profidata.orderbook.dto.response.CurrencyPairResponse;
import com.profidata.orderbook.dto.response.FXRateResponse;
import org.springframework.stereotype.Component;

/** Mapper for FXRate and CurrencyPair entities and DTOs. */
@Component
public class RateMapper {

  public FXRate fromApiResponse(FXRateResponse response) {
    CurrencyPair pair = new CurrencyPair(response.ccyPair().ccy1(), response.ccyPair().ccy2());
    return new FXRate(pair, response.bid(), response.ask());
  }

  public FXRateResponse toResponse(FXRate rate) {
    CurrencyPairResponse pairResponse =
        new CurrencyPairResponse(rate.ccyPair().ccy1(), rate.ccyPair().ccy2());
    return new FXRateResponse(pairResponse, rate.bid(), rate.ask());
  }

  public CurrencyPair currencyPairFromApiResponse(CurrencyPairResponse response) {
    return new CurrencyPair(response.ccy1(), response.ccy2());
  }

  public CurrencyPairResponse currencyPairToResponse(CurrencyPair pair) {
    return new CurrencyPairResponse(pair.ccy1(), pair.ccy2());
  }
}
