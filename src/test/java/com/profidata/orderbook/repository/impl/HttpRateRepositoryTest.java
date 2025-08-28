package com.profidata.orderbook.repository.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.profidata.orderbook.client.OrderServiceClient;
import com.profidata.orderbook.dto.response.CurrencyPairResponse;
import com.profidata.orderbook.dto.response.FXRateResponse;
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

  private HttpRateRepository repository;

  @BeforeEach
  void setUp() {
    repository = new HttpRateRepository(client);
  }

  @Test
  void shouldGetCurrentRatesSync() {
    var rateResponses =
        List.of(
            new FXRateResponse(
                new CurrencyPairResponse("EUR", "USD"),
                new BigDecimal("1.19"),
                new BigDecimal("1.21")));

    when(client.getRateSnapshotSync()).thenReturn(rateResponses);

    var result = repository.getCurrentRatesSync();

    assertThat(result).hasSize(1);
    verify(client).getRateSnapshotSync();
  }

  @Test
  void shouldGetSupportedPairsSync() {
    var pairResponses = List.of(new CurrencyPairResponse("EUR", "USD"));

    when(client.getSupportedCurrencyPairsSync()).thenReturn(pairResponses);

    var result = repository.getSupportedPairsSync();

    assertThat(result).hasSize(1);
    verify(client).getSupportedCurrencyPairsSync();
  }
}
