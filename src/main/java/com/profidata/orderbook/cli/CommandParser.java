package com.profidata.orderbook.cli;

import com.profidata.orderbook.cli.commands.*;
import com.profidata.orderbook.exception.CommandParsingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Parses and routes command line inputs to appropriate command handlers.
 *
 * <p>This parser provides: - Command registration and lookup - Input parsing and validation - Help
 * system - Error handling for unknown commands
 *
 * @author Profidata Developer
 */
@Component
public class CommandParser {

  private static final Logger LOGGER = LoggerFactory.getLogger(CommandParser.class);

  private final Map<String, Command> commands;

  public CommandParser(
      NewOrderCommand newOrderCommand,
      CancelOrderCommand cancelOrderCommand,
      RatesCommand ratesCommand,
      OrdersCommand ordersCommand,
      SummaryCommand summaryCommand) {
    this.commands = new HashMap<>();

    registerCommand(newOrderCommand);
    registerCommand(cancelOrderCommand);
    registerCommand(ratesCommand);
    registerCommand(ordersCommand);
    registerCommand(summaryCommand);

    LOGGER.info("CommandParser initialized with {} commands", commands.size());
  }

  /**
   * Parses and executes a command line input.
   *
   * @param input Raw command line input
   * @return Command execution result
   * @throws CommandParsingException if command parsing fails
   */
  public String parseAndExecute(String input) throws CommandParsingException {
    if (input == null || input.trim().isEmpty()) {
      throw new CommandParsingException("Empty command provided");
    }

    String trimmedInput = input.trim();
    LOGGER.debug("Parsing command: {}", trimmedInput);

    if ("help".equalsIgnoreCase(trimmedInput) || "?".equals(trimmedInput)) {
      return generateHelpText();
    }

    if ("exit".equalsIgnoreCase(trimmedInput) || "quit".equalsIgnoreCase(trimmedInput)) {
      return "SYSTEM_EXIT";
    }

    String[] tokens = parseTokens(trimmedInput);
    if (tokens.length == 0) {
      throw new CommandParsingException("No command specified");
    }

    String commandName = tokens[0].toLowerCase();
    String[] args = Arrays.copyOfRange(tokens, 1, tokens.length);

    Command command = commands.get(commandName);
    if (command == null) {
      throw new CommandParsingException(
          "Unknown command: " + commandName + ". Type 'help' to see available commands.");
    }

    try {
      LOGGER.debug("Executing command '{}' with {} arguments", commandName, args.length);
      return command.execute(args);
    } catch (Exception e) {
      LOGGER.error("Command execution failed: {}", commandName, e);
      throw new CommandParsingException("Command execution failed: " + e.getMessage(), e);
    }
  }

  /** Parses input into tokens, handling quoted strings properly. */
  private String[] parseTokens(String input) {
    return input.trim().split("\\s+");
  }

  /** Registers a command in the parser. */
  private void registerCommand(Command command) {
    String commandName = command.getCommandName().toLowerCase();
    commands.put(commandName, command);
    LOGGER.debug("Registered command: {}", commandName);
  }

  /** Generates comprehensive help text. */
  private String generateHelpText() {
    StringBuilder sb = new StringBuilder();

    sb.append("FX OrderBook CLI - Available Commands:\n");
    sb.append("=====================================\n\n");

    appendCommandHelp(sb, "Order Management:");
    appendCommandDetails(sb, "new");
    appendCommandDetails(sb, "cancel");

    sb.append("\n");
    appendCommandHelp(sb, "Information Display:");
    appendCommandDetails(sb, "rates");
    appendCommandDetails(sb, "orders");
    appendCommandDetails(sb, "summary");

    sb.append("\n");
    appendCommandHelp(sb, "System Commands:");
    sb.append("  help, ?        - Show this help message\n");
    sb.append("  exit, quit     - Exit the application\n");

    sb.append("\nExamples:\n");
    sb.append("---------\n");
    sb.append("  new buy EUR USD 1.10 31.12.2025\n");
    sb.append("  cancel 12345\n");
    sb.append("  rates\n");
    sb.append("  orders\n");
    sb.append("  summary\n");

    return sb.toString();
  }

  /** Appends a category header. */
  private void appendCommandHelp(StringBuilder sb, String category) {
    sb.append(category).append("\n");
  }

  /** Appends detailed command information. */
  private void appendCommandDetails(StringBuilder sb, String commandName) {
    Command command = commands.get(commandName);
    if (command != null) {
      sb.append(String.format("  %-15s - %s\n", command.getUsage(), command.getDescription()));
    }
  }

  /** Gets all available command names. */
  public Set<String> getAvailableCommands() {
    return commands.keySet();
  }

  /** Gets a specific command by name. */
  public Command getCommand(String commandName) {
    return commands.get(commandName.toLowerCase());
  }

  /** Checks if a command exists. */
  public boolean hasCommand(String commandName) {
    return commands.containsKey(commandName.toLowerCase());
  }
}
