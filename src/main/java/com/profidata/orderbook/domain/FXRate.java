package com.profidata.orderbook.domain;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;

/** Domain entity representing an FX rate. */
public record FXRate(CurrencyPair ccyPair, BigDecimal bid, BigDecimal ask) {

  private static final MathContext MATH_CONTEXT = new MathContext(10, RoundingMode.HALF_UP);
  private static final int PRICE_SCALE = 6;

  public FXRate {
    Objects.requireNonNull(ccyPair, "Currency pair cannot be null");
    Objects.requireNonNull(bid, "Bid price cannot be null");
    Objects.requireNonNull(ask, "Ask price cannot be null");

    if (bid.compareTo(ask) > 0) {
      throw new IllegalArgumentException("Bid price cannot be higher than ask price");
    }

    bid = bid.setScale(PRICE_SCALE, RoundingMode.HALF_UP);
    ask = ask.setScale(PRICE_SCALE, RoundingMode.HALF_UP);
  }

  public BigDecimal getMidPrice() {
    return bid.add(ask)
        .divide(BigDecimal.valueOf(2), MATH_CONTEXT)
        .setScale(PRICE_SCALE, RoundingMode.HALF_UP);
  }

  public BigDecimal getSpread() {
    return ask.subtract(bid).setScale(PRICE_SCALE, RoundingMode.HALF_UP);
  }

  public BigDecimal getSpreadPercentage() {
    BigDecimal spread = getSpread();
    BigDecimal midPrice = getMidPrice();

    if (midPrice.equals(BigDecimal.ZERO)) {
      return BigDecimal.ZERO;
    }

    return spread
        .divide(midPrice, MATH_CONTEXT)
        .multiply(BigDecimal.valueOf(100))
        .setScale(4, RoundingMode.HALF_UP);
  }

  public FXRate inverse() {
    BigDecimal inverseBid = BigDecimal.ONE.divide(ask, MATH_CONTEXT);
    BigDecimal inverseAsk = BigDecimal.ONE.divide(bid, MATH_CONTEXT);

    return new FXRate(ccyPair.inverse(), inverseBid, inverseAsk);
  }

  public BigDecimal getRateForOrder(boolean isBuyOrder) {
    return isBuyOrder ? ask : bid;
  }

  public boolean isValid() {
    return bid != null
        && ask != null
        && bid.compareTo(BigDecimal.ZERO) > 0
        && ask.compareTo(BigDecimal.ZERO) > 0
        && bid.compareTo(ask) <= 0;
  }

  public boolean matchesCurrencyPair(CurrencyPair pair) {
    return ccyPair.matches(pair);
  }
}
