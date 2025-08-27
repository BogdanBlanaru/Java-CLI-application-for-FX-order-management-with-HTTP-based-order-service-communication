package com.profidata.orderbook;

import com.profidata.orderbook.cli.CommandLineInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/** Main application class for FX OrderBook CLI. */
@SpringBootApplication
public class FXOrderBookApplication implements CommandLineRunner {

  private static final Logger LOGGER = LoggerFactory.getLogger(FXOrderBookApplication.class);

  private final ApplicationContext applicationContext;

  public FXOrderBookApplication(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  public static void main(String[] args) {
    try {
      LOGGER.info("Starting FX OrderBook CLI Application");

      // Disable Spring Boot web environment
      System.setProperty("spring.main.web-application-type", "none");

      SpringApplication.run(FXOrderBookApplication.class, args);

    } catch (Exception e) {
      LOGGER.error("Fatal error in FX OrderBook CLI", e);
      System.err.println("Application failed to start: " + e.getMessage());
      System.exit(1);
    }
  }

  @Override
  public void run(String... args) throws Exception {
    // Get CLI from context and start interactive session
    CommandLineInterface cli = applicationContext.getBean(CommandLineInterface.class);
    cli.start();
  }
}
