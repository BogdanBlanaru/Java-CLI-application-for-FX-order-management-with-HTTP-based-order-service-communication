package com.profidata.orderbook.repository.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.profidata.orderbook.client.OrderServiceClient;
import com.profidata.orderbook.domain.Order;
import com.profidata.orderbook.dto.response.OrderResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HttpOrderRepositoryTest {

  @Mock private OrderServiceClient client;

  private HttpOrderRepository repository;

  @BeforeEach
  void setUp() {
    repository = new HttpOrderRepository(client);
  }

  @Test
  void shouldSaveOrderSync() {
    var order = Order.createNew("EUR", true, "USD", new BigDecimal("1.20"), "31.12.2025");
    var orderResponse =
        new OrderResponse("123", "EUR", true, "USD", new BigDecimal("1.20"), "31.12.2025");

    when(client.createOrderSync(any())).thenReturn(orderResponse);

    var result = repository.saveSync(order);

    assertThat(result.id()).isEqualTo("123");
    verify(client).createOrderSync(any());
  }

  @Test
  void shouldSaveOrderAsync() {
    var order = Order.createNew("EUR", true, "USD", new BigDecimal("1.20"), "31.12.2025");
    var orderResponse =
        new OrderResponse("123", "EUR", true, "USD", new BigDecimal("1.20"), "31.12.2025");

    when(client.createOrderAsync(any()))
        .thenReturn(CompletableFuture.completedFuture(orderResponse));

    var resultFuture = repository.save(order);

    var result = resultFuture.join();
    assertThat(result.id()).isEqualTo("123");
    verify(client).createOrderAsync(any());
  }

  @Test
  void shouldDeleteOrderSync() {
    when(client.cancelOrderSync("123")).thenReturn(true);

    var result = repository.deleteSync("123");

    assertThat(result).isTrue();
    verify(client).cancelOrderSync("123");
  }

  @Test
  void shouldFindAllOrdersSync() {
    var orderResponses =
        List.of(new OrderResponse("1", "EUR", true, "USD", new BigDecimal("1.20"), "31.12.2025"));

    when(client.retrieveOrdersSync()).thenReturn(orderResponses);

    var result = repository.findAllSync();

    assertThat(result).hasSize(1);
    verify(client).retrieveOrdersSync();
  }
}
