package com.profidata.orderbook.cli;

import com.profidata.orderbook.exception.CommandParsingException;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.io.*;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Main command line interface for the FX OrderBook application.
 *
 * <p>This CLI provides: - Interactive command processing - Graceful shutdown handling - Error
 * recovery and user feedback - Metrics collection - Signal handling for clean termination
 *
 * @author Profidata Developer
 */
@Component
public class CommandLineInterface {

  private static final Logger LOGGER = LoggerFactory.getLogger(CommandLineInterface.class);

  private static final String WELCOME_MESSAGE =
      """
            ╔══════════════════════════════════════════════════════════════╗
            ║                    FX OrderBook CLI v1.0.0                  ║
            ║                                                              ║
            ║  Welcome to the advanced FX OrderBook command line tool!    ║
            ║  Type 'help' for available commands or 'exit' to quit.      ║
            ║                                                              ║
            ║  Ready to process your FX orders...                         ║
            ╚══════════════════════════════════════════════════════════════╝
            """;

  private static final String PROMPT = "fx-orderbook> ";
  private static final String GOODBYE_MESSAGE = "Thank you for using FX OrderBook CLI. Goodbye!";

  private final CommandParser commandParser;
  private final MeterRegistry meterRegistry;
  private final AtomicBoolean running;

  // Metrics
  private final Counter commandsProcessedCounter;
  private final Counter commandErrorsCounter;

  // I/O streams - configurable for testing
  private final BufferedReader reader;
  private final PrintWriter writer;
  private final PrintWriter errorWriter;

  @Autowired
  public CommandLineInterface(CommandParser commandParser, MeterRegistry meterRegistry) {
    this.commandParser = commandParser;
    this.meterRegistry = meterRegistry;
    this.reader = new BufferedReader(new InputStreamReader(System.in));
    this.writer = new PrintWriter(System.out, true);
    this.errorWriter = new PrintWriter(System.err, true);
    this.running = new AtomicBoolean(false);

    // Initialize metrics
    this.commandsProcessedCounter =
        meterRegistry != null
            ? Counter.builder("cli.commands.processed").register(meterRegistry)
            : null;
    this.commandErrorsCounter =
        meterRegistry != null
            ? Counter.builder("cli.commands.errors").register(meterRegistry)
            : null;

    // Setup shutdown hook for graceful termination
    setupShutdownHook();
  }

  /** Starts the interactive CLI session. */
  public void start() {
    LOGGER.info("Starting FX OrderBook CLI");
    running.set(true);

    try {
      showWelcomeMessage();
      runInteractiveSession();
    } catch (Exception e) {
      LOGGER.error("Unexpected error in CLI", e);
      errorWriter.println("An unexpected error occurred: " + e.getMessage());
    } finally {
      shutdown();
    }
  }

  /** Runs the main interactive command processing loop. */
  private void runInteractiveSession() {
    while (running.get()) {
      try {
        writer.print(PROMPT);
        writer.flush();

        String input = reader.readLine();

        // Handle EOF (Ctrl+D) or null input
        if (input == null) {
          writer.println(); // New line for better formatting
          LOGGER.info("EOF received, shutting down CLI");
          break;
        }

        // Process the command
        processCommand(input.trim());

      } catch (IOException e) {
        LOGGER.error("I/O error reading command", e);
        errorWriter.println("Error reading input: " + e.getMessage());
        break;
      }
    }
  }

  /** Processes a single command input. */
  private void processCommand(String input) {
    if (input.isEmpty()) {
      return; // Skip empty inputs
    }

    try {
      LOGGER.debug("Processing command: {}", input);

      String result = commandParser.parseAndExecute(input);

      // Handle special system commands
      if ("SYSTEM_EXIT".equals(result)) {
        writer.println(GOODBYE_MESSAGE);
        stop();
        return;
      }

      // Display command result
      writer.println(result);

      // Update metrics
      incrementCounter(commandsProcessedCounter);

    } catch (CommandParsingException e) {
      LOGGER.warn("Command parsing error: {}", e.getMessage());
      errorWriter.println("Command error: " + e.getMessage());
      incrementCounter(commandErrorsCounter);

    } catch (Exception e) {
      LOGGER.error("Unexpected error processing command: {}", input, e);
      errorWriter.println("Unexpected error: " + e.getMessage());
      errorWriter.println("Please try again or type 'help' for available commands.");
      incrementCounter(commandErrorsCounter);
    }
  }

  /**
   * Shows the welcome message and initial status.
   *
   * <p>Quick connection test here - saves users from typing commands just to find out the service
   * is down
   */
  private void showWelcomeMessage() {
    writer.println(WELCOME_MESSAGE);

    // Show connection status
    writer.println("Checking connection to order service...");

    try {
      // Quick health check to verify connectivity
      commandParser.parseAndExecute("rates");
      writer.println("✓ Successfully connected to order service");
    } catch (Exception e) {
      writer.println("⚠ Warning: Unable to connect to order service");
      writer.println("  Please ensure the order service is running on the configured URL");
      writer.println("  You can still use commands, but they may fail until service is available");
    }

    writer.println();
  }

  /** Stops the CLI gracefully. */
  public void stop() {
    LOGGER.info("Stopping FX OrderBook CLI");
    running.set(false);
  }

  /** Performs cleanup and shutdown operations. */
  private void shutdown() {
    LOGGER.info("Shutting down FX OrderBook CLI");

    try {
      if (reader != null) {
        reader.close();
      }
    } catch (IOException e) {
      LOGGER.warn("Error closing input reader", e);
    }

    if (writer != null) {
      writer.close();
    }

    if (errorWriter != null && errorWriter != writer) {
      errorWriter.close();
    }

    LOGGER.info("FX OrderBook CLI shutdown complete");
  }

  /** Sets up shutdown hook for clean termination on SIGTERM/SIGINT. */
  private void setupShutdownHook() {
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  if (running.get()) {
                    LOGGER.info("Shutdown signal received, stopping CLI");
                    stop();

                    // Give a moment for graceful shutdown
                    try {
                      Thread.sleep(100);
                    } catch (InterruptedException e) {
                      Thread.currentThread().interrupt();
                    }
                  }
                },
                "CLI-Shutdown-Hook"));
  }

  /** Safely increments a counter with null check. */
  private void incrementCounter(Counter counter) {
    if (counter != null) {
      counter.increment();
    }
  }

  /** Checks if the CLI is currently running. */
  public boolean isRunning() {
    return running.get();
  }

  /** Gets the command parser (for testing). */
  public CommandParser getCommandParser() {
    return commandParser;
  }
}
