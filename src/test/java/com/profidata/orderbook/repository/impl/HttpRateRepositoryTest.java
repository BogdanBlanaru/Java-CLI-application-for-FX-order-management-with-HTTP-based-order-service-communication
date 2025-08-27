package com.profidata.orderbook.repository.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.profidata.orderbook.client.OrderServiceClient;
import com.profidata.orderbook.domain.CurrencyPair;
import com.profidata.orderbook.domain.FXRate;
import com.profidata.orderbook.dto.response.CurrencyPairResponse;
import com.profidata.orderbook.dto.response.FXRateResponse;
import com.profidata.orderbook.mapper.RateMapper;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HttpRateRepositoryTest {

  @Mock private OrderServiceClient client;

  @Mock private RateMapper rateMapper;

  private HttpRateRepository repository;

  @BeforeEach
  void setUp() {
    repository = new HttpRateRepository(client, rateMapper);
  }

  @Test
  void shouldGetCurrentRatesSync() {
    // Given
    var rateResponses =
        List.of(
            new FXRateResponse(
                new CurrencyPairResponse("EUR", "USD"),
                new BigDecimal("1.19"),
                new BigDecimal("1.21")));
    var rates =
        List.of(
            new FXRate(
                new CurrencyPair("EUR", "USD"), new BigDecimal("1.19"), new BigDecimal("1.21")));

    when(client.getRateSnapshotSync()).thenReturn(rateResponses);
    when(rateMapper.fromApiResponse(any(FXRateResponse.class))).thenReturn(rates.get(0));

    // When
    var result = repository.getCurrentRatesSync();

    // Then
    assertThat(result).hasSize(1);
    verify(client).getRateSnapshotSync();
    verify(rateMapper).fromApiResponse(any(FXRateResponse.class));
  }

  @Test
  void shouldGetSupportedPairsSync() {
    // Given
    var pairResponses = List.of(new CurrencyPairResponse("EUR", "USD"));
    var pairs = List.of(new CurrencyPair("EUR", "USD"));

    when(client.getSupportedCurrencyPairsSync()).thenReturn(pairResponses);
    when(rateMapper.currencyPairFromApiResponse(any(CurrencyPairResponse.class)))
        .thenReturn(pairs.get(0));

    // When
    var result = repository.getSupportedPairsSync();

    // Then
    assertThat(result).hasSize(1);
    verify(client).getSupportedCurrencyPairsSync();
    verify(rateMapper).currencyPairFromApiResponse(any(CurrencyPairResponse.class));
  }
}
