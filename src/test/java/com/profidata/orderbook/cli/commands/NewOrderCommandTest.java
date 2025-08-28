package com.profidata.orderbook.cli.commands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.profidata.orderbook.domain.Order;
import com.profidata.orderbook.service.OrderService;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NewOrderCommandTest {

  @Mock private OrderService orderService;

  private NewOrderCommand command;

  @BeforeEach
  void setUp() {
    command = new NewOrderCommand(orderService);
  }

  @Test
  void shouldCreateOrderSuccessfully() throws Exception {
    String[] args = {"buy", "EUR", "USD", "1.20", "31.12.2025"};
    Order createdOrder = new Order("123", "EUR", true, "USD", new BigDecimal("1.20"), "31.12.2025");
    when(orderService.createOrder(any())).thenReturn(createdOrder);

    String result = command.execute(args);

    assertThat(result).contains("Order created successfully with ID: 123");
  }

  @Test
  void shouldValidateNewOrderArguments() throws Exception {
    String[] args = {"buy", "EUR"};

    String result = command.execute(args);
    assertThat(result).contains("Error executing");
    assertThat(result).contains("Expected: 5, Provided: 2");
  }

  @Test
  void shouldRejectInvalidOrderType() throws Exception {
    String[] args = {"invalid", "EUR", "USD", "1.20", "31.12.2025"};

    String result = command.execute(args);
    assertThat(result).contains("Error executing");
    assertThat(result).contains("must be either 'buy' or 'sell'");
  }

  @Test
  void shouldRejectSameCurrencies() throws Exception {
    String[] args = {"buy", "EUR", "EUR", "1.20", "31.12.2025"};

    String result = command.execute(args);
    assertThat(result).contains("Error executing");
    assertThat(result).contains("cannot be the same");
  }
}
