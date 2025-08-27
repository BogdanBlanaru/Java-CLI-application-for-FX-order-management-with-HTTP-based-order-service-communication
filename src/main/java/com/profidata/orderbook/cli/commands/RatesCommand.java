package com.profidata.orderbook.cli.commands;

import com.profidata.orderbook.domain.FXRate;
import com.profidata.orderbook.service.RateService;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Command for displaying current FX rates using proper service layer. */
@Component
public class RatesCommand extends AbstractCommand {

  private static final Logger LOGGER = LoggerFactory.getLogger(RatesCommand.class);

  private static final String COMMAND_NAME = "rates";
  private static final int EXPECTED_ARGS = 0;

  private final RateService rateService; // Use service, not client directly

  public RatesCommand(RateService rateService) {
    this.rateService = rateService;
  }

  @Override
  public String execute(String[] args) throws Exception {
    LOGGER.debug("Executing rates command");

    try {
      validateArguments(args);

      LOGGER.info("Retrieving current FX rates");

      List<FXRate> rates = rateService.getCurrentRates();

      if (rates.isEmpty()) {
        return "No FX rates available at this time.";
      }

      String result = formatRatesTable(rates);

      LOGGER.info("Successfully retrieved {} FX rates", rates.size());
      return result;

    } catch (Exception e) {
      String errorMsg = formatError(COMMAND_NAME, e.getMessage());
      LOGGER.error("Failed to retrieve FX rates", e);
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
    return "rates";
  }

  @Override
  public String getDescription() {
    return "Displays current FX exchange rates with bid/ask spreads";
  }

  private String formatRatesTable(List<FXRate> rates) {
    StringBuilder sb = new StringBuilder();

    sb.append("Current FX Exchange Rates:\n");
    sb.append("=========================\n");
    sb.append(
        String.format("%-10s %-12s %-12s %-12s %-10s%n", "Pair", "Bid", "Ask", "Mid", "Spread %"));
    sb.append("-".repeat(65)).append("\n");

    List<FXRate> sortedRates =
        rates.stream()
            .sorted((r1, r2) -> r1.ccyPair().toString().compareTo(r2.ccyPair().toString()))
            .collect(Collectors.toList());

    for (FXRate rate : sortedRates) {
      sb.append(
          String.format(
              "%-10s %-12s %-12s %-12s %-10s%n",
              rate.ccyPair().toString(),
              rate.bid().toPlainString(),
              rate.ask().toPlainString(),
              rate.getMidPrice().toPlainString(),
              rate.getSpreadPercentage().toPlainString()));
    }

    return sb.toString();
  }
}
