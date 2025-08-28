package com.profidata.orderbook.mapper;

import com.profidata.orderbook.domain.Order;
import com.profidata.orderbook.dto.response.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OrderMapper {
  OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

  Order fromApiResponse(OrderResponse response);

  OrderResponse toResponse(Order order);
}
