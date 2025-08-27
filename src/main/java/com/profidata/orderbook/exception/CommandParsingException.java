package com.profidata.orderbook.exception;

/** Exception thrown for command parsing errors. */
public class CommandParsingException extends OrderBookException {

  public CommandParsingException(String message) {
    super(message);
  }

  public CommandParsingException(String message, Throwable cause) {
    super(message, cause);
  }
}
