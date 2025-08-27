package com.profidata.orderbook.service;

import com.profidata.orderbook.client.OrderServiceClient;
import com.profidata.orderbook.domain.FXRate;
import com.profidata.orderbook.domain.Order;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** Service for order book analysis and reporting. */
@Service
public class OrderBookServiceImpl {

  private static final Logger LOGGER = LoggerFactory.getLogger(OrderBookServiceImpl.class);

  private final OrderService orderService;
  private final RateService rateService;
  private final OrderServiceClient orderServiceClient;

  public OrderBookServiceImpl(
      OrderService orderService, RateService rateService, OrderServiceClient orderServiceClient) {
    this.orderService = orderService;
    this.rateService = rateService;
    this.orderServiceClient = orderServiceClient;
  }

  public CompletableFuture<OrderBookAnalytics> generateAnalyticsAsync() {
    LOGGER.debug("Generating order book analytics");

    CompletableFuture<List<Order>> ordersFuture = orderService.getAllOrdersAsync();
    CompletableFuture<List<FXRate>> ratesFuture = rateService.getCurrentRatesAsync();

    return CompletableFuture.allOf(ordersFuture, ratesFuture)
        .thenApply(
            v -> {
              List<Order> orders = ordersFuture.join();
              List<FXRate> rates = ratesFuture.join();

              return new OrderBookAnalytics(orders, rates);
            })
        .whenComplete(
            (result, throwable) -> {
              if (throwable == null) {
                LOGGER.info("Successfully generated order book analytics");
              } else {
                LOGGER.error("Failed to generate order book analytics", throwable);
              }
            });
  }

  public CompletableFuture<HealthStatus> healthCheckAsync() {
    LOGGER.debug("Performing order book health check");

    return orderServiceClient
        .healthCheckAsync()
        .thenApply(
            isHealthy -> {
              HealthStatus status =
                  new HealthStatus(
                      isHealthy,
                      isHealthy ? "All systems operational" : "Order service unavailable",
                      System.currentTimeMillis());

              LOGGER.info("Health check completed: {}", status.status());
              return status;
            })
        .exceptionally(
            throwable -> {
              LOGGER.error("Health check failed", throwable);
              return new HealthStatus(
                  false,
                  "Health check failed: " + throwable.getMessage(),
                  System.currentTimeMillis());
            });
  }

  public record OrderBookAnalytics(List<Order> orders, List<FXRate> rates, long timestamp) {
    public OrderBookAnalytics(List<Order> orders, List<FXRate> rates) {
      this(orders, rates, System.currentTimeMillis());
    }

    public int getTotalOrders() {
      return orders.size();
    }

    public int getTotalRates() {
      return rates.size();
    }

    public long getBuyOrderCount() {
      return orders.stream().mapToLong(order -> order.isBuy() ? 1 : 0).sum();
    }

    public long getSellOrderCount() {
      return orders.stream().mapToLong(order -> order.isSell() ? 1 : 0).sum();
    }
  }

  public record HealthStatus(boolean isHealthy, String status, long timestamp) {}
}
