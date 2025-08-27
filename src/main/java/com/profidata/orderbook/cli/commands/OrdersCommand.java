package com.profidata.orderbook.cli.commands;

import com.profidata.orderbook.domain.CurrencyPair;
import com.profidata.orderbook.domain.FXRate;
import com.profidata.orderbook.domain.Order;
import com.profidata.orderbook.service.OrderService;
import com.profidata.orderbook.service.RateService;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Command for displaying all orders using proper service layer. */
@Component
public class OrdersCommand extends AbstractCommand {

  private static final Logger LOGGER = LoggerFactory.getLogger(OrdersCommand.class);

  private static final String COMMAND_NAME = "orders";
  private static final int EXPECTED_ARGS = 0;

  private final OrderService orderService;
  private final RateService rateService;

  public OrdersCommand(OrderService orderService, RateService rateService) {
    this.orderService = orderService;
    this.rateService = rateService;
  }

  @Override
  public String execute(String[] args) throws Exception {
    LOGGER.debug("Executing orders command");

    try {
      validateArguments(args);

      LOGGER.info("Retrieving all orders and current rates");

      List<Order> orders = orderService.getAllOrders();
      List<FXRate> rates = rateService.getCurrentRates();

      if (orders.isEmpty()) {
        return "No orders found in the order book.";
      }

      Map<CurrencyPair, FXRate> rateMap = createRateMap(rates);
      String result = formatOrdersTable(orders, rateMap);

      LOGGER.info("Successfully retrieved {} orders", orders.size());
      return result;

    } catch (Exception e) {
      String errorMsg = formatError(COMMAND_NAME, e.getMessage());
      LOGGER.error("Failed to retrieve orders", e);
      return errorMsg;
    }
  }

  @Override
  public void validateArguments(String[] args) throws IllegalArgumentException {
    validateArgumentCount(args, EXPECTED_ARGS, COMMAND_NAME);
  }

  @Override
  public String getCommandName() {
    return COMMAND_NAME;
  }

  @Override
  public String getUsage() {
    return "orders";
  }

  @Override
  public String getDescription() {
    return "Displays all orders sorted by currency pair and distance to current market rate";
  }

  private Map<CurrencyPair, FXRate> createRateMap(List<FXRate> rates) {
    Map<CurrencyPair, FXRate> rateMap = new HashMap<>();

    for (FXRate rate : rates) {
      rateMap.put(rate.ccyPair(), rate);
      rateMap.put(rate.ccyPair().inverse(), rate.inverse());
    }

    return rateMap;
  }

  private BigDecimal findMarketRate(Order order, Map<CurrencyPair, FXRate> rateMap) {
    CurrencyPair orderPair = order.getCurrencyPair();

    FXRate rate = rateMap.get(orderPair);
    if (rate != null) {
      return rate.getRateForOrder(order.isBuy());
    }

    rate = rateMap.get(orderPair.inverse());
    if (rate != null) {
      return rate.getRateForOrder(!order.isBuy());
    }

    LOGGER.warn("No market rate found for currency pair: {}", orderPair);
    return BigDecimal.ZERO;
  }

  private String formatOrdersTable(List<Order> orders, Map<CurrencyPair, FXRate> rateMap) {
    StringBuilder sb = new StringBuilder();

    sb.append("Current Orders (sorted by currency pair and distance to market):\n");
    sb.append("===============================================================\n");
    sb.append(
        String.format(
            "%-6s %-4s %-4s %-10s %-15s %-10s%n",
            "Type", "Inv", "Ctr", "Limit", "Valid Until", "Distance"));
    sb.append("-".repeat(70)).append("\n");

    List<OrderWithDistance> ordersWithDistance =
        orders.stream()
            .map(
                order -> {
                  BigDecimal marketRate = findMarketRate(order, rateMap);
                  String distanceStr;
                  BigDecimal distanceForSort;

                  if (marketRate.compareTo(BigDecimal.ZERO) == 0) {
                    distanceStr = "N/A";
                    distanceForSort = new BigDecimal(Long.MAX_VALUE);
                  } else {
                    BigDecimal distance = order.calculateDistanceFromMarket(marketRate);
                    distanceStr = distance.toPlainString();
                    distanceForSort = distance;
                  }
                  return new OrderWithDistance(order, distanceStr, distanceForSort);
                })
            .collect(Collectors.toList());

    ordersWithDistance.sort(
        Comparator.comparing((OrderWithDistance o) -> o.order.getCurrencyPair().toString())
            .thenComparing(o -> o.distanceForSort));

    for (OrderWithDistance orderInfo : ordersWithDistance) {
      Order order = orderInfo.order;
      sb.append(
          String.format(
              "%-6s %-4s %-4s %-10s %-15s %-10s%n",
              order.getOrderType(),
              order.investmentCcy(),
              order.counterCcy(),
              order.limit() != null ? order.limit().toPlainString() : "N/A",
              order.validUntil(),
              orderInfo.distance));
    }

    sb.append("-".repeat(70)).append("\n");
    sb.append(String.format("Total orders: %d%n", orders.size()));

    return sb.toString();
  }

  private static class OrderWithDistance {
    final Order order;
    final String distance;
    final BigDecimal distanceForSort;

    OrderWithDistance(Order order, String distance, BigDecimal distanceForSort) {
      this.order = order;
      this.distance = distance;
      this.distanceForSort = distanceForSort;
    }
  }
}
