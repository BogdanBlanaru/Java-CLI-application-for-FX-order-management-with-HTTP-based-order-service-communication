package com.profidata.orderbook.repository.impl;

import com.profidata.orderbook.client.OrderServiceClient;
import com.profidata.orderbook.domain.Order;
import com.profidata.orderbook.mapper.OrderMapper;
import com.profidata.orderbook.repository.OrderRepository;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

/** HTTP-based implementation of OrderRepository. */
@Repository
public class HttpOrderRepository implements OrderRepository {

  private final OrderServiceClient client;
  private final OrderMapper mapper = OrderMapper.INSTANCE;

  public HttpOrderRepository(OrderServiceClient client) {
    this.client = client;
  }

  @Override
  public CompletableFuture<Order> save(Order order) {
    var orderResponse = mapper.toResponse(order);
    return client.createOrderAsync(orderResponse).thenApply(mapper::fromApiResponse);
  }

  @Override
  public Order saveSync(Order order) {
    var orderResponse = mapper.toResponse(order);
    var result = client.createOrderSync(orderResponse);
    return mapper.fromApiResponse(result);
  }

  @Override
  public CompletableFuture<Boolean> delete(String orderId) {
    return client.cancelOrderAsync(orderId);
  }

  @Override
  public boolean deleteSync(String orderId) {
    return client.cancelOrderSync(orderId);
  }

  @Override
  public CompletableFuture<List<Order>> findAll() {
    return client
        .retrieveOrdersAsync()
        .thenApply(
            responses ->
                responses.stream().map(mapper::fromApiResponse).collect(Collectors.toList()));
  }

  @Override
  public List<Order> findAllSync() {
    return client.retrieveOrdersSync().stream()
        .map(mapper::fromApiResponse)
        .collect(Collectors.toList());
  }
}
