package com.profidata.orderbook.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.profidata.orderbook.domain.Order;
import com.profidata.orderbook.repository.OrderRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

  @Mock private OrderRepository orderRepository;

  private OrderServiceImpl orderService;

  @BeforeEach
  void setUp() {
    orderService = new OrderServiceImpl(orderRepository);
  }

  @Test
  void shouldCreateOrderSuccessfully() {
    // Given
    var inputOrder = Order.createNew("EUR", true, "USD", new BigDecimal("1.20"), "31.12.2025");
    var createdOrder = new Order("123", "EUR", true, "USD", new BigDecimal("1.20"), "31.12.2025");

    when(orderRepository.saveSync(any(Order.class))).thenReturn(createdOrder);

    // When
    var result = orderService.createOrder(inputOrder);

    // Then
    assertThat(result.id()).isEqualTo("123");
    assertThat(result.investmentCcy()).isEqualTo("EUR");
    verify(orderRepository).saveSync(inputOrder);
  }

  @Test
  void shouldCreateOrderAsync() {
    // Given
    var inputOrder = Order.createNew("EUR", true, "USD", new BigDecimal("1.20"), "31.12.2025");
    var createdOrder = new Order("123", "EUR", true, "USD", new BigDecimal("1.20"), "31.12.2025");

    when(orderRepository.save(any(Order.class)))
        .thenReturn(CompletableFuture.completedFuture(createdOrder));

    // When
    var resultFuture = orderService.createOrderAsync(inputOrder);

    // Then
    var result = resultFuture.join();
    assertThat(result.id()).isEqualTo("123");
    verify(orderRepository).save(inputOrder);
  }

  @Test
  void shouldRejectOrderWithId() {
    // Given
    var orderWithId = new Order("123", "EUR", true, "USD", new BigDecimal("1.20"), "31.12.2025");

    // When & Then
    assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(orderWithId));
    verifyNoInteractions(orderRepository);
  }

  @Test
  void shouldRejectNullOrder() {
    // When & Then
    assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(null));
    verifyNoInteractions(orderRepository);
  }

  @Test
  void shouldRejectInvalidOrder() {
    // Given - an order with a date in the past is invalid
    var invalidOrder = Order.createNew("EUR", true, "USD", new BigDecimal("1.20"), "01.01.2020");

    // When & Then
    assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(invalidOrder));
    verifyNoInteractions(orderRepository);
  }

  @Test
  void shouldCancelOrderSuccessfully() {
    // Given
    when(orderRepository.deleteSync("123")).thenReturn(true);

    // When
    var result = orderService.cancelOrder("123");

    // Then
    assertThat(result).isTrue();
    verify(orderRepository).deleteSync("123");
  }

  @Test
  void shouldReturnFalseWhenCancelOrderFails() {
    // Given
    when(orderRepository.deleteSync("123")).thenReturn(false);

    // When
    var result = orderService.cancelOrder("123");

    // Then
    assertThat(result).isFalse();
    verify(orderRepository).deleteSync("123");
  }

  @Test
  void shouldCancelOrderAsync() {
    // Given
    when(orderRepository.delete("123")).thenReturn(CompletableFuture.completedFuture(true));

    // When
    var resultFuture = orderService.cancelOrderAsync("123");

    // Then
    var result = resultFuture.join();
    assertThat(result).isTrue();
    verify(orderRepository).delete("123");
  }

  @Test
  void shouldGetAllOrders() {
    // Given
    var orders =
        List.of(
            new Order("1", "EUR", true, "USD", new BigDecimal("1.20"), "31.12.2025"),
            new Order("2", "GBP", false, "USD", new BigDecimal("1.30"), "31.12.2025"));
    when(orderRepository.findAllSync()).thenReturn(orders);

    // When
    var result = orderService.getAllOrders();

    // Then
    assertThat(result).hasSize(2);
    assertThat(result.get(0).id()).isEqualTo("1");
    assertThat(result.get(1).id()).isEqualTo("2");
    verify(orderRepository).findAllSync();
  }

  @Test
  void shouldGetAllOrdersAsync() {
    // Given
    var orders = List.of(new Order("1", "EUR", true, "USD", new BigDecimal("1.20"), "31.12.2025"));
    when(orderRepository.findAll()).thenReturn(CompletableFuture.completedFuture(orders));

    // When
    var resultFuture = orderService.getAllOrdersAsync();

    // Then
    var result = resultFuture.join();
    assertThat(result).hasSize(1);
    verify(orderRepository).findAll();
  }

  @Test
  void shouldFindOrderById() {
    // Given
    var orders =
        List.of(
            new Order("1", "EUR", true, "USD", new BigDecimal("1.20"), "31.12.2025"),
            new Order("2", "GBP", false, "USD", new BigDecimal("1.30"), "31.12.2025"));
    when(orderRepository.findAllSync()).thenReturn(orders);

    // When
    var result = orderService.findOrderById("1");

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().id()).isEqualTo("1");
    assertThat(result.get().investmentCcy()).isEqualTo("EUR");
  }

  @Test
  void shouldReturnEmptyWhenOrderNotFound() {
    // Given
    var orders = List.of(new Order("1", "EUR", true, "USD", new BigDecimal("1.20"), "31.12.2025"));
    when(orderRepository.findAllSync()).thenReturn(orders);

    // When
    var result = orderService.findOrderById("999");

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void shouldHandleRepositoryExceptionInFindById() {
    // Given
    when(orderRepository.findAllSync()).thenThrow(new RuntimeException("Repository error"));

    // When
    var result = orderService.findOrderById("1");

    // Then
    assertThat(result).isEmpty();
    verify(orderRepository).findAllSync();
  }

  @Test
  void shouldReturnEmptyListWhenNoOrders() {
    // Given
    when(orderRepository.findAllSync()).thenReturn(List.of());

    // When
    var result = orderService.getAllOrders();

    // Then
    assertThat(result).isEmpty();
    verify(orderRepository).findAllSync();
  }
}
