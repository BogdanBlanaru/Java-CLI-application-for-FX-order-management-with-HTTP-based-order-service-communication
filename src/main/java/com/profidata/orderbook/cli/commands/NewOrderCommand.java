package com.profidata.orderbook.cli.commands;

import com.profidata.orderbook.domain.Order;
import com.profidata.orderbook.service.OrderService;
import com.profidata.orderbook.util.ValidationUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Command for creating new FX orders using proper service layer. */
@Component
public class NewOrderCommand extends AbstractCommand {

  private static final Logger LOGGER = LoggerFactory.getLogger(NewOrderCommand.class);

  private static final String COMMAND_NAME = "new";
  private static final int EXPECTED_ARGS = 5;

  private final OrderService orderService;

  public NewOrderCommand(OrderService orderService) {
    this.orderService = orderService;
  }

  @Override
  public String execute(String[] args) throws Exception {
    LOGGER.debug("Executing new order command with args: {}", (Object) args);

    try {
      validateArguments(args);

      String orderType = safeUpperCase(args[0]);
      String investmentCcy = safeUpperCase(args[1]);
      String counterCcy = safeUpperCase(args[2]);
      String limitStr = safeTrim(args[3]);
      String validityStr = safeTrim(args[4]);

      validateOrderType(orderType);
      ValidationUtils.validateCurrencyCode(investmentCcy, "Investment currency");
      ValidationUtils.validateCurrencyCode(counterCcy, "Counter currency");

      if (investmentCcy.equals(counterCcy)) {
        throw new IllegalArgumentException(
            "Investment currency and counter currency cannot be the same");
      }

      BigDecimal limit = parseLimit(limitStr);
      validateDate(validityStr);

      boolean isBuy = "BUY".equals(orderType);

      Order orderToCreate = Order.createNew(investmentCcy, isBuy, counterCcy, limit, validityStr);

      LOGGER.info(
          "Creating {} order: {} {} with limit {} valid until {}",
          orderType,
          investmentCcy,
          counterCcy,
          limit,
          validityStr);

      Order createdOrder = orderService.createOrder(orderToCreate);

      String result = String.format("Order created successfully with ID: %s", createdOrder.id());
      LOGGER.info("Successfully created order: {}", createdOrder);

      return result;

    } catch (Exception e) {
      String errorMsg = formatError(COMMAND_NAME, e.getMessage());
      LOGGER.error("Failed to create new order", e);
      return errorMsg;
    }
  }

  @Override
  public void validateArguments(String[] args) throws IllegalArgumentException {
    validateArgumentCount(args, EXPECTED_ARGS, COMMAND_NAME);

    for (int i = 0; i < args.length; i++) {
      validateNotEmpty(args[i], "Argument " + (i + 1));
    }
  }

  @Override
  public String getCommandName() {
    return COMMAND_NAME;
  }

  @Override
  public String getUsage() {
    return "new [buy|sell] <investment ccy> <counter ccy> <limit> <validity>";
  }

  @Override
  public String getDescription() {
    return "Creates a new FX order with specified parameters. "
        + "Example: new buy EUR CHF 1.14 31.12.2025";
  }

  private void validateOrderType(String orderType) {
    if (!"BUY".equals(orderType) && !"SELL".equals(orderType)) {
      throw new IllegalArgumentException(
          "Order type must be either 'buy' or 'sell', got: " + orderType);
    }
  }

  private BigDecimal parseLimit(String limitStr) {
    try {
      BigDecimal limit = new BigDecimal(limitStr);
      if (limit.compareTo(BigDecimal.ZERO) <= 0) {
        throw new IllegalArgumentException("Limit price must be positive, got: " + limitStr);
      }
      return limit;
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(
          "Invalid limit price format: " + limitStr + ". Expected a positive number.", e);
    }
  }

  private void validateDate(String dateStr) {
    try {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
      LocalDate validUntil = LocalDate.parse(dateStr, formatter);

      if (validUntil.isBefore(LocalDate.now())) {
        throw new IllegalArgumentException("Validity date cannot be in the past: " + dateStr);
      }

    } catch (Exception e) {
      if (e instanceof IllegalArgumentException) {
        throw e;
      }
      throw new IllegalArgumentException(
          "Invalid date format: " + dateStr + ". Expected format: dd.MM.yyyy (e.g., 31.12.2025)",
          e);
    }
  }
}
