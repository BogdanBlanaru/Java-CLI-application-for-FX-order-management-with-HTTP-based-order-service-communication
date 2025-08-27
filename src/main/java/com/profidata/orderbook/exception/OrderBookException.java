package com.profidata.orderbook.exception;

/**
 * Base exception for all OrderBook application errors.
 *
 * @author Profidata Developer
 */
public class OrderBookException extends RuntimeException {

  public OrderBookException(String message) {
    super(message);
  }

  public OrderBookException(String message, Throwable cause) {
    super(message, cause);
  }

  public OrderBookException(Throwable cause) {
    super(cause);
  }
}
