package com.profidata.orderbook.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.profidata.orderbook.domain.CurrencyPair;
import com.profidata.orderbook.domain.FXRate;
import com.profidata.orderbook.repository.RateRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RateServiceImplTest {

  @Mock private RateRepository rateRepository;

  private RateServiceImpl rateService;

  @BeforeEach
  void setUp() {
    rateService = new RateServiceImpl(rateRepository);
  }

  @Test
  void shouldGetCurrentRates() {
    var rates =
        List.of(
            new FXRate(
                new CurrencyPair("EUR", "USD"), new BigDecimal("1.19"), new BigDecimal("1.21")));
    when(rateRepository.getCurrentRatesSync()).thenReturn(rates);

    var result = rateService.getCurrentRates();

    assertThat(result).hasSize(1);
    assertThat(result.get(0).ccyPair().ccy1()).isEqualTo("EUR");
    verify(rateRepository).getCurrentRatesSync();
  }

  @Test
  void shouldGetCurrentRatesAsync() {
    var rates =
        List.of(
            new FXRate(
                new CurrencyPair("EUR", "USD"), new BigDecimal("1.19"), new BigDecimal("1.21")));
    when(rateRepository.getCurrentRates()).thenReturn(CompletableFuture.completedFuture(rates));

    var resultFuture = rateService.getCurrentRatesAsync();

    var result = resultFuture.join();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).ccyPair().ccy1()).isEqualTo("EUR");
    verify(rateRepository).getCurrentRates();
  }

  @Test
  void shouldGetSupportedPairs() {
    var pairs = List.of(new CurrencyPair("EUR", "USD"));
    when(rateRepository.getSupportedPairsSync()).thenReturn(pairs);

    var result = rateService.getSupportedPairs();

    assertThat(result).hasSize(1);
    assertThat(result.get(0).ccy1()).isEqualTo("EUR");
    verify(rateRepository).getSupportedPairsSync();
  }

  @Test
  void shouldGetSupportedPairsAsync() {
    var pairs = List.of(new CurrencyPair("EUR", "USD"));
    when(rateRepository.getSupportedPairs()).thenReturn(CompletableFuture.completedFuture(pairs));

    var resultFuture = rateService.getSupportedPairsAsync();

    var result = resultFuture.join();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).ccy1()).isEqualTo("EUR");
    verify(rateRepository).getSupportedPairs();
  }

  @Test
  void shouldHandleEmptyRatesResponse() {
    when(rateRepository.getCurrentRatesSync()).thenReturn(List.of());

    var result = rateService.getCurrentRates();

    assertThat(result).isEmpty();
    verify(rateRepository).getCurrentRatesSync();
  }

  @Test
  void shouldHandleRepositoryException() {
    when(rateRepository.getCurrentRatesSync())
        .thenThrow(new RuntimeException("Service unavailable"));

    org.junit.jupiter.api.Assertions.assertThrows(
        RuntimeException.class,
        () -> {
          rateService.getCurrentRates();
        });
    verify(rateRepository).getCurrentRatesSync();
  }
}
