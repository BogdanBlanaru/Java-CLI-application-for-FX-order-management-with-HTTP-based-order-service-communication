package com.profidata.orderbook.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.profidata.orderbook.domain.Order;
import com.profidata.orderbook.dto.request.CreateOrderRequest;
import com.profidata.orderbook.dto.response.OrderResponse;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderMapperTest {

  private OrderMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new OrderMapper();
  }

  @Test
  void shouldMapCreateOrderRequestToEntity() {
    // Given
    var request = new CreateOrderRequest("EUR", true, "USD", new BigDecimal("1.20"), "31.12.2025");

    // When
    var order = mapper.toEntity(request);

    // Then
    assertThat(order.id()).isNull();
    assertThat(order.investmentCcy()).isEqualTo("EUR");
    assertThat(order.buy()).isTrue();
    assertThat(order.counterCcy()).isEqualTo("USD");
    assertThat(order.limit()).isEqualTo(new BigDecimal("1.20"));
    assertThat(order.validUntil()).isEqualTo("31.12.2025");
  }

  @Test
  void shouldMapOrderToResponse() {
    // Given
    var order = new Order("123", "EUR", true, "USD", new BigDecimal("1.20"), "31.12.2025");

    // When
    var response = mapper.toResponse(order);

    // Then
    assertThat(response.id()).isEqualTo("123");
    assertThat(response.investmentCcy()).isEqualTo("EUR");
    assertThat(response.buy()).isTrue();
    assertThat(response.counterCcy()).isEqualTo("USD");
    assertThat(response.limit()).isEqualTo(new BigDecimal("1.20"));
    assertThat(response.validUntil()).isEqualTo("31.12.2025");
  }

  @Test
  void shouldMapOrderResponseToEntity() {
    // Given
    var response =
        new OrderResponse("123", "EUR", true, "USD", new BigDecimal("1.20"), "31.12.2025");

    // When
    var order = mapper.fromApiResponse(response);

    // Then
    assertThat(order.id()).isEqualTo("123");
    assertThat(order.investmentCcy()).isEqualTo("EUR");
    assertThat(order.buy()).isTrue();
    assertThat(order.counterCcy()).isEqualTo("USD");
    assertThat(order.limit()).isEqualTo(new BigDecimal("1.20"));
    assertThat(order.validUntil()).isEqualTo("31.12.2025");
  }
}
