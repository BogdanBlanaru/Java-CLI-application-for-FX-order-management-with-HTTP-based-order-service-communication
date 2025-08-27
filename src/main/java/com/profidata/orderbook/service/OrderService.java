package com.profidata.orderbook.service;

import com.profidata.orderbook.domain.Order;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/** Service interface for order management operations. */
public interface OrderService {
  CompletableFuture<Order> createOrderAsync(Order order);

  Order createOrder(Order order);

  CompletableFuture<Boolean> cancelOrderAsync(String orderId);

  boolean cancelOrder(String orderId);

  CompletableFuture<List<Order>> getAllOrdersAsync();

  List<Order> getAllOrders();

  Optional<Order> findOrderById(String orderId);
}
