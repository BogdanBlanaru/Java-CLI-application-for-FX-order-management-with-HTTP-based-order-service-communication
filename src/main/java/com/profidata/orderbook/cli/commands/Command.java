package com.profidata.orderbook.cli.commands;

import java.util.concurrent.CompletableFuture;

/**
 * Base interface for all CLI commands.
 *
 * <p>Implements the Command pattern for clean separation of concerns and supports both synchronous
 * and asynchronous execution.
 *
 * @author Profidata Developer
 */
public interface Command {

  /**
   * Executes the command synchronously.
   *
   * @param args Command arguments
   * @return Command result as string
   * @throws Exception if command execution fails
   */
  String execute(String[] args) throws Exception;

  /**
   * Executes the command asynchronously.
   *
   * @param args Command arguments
   * @return CompletableFuture with command result
   */
  default CompletableFuture<String> executeAsync(String[] args) {
    return CompletableFuture.supplyAsync(
        () -> {
          try {
            return execute(args);
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        });
  }

  /**
   * Gets the command name.
   *
   * @return Command name
   */
  String getCommandName();

  /**
   * Gets command usage information.
   *
   * @return Usage description
   */
  String getUsage();

  /**
   * Gets command description.
   *
   * @return Description of what the command does
   */
  String getDescription();

  /**
   * Validates command arguments.
   *
   * @param args Command arguments
   * @throws IllegalArgumentException if arguments are invalid
   */
  default void validateArguments(String[] args) throws IllegalArgumentException {}
}

/** Abstract base class for commands with common functionality. */
abstract class AbstractCommand implements Command {

  /**
   * Validates that the correct number of arguments is provided.
   *
   * @param args Provided arguments
   * @param expectedCount Expected number of arguments
   * @param commandName Name of the command for error messages
   * @throws IllegalArgumentException if argument count is incorrect
   */
  protected void validateArgumentCount(String[] args, int expectedCount, String commandName) {
    if (args.length != expectedCount) {
      throw new IllegalArgumentException(
          String.format(
              "Invalid number of arguments for %s. Expected: %d, Provided: %d. Usage: %s",
              commandName, expectedCount, args.length, getUsage()));
    }
  }

  /**
   * Validates that at least the minimum number of arguments is provided.
   *
   * @param args Provided arguments
   * @param minCount Minimum number of arguments
   * @param commandName Name of the command for error messages
   * @throws IllegalArgumentException if not enough arguments provided
   */
  protected void validateMinimumArgumentCount(String[] args, int minCount, String commandName) {
    if (args.length < minCount) {
      throw new IllegalArgumentException(
          String.format(
              "Insufficient arguments for %s. Expected at least: %d, Provided: %d. Usage: %s",
              commandName, minCount, args.length, getUsage()));
    }
  }

  /**
   * Validates that an argument is not null or empty.
   *
   * @param arg Argument to validate
   * @param argName Name of the argument for error messages
   * @throws IllegalArgumentException if argument is invalid
   */
  protected void validateNotEmpty(String arg, String argName) {
    if (arg == null || arg.trim().isEmpty()) {
      throw new IllegalArgumentException(argName + " cannot be null or empty");
    }
  }

  /**
   * Safely trims a string argument.
   *
   * @param arg String to trim
   * @return Trimmed string or null if input was null
   */
  protected String safeTrim(String arg) {
    return arg != null ? arg.trim() : null;
  }

  /**
   * Converts a string to uppercase safely.
   *
   * @param arg String to convert
   * @return Uppercase string or null if input was null
   */
  protected String safeUpperCase(String arg) {
    return arg != null ? arg.trim().toUpperCase() : null;
  }

  /**
   * Formats error messages consistently.
   *
   * @param commandName Name of the command
   * @param error Error description
   * @return Formatted error message
   */
  protected String formatError(String commandName, String error) {
    return String.format("Error executing %s: %s", commandName, error);
  }

  /**
   * Formats success messages consistently.
   *
   * @param commandName Name of the command
   * @param result Success description
   * @return Formatted success message
   */
  protected String formatSuccess(String commandName, String result) {
    return String.format("%s completed successfully: %s", commandName, result);
  }
}
