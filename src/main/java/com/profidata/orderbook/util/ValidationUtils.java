package com.profidata.orderbook.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Utility class for common validation operations.
 *
 * @author Profidata Developer
 */
public final class ValidationUtils {

  private static final Pattern CURRENCY_CODE_PATTERN = Pattern.compile("^[A-Z]{3}$");
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

  /** Common currency codes for validation. */
  private static final Set<String> COMMON_CURRENCY_CODES =
      Set.of(
          "USD", "EUR", "GBP", "JPY", "CHF", "CAD", "AUD", "NZD", "SEK", "NOK", "DKK", "PLN", "CZK",
          "HUF", "RUB", "CNY", "KRW", "INR", "BRL", "MXN", "ZAR", "TRY", "SGD", "HKD", "ILS", "THB",
          "MYR", "PHP", "IDR", "VND");

  private ValidationUtils() {
    // Utility class - prevent instantiation
  }

  /**
   * Validates a currency code format and common usage.
   *
   * @param currencyCode Currency code to validate
   * @param fieldName Name of the field for error messages
   * @throws IllegalArgumentException if currency code is invalid
   */
  public static void validateCurrencyCode(String currencyCode, String fieldName) {
    if (currencyCode == null || currencyCode.trim().isEmpty()) {
      throw new IllegalArgumentException(fieldName + " cannot be null or empty");
    }

    String normalizedCode = currencyCode.trim().toUpperCase();

    if (!CURRENCY_CODE_PATTERN.matcher(normalizedCode).matches()) {
      throw new IllegalArgumentException(
          fieldName + " must be a 3-letter currency code, got: " + currencyCode);
    }

    // Optional: warn about uncommon currency codes
    if (!COMMON_CURRENCY_CODES.contains(normalizedCode)) {
      // For now, just accept it - we could log a warning in production
      // LOGGER.warn("Uncommon currency code used: {}", normalizedCode);
    }
  }

  /**
   * Validates a date string in dd.MM.yyyy format.
   *
   * @param dateStr Date string to validate
   * @param fieldName Name of the field for error messages
   * @return Parsed LocalDate
   * @throws IllegalArgumentException if date is invalid
   */
  public static LocalDate validateDateFormat(String dateStr, String fieldName) {
    if (dateStr == null || dateStr.trim().isEmpty()) {
      throw new IllegalArgumentException(fieldName + " cannot be null or empty");
    }

    try {
      return LocalDate.parse(dateStr.trim(), DATE_FORMATTER);
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException(
          fieldName + " must be in format dd.MM.yyyy, got: " + dateStr, e);
    }
  }

  /**
   * Validates that a date is not in the past.
   *
   * @param date Date to validate
   * @param fieldName Name of the field for error messages
   * @throws IllegalArgumentException if date is in the past
   */
  public static void validateNotInPast(LocalDate date, String fieldName) {
    if (date == null) {
      throw new IllegalArgumentException(fieldName + " cannot be null");
    }

    if (date.isBefore(LocalDate.now())) {
      throw new IllegalArgumentException(
          fieldName + " cannot be in the past, got: " + date.format(DATE_FORMATTER));
    }
  }

  /**
   * Validates that a string represents a valid positive number.
   *
   * @param numberStr String to validate
   * @param fieldName Name of the field for error messages
   * @throws IllegalArgumentException if string is not a valid positive number
   */
  public static void validatePositiveNumber(String numberStr, String fieldName) {
    if (numberStr == null || numberStr.trim().isEmpty()) {
      throw new IllegalArgumentException(fieldName + " cannot be null or empty");
    }

    try {
      double value = Double.parseDouble(numberStr.trim());
      if (value <= 0) {
        throw new IllegalArgumentException(fieldName + " must be positive, got: " + numberStr);
      }
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(
          fieldName + " must be a valid number, got: " + numberStr, e);
    }
  }

  /**
   * Validates that a string is not null or empty after trimming.
   *
   * @param str String to validate
   * @param fieldName Name of the field for error messages
   * @throws IllegalArgumentException if string is invalid
   */
  public static void validateNotEmpty(String str, String fieldName) {
    if (str == null || str.trim().isEmpty()) {
      throw new IllegalArgumentException(fieldName + " cannot be null or empty");
    }
  }

  /**
   * Validates an order ID format.
   *
   * @param orderId Order ID to validate
   * @throws IllegalArgumentException if order ID is invalid
   */
  public static void validateOrderId(String orderId) {
    validateNotEmpty(orderId, "Order ID");

    String trimmed = orderId.trim();
    if (trimmed.length() > 50) {
      throw new IllegalArgumentException("Order ID too long, maximum 50 characters");
    }

    // Order IDs should be alphanumeric (allowing hyphens and underscores)
    if (!trimmed.matches("^[a-zA-Z0-9_-]+$")) {
      throw new IllegalArgumentException(
          "Order ID can only contain letters, numbers, hyphens, and underscores");
    }
  }

  /**
   * Safely trims a string.
   *
   * @param str String to trim
   * @return Trimmed string or null if input was null
   */
  public static String safeTrim(String str) {
    return str != null ? str.trim() : null;
  }

  /**
   * Safely converts a string to uppercase.
   *
   * @param str String to convert
   * @return Uppercase string or null if input was null
   */
  public static String safeUpperCase(String str) {
    return str != null ? str.trim().toUpperCase() : null;
  }

  /**
   * Validates that two currency codes are different.
   *
   * @param ccy1 First currency code
   * @param ccy2 Second currency code
   * @throws IllegalArgumentException if currencies are the same
   */
  public static void validateDifferentCurrencies(String ccy1, String ccy2) {
    if (ccy1 != null && ccy2 != null && ccy1.equals(ccy2)) {
      throw new IllegalArgumentException(
          "Currency codes must be different, got: " + ccy1 + " and " + ccy2);
    }
  }
}
