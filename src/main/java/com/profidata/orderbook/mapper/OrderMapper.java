package com.profidata.orderbook.mapper;

import com.profidata.orderbook.domain.Order;
import com.profidata.orderbook.dto.request.CreateOrderRequest;
import com.profidata.orderbook.dto.response.OrderResponse;
import org.springframework.stereotype.Component;

/** Mapper for Order entity and DTOs. */
@Component
public class OrderMapper {

  public Order toEntity(CreateOrderRequest request) {
    return Order.createNew(
        request.investmentCcy(),
        request.buy(),
        request.counterCcy(),
        request.limit(),
        request.validUntil());
  }

  public OrderResponse toResponse(Order order) {
    return new OrderResponse(
        order.id(),
        order.investmentCcy(),
        order.buy(),
        order.counterCcy(),
        order.limit(),
        order.validUntil());
  }

  public Order fromApiResponse(OrderResponse response) {
    return new Order(
        response.id(),
        response.investmentCcy(),
        response.buy(),
        response.counterCcy(),
        response.limit(),
        response.validUntil());
  }
}
