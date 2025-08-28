package com.profidata.orderbook.cli.commands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.profidata.orderbook.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CancelOrderCommandTest {

  @Mock private OrderService orderService;

  private CancelOrderCommand command;

  @BeforeEach
  void setUp() {
    command = new CancelOrderCommand(orderService);
  }

  @Test
  void shouldCancelOrderSuccessfully() throws Exception {
    String[] args = {"123"};
    when(orderService.cancelOrder("123")).thenReturn(true);

    String result = command.execute(args);

    assertThat(result).contains("cancelled successfully");
  }

  @Test
  void shouldHandleOrderNotFound() throws Exception {
    String[] args = {"999"};
    when(orderService.cancelOrder("999")).thenReturn(false);

    String result = command.execute(args);

    assertThat(result).contains("not found or could not be cancelled");
  }

  @Test
  void shouldValidateOrderId() throws Exception {
    String[] args = {"invalid@id"};

    String result = command.execute(args);
    assertThat(result).contains("Error executing");
    assertThat(result).contains("Order ID");
  }

  @Test
  void shouldHandleEmptyArgs() throws Exception {
    String[] args = {};

    String result = command.execute(args);
    assertThat(result).contains("Error executing");
    assertThat(result).contains("Expected: 1, Provided: 0");
  }
}
