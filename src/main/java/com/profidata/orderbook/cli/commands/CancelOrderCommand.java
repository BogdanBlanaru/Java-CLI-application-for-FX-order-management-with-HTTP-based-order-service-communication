package com.profidata.orderbook.cli.commands;

import com.profidata.orderbook.service.OrderService;
import com.profidata.orderbook.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Command for cancelling existing FX orders using proper service layer. */
@Component
public class CancelOrderCommand extends AbstractCommand {

  private static final Logger LOGGER = LoggerFactory.getLogger(CancelOrderCommand.class);

  private static final String COMMAND_NAME = "cancel";
  private static final int EXPECTED_ARGS = 1;

  private final OrderService orderService; // Use service instead of client

  public CancelOrderCommand(OrderService orderService) {
    this.orderService = orderService;
  }

  @Override
  public String execute(String[] args) throws Exception {
    LOGGER.debug("Executing cancel order command with args: {}", (Object) args);

    try {
      validateArguments(args);

      String orderId = safeTrim(args[0]);
      ValidationUtils.validateOrderId(orderId);

      LOGGER.info("Attempting to cancel order with ID: {}", orderId);

      boolean cancelled = orderService.cancelOrder(orderId);

      String result;
      if (cancelled) {
        result = String.format("Order %s cancelled successfully", orderId);
        LOGGER.info("Successfully cancelled order: {}", orderId);
      } else {
        result = String.format("Order %s not found or could not be cancelled", orderId);
        LOGGER.warn("Order not found or could not be cancelled: {}", orderId);
      }

      return result;

    } catch (Exception e) {
      String errorMsg = formatError(COMMAND_NAME, e.getMessage());
      LOGGER.error("Failed to cancel order", e);
      return errorMsg;
    }
  }

  @Override
  public void validateArguments(String[] args) throws IllegalArgumentException {
    validateArgumentCount(args, EXPECTED_ARGS, COMMAND_NAME);
    validateNotEmpty(args[0], "Order ID");
  }

  @Override
  public String getCommandName() {
    return COMMAND_NAME;
  }

  @Override
  public String getUsage() {
    return "cancel <order_id>";
  }

  @Override
  public String getDescription() {
    return "Cancels an existing FX order by its ID. Example: cancel 5";
  }
}
