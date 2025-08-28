package com.profidata.orderbook.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for date operations.
 *
 * @author Profidata Developer
 */
public final class DateUtils {

  public static final DateTimeFormatter DEFAULT_DATE_FORMATTER =
      DateTimeFormatter.ofPattern("dd.MM.yyyy");

  private DateUtils() {}

  /** Formats a LocalDate to string using default formatter. */
  public static String formatDate(LocalDate date) {
    return date != null ? date.format(DEFAULT_DATE_FORMATTER) : null;
  }

  /** Parses a date string using default formatter. */
  public static LocalDate parseDate(String dateString) {
    return dateString != null ? LocalDate.parse(dateString, DEFAULT_DATE_FORMATTER) : null;
  }

  /** Checks if a date string represents a future date. */
  public static boolean isFutureDate(String dateString) {
    try {
      LocalDate date = parseDate(dateString);
      return date != null && date.isAfter(LocalDate.now());
    } catch (Exception e) {
      return false;
    }
  }

  /** Checks if a date is within a valid range for orders. */
  public static boolean isValidOrderDate(LocalDate date) {
    if (date == null) return false;

    LocalDate now = LocalDate.now();
    LocalDate maxFutureDate = now.plusYears(10);

    return !date.isBefore(now) && !date.isAfter(maxFutureDate);
  }
}
