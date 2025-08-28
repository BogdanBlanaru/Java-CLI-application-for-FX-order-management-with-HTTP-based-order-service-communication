package com.profidata.orderbook.mapper;

import com.profidata.orderbook.domain.CurrencyPair;
import com.profidata.orderbook.domain.FXRate;
import com.profidata.orderbook.dto.response.CurrencyPairResponse;
import com.profidata.orderbook.dto.response.FXRateResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RateMapper {
  RateMapper INSTANCE = Mappers.getMapper(RateMapper.class);

  FXRate fromApiResponse(FXRateResponse response);

  CurrencyPair currencyPairFromApiResponse(CurrencyPairResponse response);
}
