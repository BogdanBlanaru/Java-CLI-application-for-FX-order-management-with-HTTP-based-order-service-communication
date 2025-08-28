package com.profidata.orderbook.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class ValidationUtilsTest {

  @Test
  void shouldValidateCorrectCurrencyCode() {
    ValidationUtils.validateCurrencyCode("USD", "currency");
    ValidationUtils.validateCurrencyCode("EUR", "currency");
    ValidationUtils.validateCurrencyCode("GBP", "currency");
  }

  @Test
  void shouldThrowExceptionForNullCurrencyCode() {
    assertThatThrownBy(() -> ValidationUtils.validateCurrencyCode(null, "currency"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("currency cannot be null or empty");
  }

  @Test
  void shouldThrowExceptionForEmptyCurrencyCode() {
    assertThatThrownBy(() -> ValidationUtils.validateCurrencyCode("", "currency"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("currency cannot be null or empty");
  }

  @Test
  void shouldThrowExceptionForInvalidCurrencyCodeFormat() {
    assertThatThrownBy(() -> ValidationUtils.validateCurrencyCode("US", "currency"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("currency must be a 3-letter currency code, got: US");
  }

  @Test
  void shouldValidateCorrectDateFormat() {
    LocalDate result = ValidationUtils.validateDateFormat("31.12.2025", "validUntil");
    assertThat(result).isEqualTo(LocalDate.of(2025, 12, 31));
  }

  @Test
  void shouldThrowExceptionForInvalidDateFormat() {
    assertThatThrownBy(() -> ValidationUtils.validateDateFormat("2025-12-31", "validUntil"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("validUntil must be in format dd.MM.yyyy");
  }

  @Test
  void shouldThrowExceptionForNullDate() {
    assertThatThrownBy(() -> ValidationUtils.validateDateFormat(null, "validUntil"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("validUntil cannot be null or empty");
  }

  @Test
  void shouldValidateFutureDate() {
    LocalDate futureDate = LocalDate.now().plusDays(1);
    ValidationUtils.validateNotInPast(futureDate, "validUntil");
  }

  @Test
  void shouldThrowExceptionForPastDate() {
    LocalDate pastDate = LocalDate.now().minusDays(1);
    assertThatThrownBy(() -> ValidationUtils.validateNotInPast(pastDate, "validUntil"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("validUntil cannot be in the past");
  }

  @Test
  void shouldValidatePositiveNumber() {
    ValidationUtils.validatePositiveNumber("1.20", "limit");
    ValidationUtils.validatePositiveNumber("100", "limit");
  }

  @Test
  void shouldThrowExceptionForNegativeNumber() {
    assertThatThrownBy(() -> ValidationUtils.validatePositiveNumber("-1.20", "limit"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("limit must be positive, got: -1.20");
  }

  @Test
  void shouldThrowExceptionForZero() {
    assertThatThrownBy(() -> ValidationUtils.validatePositiveNumber("0", "limit"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("limit must be positive, got: 0");
  }

  @Test
  void shouldThrowExceptionForInvalidNumber() {
    assertThatThrownBy(() -> ValidationUtils.validatePositiveNumber("abc", "limit"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("limit must be a valid number, got: abc");
  }

  @Test
  void shouldValidateNotEmptyString() {
    ValidationUtils.validateNotEmpty("test", "field");
    ValidationUtils.validateNotEmpty("  test  ", "field");
  }

  @Test
  void shouldThrowExceptionForEmptyString() {
    assertThatThrownBy(() -> ValidationUtils.validateNotEmpty("", "field"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("field cannot be null or empty");
  }

  @Test
  void shouldValidateOrderId() {
    ValidationUtils.validateOrderId("123");
    ValidationUtils.validateOrderId("order-123");
    ValidationUtils.validateOrderId("order_123");
  }

  @Test
  void shouldThrowExceptionForInvalidOrderId() {
    assertThatThrownBy(() -> ValidationUtils.validateOrderId("order@123"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Order ID can only contain letters, numbers, hyphens, and underscores");
  }

  @Test
  void shouldSafeTrimString() {
    assertThat(ValidationUtils.safeTrim("  test  ")).isEqualTo("test");
    assertThat(ValidationUtils.safeTrim(null)).isNull();
  }

  @Test
  void shouldSafeUpperCaseString() {
    assertThat(ValidationUtils.safeUpperCase("  usd  ")).isEqualTo("USD");
    assertThat(ValidationUtils.safeUpperCase(null)).isNull();
  }

  @Test
  void shouldValidateDifferentCurrencies() {
    ValidationUtils.validateDifferentCurrencies("USD", "EUR");
    ValidationUtils.validateDifferentCurrencies(null, "EUR");
    ValidationUtils.validateDifferentCurrencies("USD", null);
  }

  @Test
  void shouldThrowExceptionForSameCurrencies() {
    assertThatThrownBy(() -> ValidationUtils.validateDifferentCurrencies("USD", "USD"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Currency codes must be different, got: USD and USD");
  }
}
