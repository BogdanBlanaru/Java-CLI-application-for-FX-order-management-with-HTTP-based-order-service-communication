package com.profidata.orderbook.repository.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.profidata.orderbook.client.OrderServiceClient;
import com.profidata.orderbook.domain.Order;
import com.profidata.orderbook.dto.response.OrderResponse;
import com.profidata.orderbook.mapper.OrderMapper;
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

  @Mock private OrderMapper orderMapper;

  private HttpOrderRepository repository;

  @BeforeEach
  void setUp() {
    repository = new HttpOrderRepository(client, orderMapper);
  }

  @Test
  void shouldSaveOrderSync() {
    // Given
    var order = Order.createNew("EUR", true, "USD", new BigDecimal("1.20"), "31.12.2025");
    var orderResponse =
        new OrderResponse("123", "EUR", true, "USD", new BigDecimal("1.20"), "31.12.2025");
    var createdOrder = new Order("123", "EUR", true, "USD", new BigDecimal("1.20"), "31.12.2025");

    when(orderMapper.toResponse(order)).thenReturn(orderResponse);
    when(client.createOrderSync(orderResponse)).thenReturn(orderResponse);
    when(orderMapper.fromApiResponse(orderResponse)).thenReturn(createdOrder);

    // When
    var result = repository.saveSync(order);

    // Then
    assertThat(result.id()).isEqualTo("123");
    verify(client).createOrderSync(orderResponse);
    verify(orderMapper).toResponse(order);
    verify(orderMapper).fromApiResponse(orderResponse);
  }

  @Test
  void shouldSaveOrderAsync() {
    // Given
    var order = Order.createNew("EUR", true, "USD", new BigDecimal("1.20"), "31.12.2025");
    var orderResponse =
        new OrderResponse("123", "EUR", true, "USD", new BigDecimal("1.20"), "31.12.2025");
    var createdOrder = new Order("123", "EUR", true, "USD", new BigDecimal("1.20"), "31.12.2025");

    when(orderMapper.toResponse(order)).thenReturn(orderResponse);
    when(client.createOrderAsync(orderResponse))
        .thenReturn(CompletableFuture.completedFuture(orderResponse));
    when(orderMapper.fromApiResponse(orderResponse)).thenReturn(createdOrder);

    // When
    var resultFuture = repository.save(order);

    // Then
    var result = resultFuture.join();
    assertThat(result.id()).isEqualTo("123");
    verify(client).createOrderAsync(orderResponse);
  }

  @Test
  void shouldDeleteOrderSync() {
    // Given
    when(client.cancelOrderSync("123")).thenReturn(true);

    // When
    var result = repository.deleteSync("123");

    // Then
    assertThat(result).isTrue();
    verify(client).cancelOrderSync("123");
  }

  @Test
  void shouldFindAllOrdersSync() {
    // Given
    var orderResponses =
        List.of(new OrderResponse("1", "EUR", true, "USD", new BigDecimal("1.20"), "31.12.2025"));
    var orders = List.of(new Order("1", "EUR", true, "USD", new BigDecimal("1.20"), "31.12.2025"));

    when(client.retrieveOrdersSync()).thenReturn(orderResponses);
    when(orderMapper.fromApiResponse(any(OrderResponse.class))).thenReturn(orders.get(0));

    // When
    var result = repository.findAllSync();

    // Then
    assertThat(result).hasSize(1);
    verify(client).retrieveOrdersSync();
    verify(orderMapper).fromApiResponse(any(OrderResponse.class));
  }
}
