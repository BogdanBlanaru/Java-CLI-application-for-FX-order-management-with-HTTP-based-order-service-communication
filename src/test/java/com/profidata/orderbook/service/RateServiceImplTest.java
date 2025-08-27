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
    // Given
    var rates =
        List.of(
            new FXRate(
                new CurrencyPair("EUR", "USD"), new BigDecimal("1.19"), new BigDecimal("1.21")));
    when(rateRepository.getCurrentRatesSync()).thenReturn(rates);

    // When
    var result = rateService.getCurrentRates();

    // Then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).ccyPair().ccy1()).isEqualTo("EUR"); // Corectat aici
    verify(rateRepository).getCurrentRatesSync();
  }

  @Test
  void shouldGetCurrentRatesAsync() {
    // Given
    var rates =
        List.of(
            new FXRate(
                new CurrencyPair("EUR", "USD"), new BigDecimal("1.19"), new BigDecimal("1.21")));
    when(rateRepository.getCurrentRates()).thenReturn(CompletableFuture.completedFuture(rates));

    // When
    var resultFuture = rateService.getCurrentRatesAsync();

    // Then
    var result = resultFuture.join();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).ccyPair().ccy1()).isEqualTo("EUR"); // Corectat aici
    verify(rateRepository).getCurrentRates();
  }

  @Test
  void shouldGetSupportedPairs() {
    // Given
    var pairs = List.of(new CurrencyPair("EUR", "USD"));
    when(rateRepository.getSupportedPairsSync()).thenReturn(pairs);

    // When
    var result = rateService.getSupportedPairs();

    // Then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).ccy1()).isEqualTo("EUR"); // Corectat aici
    verify(rateRepository).getSupportedPairsSync();
  }

  @Test
  void shouldGetSupportedPairsAsync() {
    // Given
    var pairs = List.of(new CurrencyPair("EUR", "USD"));
    when(rateRepository.getSupportedPairs()).thenReturn(CompletableFuture.completedFuture(pairs));

    // When
    var resultFuture = rateService.getSupportedPairsAsync();

    // Then
    var result = resultFuture.join();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).ccy1()).isEqualTo("EUR"); // Corectat aici
    verify(rateRepository).getSupportedPairs();
  }

  @Test
  void shouldHandleEmptyRatesResponse() {
    // Given
    when(rateRepository.getCurrentRatesSync()).thenReturn(List.of());

    // When
    var result = rateService.getCurrentRates();

    // Then
    assertThat(result).isEmpty();
    verify(rateRepository).getCurrentRatesSync();
  }

  @Test
  void shouldHandleRepositoryException() {
    // Given
    when(rateRepository.getCurrentRatesSync())
        .thenThrow(new RuntimeException("Service unavailable"));

    // When & Then
    org.junit.jupiter.api.Assertions.assertThrows(
        RuntimeException.class,
        () -> {
          rateService.getCurrentRates();
        });
    verify(rateRepository).getCurrentRatesSync();
  }
}
