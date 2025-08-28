package com.profidata.orderbook.cli;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.profidata.orderbook.cli.commands.*;
import com.profidata.orderbook.exception.CommandParsingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommandParserTest {

  @Mock private NewOrderCommand newOrderCommand;
  @Mock private CancelOrderCommand cancelOrderCommand;
  @Mock private RatesCommand ratesCommand;
  @Mock private OrdersCommand ordersCommand;
  @Mock private SummaryCommand summaryCommand;

  private CommandParser parser;

  @BeforeEach
  void setUp() {
    when(newOrderCommand.getCommandName()).thenReturn("new");
    when(cancelOrderCommand.getCommandName()).thenReturn("cancel");
    when(ratesCommand.getCommandName()).thenReturn("rates");
    when(ordersCommand.getCommandName()).thenReturn("orders");
    when(summaryCommand.getCommandName()).thenReturn("summary");

    parser =
        new CommandParser(
            newOrderCommand, cancelOrderCommand, ratesCommand, ordersCommand, summaryCommand);
  }

  @Test
  void shouldParseValidCommand() throws Exception {
    when(ratesCommand.execute(any())).thenReturn("Rate data");

    String result = parser.parseAndExecute("rates");

    assertThat(result).isEqualTo("Rate data");
  }

  @Test
  void shouldHandleHelpCommand() throws Exception {
    String result = parser.parseAndExecute("help");

    assertThat(result).contains("FX OrderBook CLI - Available Commands:");
  }

  @Test
  void shouldHandleExitCommand() throws Exception {
    String result = parser.parseAndExecute("exit");

    assertThat(result).isEqualTo("SYSTEM_EXIT");
  }

  @Test
  void shouldThrowExceptionForEmptyInput() {
    assertThatThrownBy(() -> parser.parseAndExecute(""))
        .isInstanceOf(CommandParsingException.class)
        .hasMessage("Empty command provided");
  }

  @Test
  void shouldThrowExceptionForUnknownCommand() {
    assertThatThrownBy(() -> parser.parseAndExecute("unknown"))
        .isInstanceOf(CommandParsingException.class)
        .hasMessageContaining("Unknown command: unknown");
  }

  @Test
  void shouldReturnAvailableCommands() {
    assertThat(parser.getAvailableCommands())
        .contains("new", "cancel", "rates", "orders", "summary");
  }

  @Test
  void shouldCheckCommandExists() {
    assertThat(parser.hasCommand("rates")).isTrue();
    assertThat(parser.hasCommand("unknown")).isFalse();
  }

  @Test
  void shouldGetSpecificCommand() {
    assertThat(parser.getCommand("rates")).isEqualTo(ratesCommand);
    assertThat(parser.getCommand("unknown")).isNull();
  }
}
