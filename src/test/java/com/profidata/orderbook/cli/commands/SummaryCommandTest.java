package com.profidata.orderbook.cli.commands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.profidata.orderbook.domain.Order;
import com.profidata.orderbook.service.OrderService;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SummaryCommandTest {

  @Mock private OrderService orderService;

  @InjectMocks private SummaryCommand command;

  @Test
  void shouldExecuteWithNoOrders() throws Exception {
    when(orderService.getAllOrders()).thenReturn(Collections.emptyList());

    String result = command.execute(new String[] {});

    assertThat(result).isEqualTo("No orders found in the order book.");
  }

  @Test
  void shouldExecuteWithOrders() throws Exception {
    List<Order> orders =
        Arrays.asList(
            new Order("1", "EUR", true, "USD", new BigDecimal("1.10"), "31.12.2025"),
            new Order("2", "EUR", true, "USD", new BigDecimal("1.15"), "31.12.2025"),
            new Order("3", "EUR", false, "CHF", new BigDecimal("1.05"), "31.12.2025"));

    when(orderService.getAllOrders()).thenReturn(orders);

    String result = command.execute(new String[] {});

    assertThat(result).contains("Order Book Summary:");
    assertThat(result).contains("Total orders: 3");
    assertThat(result).contains("Unique currency pairs: 2");
  }

  @Test
  void shouldRejectInvalidArguments() throws Exception {
    assertThat(command.execute(new String[] {"extra"})).contains("Error");
  }

  @Test
  void shouldReturnCorrectCommandInfo() {
    assertThat(command.getCommandName()).isEqualTo("summary");
    assertThat(command.getUsage()).isEqualTo("summary");
    assertThat(command.getDescription()).contains("order book summary");
  }
}
