package com.profidata.orderbook.exception;

/** Exception thrown for validation errors. */
public class ValidationException extends OrderBookException {

  public ValidationException(String message) {
    super(message);
  }

  public ValidationException(String message, Throwable cause) {
    super(message, cause);
  }
}
