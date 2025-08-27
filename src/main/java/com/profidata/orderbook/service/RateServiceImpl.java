package com.profidata.orderbook.service;

import com.profidata.orderbook.domain.CurrencyPair;
import com.profidata.orderbook.domain.FXRate;
import com.profidata.orderbook.repository.RateRepository;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** Implementation of RateService interface. */
@Service
public class RateServiceImpl implements RateService {

  private static final Logger LOGGER = LoggerFactory.getLogger(RateServiceImpl.class);

  private final RateRepository rateRepository;

  public RateServiceImpl(RateRepository rateRepository) {
    this.rateRepository = rateRepository;
  }

  @Override
  public CompletableFuture<List<FXRate>> getCurrentRatesAsync() {
    LOGGER.debug("Retrieving current FX rates asynchronously");

    return rateRepository
        .getCurrentRates()
        .whenComplete(
            (result, throwable) -> {
              if (throwable == null) {
                LOGGER.info("Successfully retrieved {} FX rates", result.size());
              } else {
                LOGGER.error("Failed to retrieve FX rates", throwable);
              }
            });
  }

  @Override
  public List<FXRate> getCurrentRates() {
    LOGGER.debug("Retrieving current FX rates synchronously");

    List<FXRate> rates = rateRepository.getCurrentRatesSync();
    LOGGER.info("Successfully retrieved {} FX rates", rates.size());
    return rates;
  }

  @Override
  public CompletableFuture<List<CurrencyPair>> getSupportedPairsAsync() {
    LOGGER.debug("Retrieving supported currency pairs asynchronously");

    return rateRepository
        .getSupportedPairs()
        .whenComplete(
            (result, throwable) -> {
              if (throwable == null) {
                LOGGER.info("Successfully retrieved {} supported currency pairs", result.size());
              } else {
                LOGGER.error("Failed to retrieve supported currency pairs", throwable);
              }
            });
  }

  @Override
  public List<CurrencyPair> getSupportedPairs() {
    LOGGER.debug("Retrieving supported currency pairs synchronously");

    List<CurrencyPair> pairs = rateRepository.getSupportedPairsSync();
    LOGGER.info("Successfully retrieved {} supported currency pairs", pairs.size());
    return pairs;
  }
}
