package com.profidata.orderbook.cli.commands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.profidata.orderbook.domain.CurrencyPair;
import com.profidata.orderbook.domain.FXRate;
import com.profidata.orderbook.domain.Order;
import com.profidata.orderbook.service.OrderService;
import com.profidata.orderbook.service.RateService;
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
class OrdersCommandTest {

  @Mock private OrderService orderService;

  @Mock private RateService rateService;

  @InjectMocks private OrdersCommand command;

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
            new Order("2", "EUR", false, "USD", new BigDecimal("1.20"), "31.12.2025"));

    List<FXRate> rates =
        Arrays.asList(
            new FXRate(
                new CurrencyPair("EUR", "USD"), new BigDecimal("1.15"), new BigDecimal("1.16")));

    when(orderService.getAllOrders()).thenReturn(orders);
    when(rateService.getCurrentRates()).thenReturn(rates);

    String result = command.execute(new String[] {});

    assertThat(result).contains("Current Orders");
    assertThat(result).contains("Total orders: 2");
  }

  @Test
  void shouldRejectInvalidArguments() throws Exception {
    assertThat(command.execute(new String[] {"extra"})).contains("Error");
  }

  @Test
  void shouldReturnCorrectCommandInfo() {
    assertThat(command.getCommandName()).isEqualTo("orders");
    assertThat(command.getUsage()).isEqualTo("orders");
    assertThat(command.getDescription()).contains("Displays all orders");
  }
}
