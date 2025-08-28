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
    var inputOrder = Order.createNew("EUR", true, "USD", new BigDecimal("1.20"), "31.12.2025");
    var createdOrder = new Order("123", "EUR", true, "USD", new BigDecimal("1.20"), "31.12.2025");

    when(orderRepository.saveSync(any(Order.class))).thenReturn(createdOrder);

    var result = orderService.createOrder(inputOrder);

    assertThat(result.id()).isEqualTo("123");
    assertThat(result.investmentCcy()).isEqualTo("EUR");
    verify(orderRepository).saveSync(inputOrder);
  }

  @Test
  void shouldCreateOrderAsync() {
    var inputOrder = Order.createNew("EUR", true, "USD", new BigDecimal("1.20"), "31.12.2025");
    var createdOrder = new Order("123", "EUR", true, "USD", new BigDecimal("1.20"), "31.12.2025");

    when(orderRepository.save(any(Order.class)))
        .thenReturn(CompletableFuture.completedFuture(createdOrder));

    var resultFuture = orderService.createOrderAsync(inputOrder);

    var result = resultFuture.join();
    assertThat(result.id()).isEqualTo("123");
    verify(orderRepository).save(inputOrder);
  }

  @Test
  void shouldRejectOrderWithId() {
    var orderWithId = new Order("123", "EUR", true, "USD", new BigDecimal("1.20"), "31.12.2025");

    assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(orderWithId));
    verifyNoInteractions(orderRepository);
  }

  @Test
  void shouldRejectNullOrder() {
    assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(null));
    verifyNoInteractions(orderRepository);
  }

  @Test
  void shouldRejectInvalidOrder() {
    var invalidOrder = Order.createNew("EUR", true, "USD", new BigDecimal("1.20"), "01.01.2020");

    assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(invalidOrder));
    verifyNoInteractions(orderRepository);
  }

  @Test
  void shouldCancelOrderSuccessfully() {
    when(orderRepository.deleteSync("123")).thenReturn(true);

    var result = orderService.cancelOrder("123");

    assertThat(result).isTrue();
    verify(orderRepository).deleteSync("123");
  }

  @Test
  void shouldReturnFalseWhenCancelOrderFails() {
    when(orderRepository.deleteSync("123")).thenReturn(false);

    var result = orderService.cancelOrder("123");

    assertThat(result).isFalse();
    verify(orderRepository).deleteSync("123");
  }

  @Test
  void shouldCancelOrderAsync() {
    when(orderRepository.delete("123")).thenReturn(CompletableFuture.completedFuture(true));

    var resultFuture = orderService.cancelOrderAsync("123");

    var result = resultFuture.join();
    assertThat(result).isTrue();
    verify(orderRepository).delete("123");
  }

  @Test
  void shouldGetAllOrders() {
    var orders =
        List.of(
            new Order("1", "EUR", true, "USD", new BigDecimal("1.20"), "31.12.2025"),
            new Order("2", "GBP", false, "USD", new BigDecimal("1.30"), "31.12.2025"));
    when(orderRepository.findAllSync()).thenReturn(orders);

    var result = orderService.getAllOrders();

    assertThat(result).hasSize(2);
    assertThat(result.get(0).id()).isEqualTo("1");
    assertThat(result.get(1).id()).isEqualTo("2");
    verify(orderRepository).findAllSync();
  }

  @Test
  void shouldGetAllOrdersAsync() {
    var orders = List.of(new Order("1", "EUR", true, "USD", new BigDecimal("1.20"), "31.12.2025"));
    when(orderRepository.findAll()).thenReturn(CompletableFuture.completedFuture(orders));

    var resultFuture = orderService.getAllOrdersAsync();

    var result = resultFuture.join();
    assertThat(result).hasSize(1);
    verify(orderRepository).findAll();
  }

  @Test
  void shouldFindOrderById() {
    var orders =
        List.of(
            new Order("1", "EUR", true, "USD", new BigDecimal("1.20"), "31.12.2025"),
            new Order("2", "GBP", false, "USD", new BigDecimal("1.30"), "31.12.2025"));
    when(orderRepository.findAllSync()).thenReturn(orders);

    var result = orderService.findOrderById("1");

    assertThat(result).isPresent();
    assertThat(result.get().id()).isEqualTo("1");
    assertThat(result.get().investmentCcy()).isEqualTo("EUR");
  }

  @Test
  void shouldReturnEmptyWhenOrderNotFound() {
    var orders = List.of(new Order("1", "EUR", true, "USD", new BigDecimal("1.20"), "31.12.2025"));
    when(orderRepository.findAllSync()).thenReturn(orders);

    var result = orderService.findOrderById("999");

    assertThat(result).isEmpty();
  }

  @Test
  void shouldHandleRepositoryExceptionInFindById() {
    when(orderRepository.findAllSync()).thenThrow(new RuntimeException("Repository error"));

    var result = orderService.findOrderById("1");

    assertThat(result).isEmpty();
    verify(orderRepository).findAllSync();
  }

  @Test
  void shouldReturnEmptyListWhenNoOrders() {
    when(orderRepository.findAllSync()).thenReturn(List.of());

    var result = orderService.getAllOrders();

    assertThat(result).isEmpty();
    verify(orderRepository).findAllSync();
  }
}
