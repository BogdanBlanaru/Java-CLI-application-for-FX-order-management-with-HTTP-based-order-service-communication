package com.profidata.orderbook.exception;

/** Exception thrown when the order service is unavailable. */
public class ServiceUnavailableException extends OrderBookException {

  public ServiceUnavailableException(String message) {
    super(message);
  }

  public ServiceUnavailableException(String message, Throwable cause) {
    super(message, cause);
  }
}
