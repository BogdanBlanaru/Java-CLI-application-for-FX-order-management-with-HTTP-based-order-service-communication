package com.profidata.orderbook.service;

import com.profidata.orderbook.domain.Order;
import com.profidata.orderbook.repository.OrderRepository;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** Implementation of OrderService interface. */
@Service
public class OrderServiceImpl implements OrderService {

  private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

  private final OrderRepository orderRepository;

  public OrderServiceImpl(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  @Override
  public CompletableFuture<Order> createOrderAsync(Order order) {
    LOGGER.debug("Creating order asynchronously: {}", order);

    return validateOrderForCreation(order)
        .thenCompose(orderRepository::save)
        .whenComplete(
            (result, throwable) -> {
              if (throwable == null) {
                LOGGER.info("Successfully created order with ID: {}", result.id());
              } else {
                LOGGER.error("Failed to create order", throwable);
              }
            });
  }

  @Override
  public Order createOrder(Order order) {
    LOGGER.debug("Creating order synchronously: {}", order);

    validateOrder(order);
    Order createdOrder = orderRepository.saveSync(order);

    LOGGER.info("Successfully created order with ID: {}", createdOrder.id());
    return createdOrder;
  }

  @Override
  public CompletableFuture<Boolean> cancelOrderAsync(String orderId) {
    LOGGER.debug("Cancelling order asynchronously: {}", orderId);
    return orderRepository.delete(orderId);
  }

  @Override
  public boolean cancelOrder(String orderId) {
    LOGGER.debug("Cancelling order synchronously: {}", orderId);
    return orderRepository.deleteSync(orderId);
  }

  @Override
  public CompletableFuture<List<Order>> getAllOrdersAsync() {
    LOGGER.debug("Retrieving all orders asynchronously");
    return orderRepository.findAll();
  }

  @Override
  public List<Order> getAllOrders() {
    LOGGER.debug("Retrieving all orders synchronously");
    return orderRepository.findAllSync();
  }

  @Override
  public Optional<Order> findOrderById(String orderId) {
    LOGGER.debug("Finding order by ID: {}", orderId);

    try {
      List<Order> orders = orderRepository.findAllSync();
      return orders.stream().filter(order -> orderId.equals(order.id())).findFirst();
    } catch (Exception e) {
      LOGGER.error("Error finding order by ID: {}", orderId, e);
      return Optional.empty();
    }
  }

  private CompletableFuture<Order> validateOrderForCreation(Order order) {
    return CompletableFuture.supplyAsync(
        () -> {
          validateOrder(order);
          return order;
        });
  }

  private void validateOrder(Order order) {
    if (order == null) {
      throw new IllegalArgumentException("Order cannot be null");
    }

    if (order.id() != null) {
      throw new IllegalArgumentException("Order ID should be null for new orders");
    }

    if (!order.isValid()) {
      throw new IllegalArgumentException("Order validation failed: order is not valid");
    }
  }
}
