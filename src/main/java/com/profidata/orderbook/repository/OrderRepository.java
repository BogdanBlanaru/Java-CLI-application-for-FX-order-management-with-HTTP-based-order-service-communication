package com.profidata.orderbook.repository;

import com.profidata.orderbook.domain.Order;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/** Repository interface for order data access. */
public interface OrderRepository {
  CompletableFuture<Order> save(Order order);

  Order saveSync(Order order);

  CompletableFuture<Boolean> delete(String orderId);

  boolean deleteSync(String orderId);

  CompletableFuture<List<Order>> findAll();

  List<Order> findAllSync();
}
