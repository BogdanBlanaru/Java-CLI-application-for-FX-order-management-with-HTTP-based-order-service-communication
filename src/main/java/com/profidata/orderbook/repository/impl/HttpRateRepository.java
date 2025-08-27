package com.profidata.orderbook.repository.impl;

import com.profidata.orderbook.client.OrderServiceClient;
import com.profidata.orderbook.domain.CurrencyPair;
import com.profidata.orderbook.domain.FXRate;
import com.profidata.orderbook.mapper.RateMapper;
import com.profidata.orderbook.repository.RateRepository;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

/** HTTP-based implementation of RateRepository. */
@Repository
public class HttpRateRepository implements RateRepository {

  private final OrderServiceClient client;
  private final RateMapper mapper;

  public HttpRateRepository(OrderServiceClient client, RateMapper mapper) {
    this.client = client;
    this.mapper = mapper;
  }

  @Override
  public CompletableFuture<List<FXRate>> getCurrentRates() {
    return client
        .getRateSnapshotAsync()
        .thenApply(
            responses ->
                responses.stream().map(mapper::fromApiResponse).collect(Collectors.toList()));
  }

  @Override
  public List<FXRate> getCurrentRatesSync() {
    return client.getRateSnapshotSync().stream()
        .map(mapper::fromApiResponse)
        .collect(Collectors.toList());
  }

  @Override
  public CompletableFuture<List<CurrencyPair>> getSupportedPairs() {
    return client
        .getSupportedCurrencyPairsAsync()
        .thenApply(
            responses ->
                responses.stream()
                    .map(mapper::currencyPairFromApiResponse)
                    .collect(Collectors.toList()));
  }

  @Override
  public List<CurrencyPair> getSupportedPairsSync() {
    return client.getSupportedCurrencyPairsSync().stream()
        .map(mapper::currencyPairFromApiResponse)
        .collect(Collectors.toList());
  }
}
