package com.profidata.orderbook.service;

import com.profidata.orderbook.domain.CurrencyPair;
import com.profidata.orderbook.domain.FXRate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/** Service interface for FX rate operations. */
public interface RateService {
  CompletableFuture<List<FXRate>> getCurrentRatesAsync();

  List<FXRate> getCurrentRates();

  CompletableFuture<List<CurrencyPair>> getSupportedPairsAsync();

  List<CurrencyPair> getSupportedPairs();
}
