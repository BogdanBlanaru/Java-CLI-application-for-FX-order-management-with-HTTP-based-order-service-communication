package com.profidata.orderbook.repository;

import com.profidata.orderbook.domain.CurrencyPair;
import com.profidata.orderbook.domain.FXRate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/** Repository interface for rate data access. */
public interface RateRepository {
  CompletableFuture<List<FXRate>> getCurrentRates();

  List<FXRate> getCurrentRatesSync();

  CompletableFuture<List<CurrencyPair>> getSupportedPairs();

  List<CurrencyPair> getSupportedPairsSync();
}
