package com.profidata.orderbook.cli.commands;

import com.profidata.orderbook.domain.Order;
import com.profidata.orderbook.service.OrderService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Command for generating order book summary using proper service layer. */
@Component
public class SummaryCommand extends AbstractCommand {

  private static final Logger LOGGER = LoggerFactory.getLogger(SummaryCommand.class);

  private static final String COMMAND_NAME = "summary";
  private static final int EXPECTED_ARGS = 0;

  private final OrderService orderService;

  public SummaryCommand(OrderService orderService) {
    this.orderService = orderService;
  }

  @Override
  public String execute(String[] args) throws Exception {
    LOGGER.debug("Executing summary command");

    try {
      validateArguments(args);

      LOGGER.info("Retrieving all orders for summary");

      List<Order> orders = orderService.getAllOrders();

      if (orders.isEmpty()) {
        return "No orders found in the order book.";
      }

      String result = generateOrderSummary(orders);

      LOGGER.info("Successfully generated summary for {} orders", orders.size());
      return result;

    } catch (Exception e) {
      String errorMsg = formatError(COMMAND_NAME, e.getMessage());
      LOGGER.error("Failed to generate order summary", e);
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
    return "summary";
  }

  @Override
  public String getDescription() {
    return "Displays order book summary grouped by investment currency, counter currency, and buy/sell";
  }

  private String generateOrderSummary(List<Order> orders) {
    StringBuilder sb = new StringBuilder();

    Map<OrderGroupKey, List<Order>> groupedOrders =
        orders.stream()
            .collect(
                Collectors.groupingBy(
                    order ->
                        new OrderGroupKey(
                            order.investmentCcy(), order.counterCcy(), order.isBuy())));

    List<OrderSummary> summaries =
        groupedOrders.entrySet().stream()
            .map(
                entry -> {
                  OrderGroupKey key = entry.getKey();
                  List<Order> groupOrders = entry.getValue();

                  int count = groupOrders.size();
                  BigDecimal averageLimit = calculateAverageLimit(groupOrders);

                  return new OrderSummary(
                      key.orderType(), key.investmentCcy(), key.counterCcy(), count, averageLimit);
                })
            .collect(Collectors.toList());

    summaries.sort(
        (s1, s2) -> {
          int invCcyComparison = s1.investmentCcy().compareTo(s2.investmentCcy());
          if (invCcyComparison != 0) {
            return invCcyComparison;
          }

          int counterCcyComparison = s1.counterCcy().compareTo(s2.counterCcy());
          if (counterCcyComparison != 0) {
            return counterCcyComparison;
          }

          return s1.orderType().compareTo(s2.orderType());
        });

    sb.append("Order Book Summary:\n");
    sb.append("==================\n");
    sb.append(
        String.format("%-6s %-4s %-4s %-8s %-12s%n", "Type", "Inv", "Ctr", "Count", "Avg Limit"));
    sb.append("-".repeat(45)).append("\n");

    for (OrderSummary summary : summaries) {
      sb.append(
          String.format(
              "%-6s %-4s %-4s %-8d %-12s%n",
              summary.orderType(),
              summary.investmentCcy(),
              summary.counterCcy(),
              summary.count(),
              summary.averageLimit() != null ? summary.averageLimit().toPlainString() : "N/A"));
    }

    sb.append("-".repeat(45)).append("\n");
    sb.append(String.format("Total orders: %d%n", orders.size()));
    sb.append(String.format("Unique currency pairs: %d%n", countUniqueCurrencyPairs(summaries)));

    return sb.toString();
  }

  private BigDecimal calculateAverageLimit(List<Order> orders) {
    List<BigDecimal> limits =
        orders.stream().map(Order::limit).filter(Objects::nonNull).collect(Collectors.toList());

    if (limits.isEmpty()) {
      return null;
    }

    BigDecimal sum = limits.stream().reduce(BigDecimal.ZERO, BigDecimal::add);

    return sum.divide(BigDecimal.valueOf(limits.size()), 4, RoundingMode.HALF_UP);
  }

  private int countUniqueCurrencyPairs(List<OrderSummary> summaries) {
    return (int)
        summaries.stream()
            .map(summary -> summary.investmentCcy() + "/" + summary.counterCcy())
            .distinct()
            .count();
  }

  private record OrderGroupKey(String investmentCcy, String counterCcy, boolean isBuy) {
    public String orderType() {
      return isBuy ? "buy" : "sell";
    }
  }

  private record OrderSummary(
      String orderType,
      String investmentCcy,
      String counterCcy,
      int count,
      BigDecimal averageLimit) {}
}
