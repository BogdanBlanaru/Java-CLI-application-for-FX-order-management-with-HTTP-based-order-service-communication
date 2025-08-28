package com.profidata.orderbook;

import org.junit.jupiter.api.Test;

/** Simple smoke test without Spring context complexity. */
class FXOrderBookApplicationTests {

  @Test
  void applicationClassExists() {
    try {
      Class.forName("com.profidata.orderbook.FXOrderBookApplication");
      var mainMethod = FXOrderBookApplication.class.getMethod("main", String[].class);
      assert mainMethod != null;
    } catch (Exception e) {
      throw new AssertionError("Application class not properly configured", e);
    }
  }
}
