package com.profidata.orderbook.util;

import java.math.BigDecimal;
import java.util.List;

/**
 * Utility class for formatting output.
 *
 * @author Profidata Developer
 */
public final class FormatUtils {

  private FormatUtils() {
    // Utility class
  }

  /** Creates a formatted table with headers and data rows. */
  public static String createTable(String[] headers, List<String[]> rows) {
    if (headers == null || headers.length == 0) {
      return "";
    }

    // Calculate column widths
    int[] widths = calculateColumnWidths(headers, rows);

    StringBuilder sb = new StringBuilder();

    // Add header
    sb.append(formatRow(headers, widths)).append("\n");

    // Add separator
    sb.append(createSeparator(widths)).append("\n");

    // Add rows
    for (String[] row : rows) {
      sb.append(formatRow(row, widths)).append("\n");
    }

    return sb.toString();
  }

  /** Calculates optimal column widths for table formatting. */
  private static int[] calculateColumnWidths(String[] headers, List<String[]> rows) {
    int[] widths = new int[headers.length];

    // Initialize with header lengths
    for (int i = 0; i < headers.length; i++) {
      widths[i] = headers[i] != null ? headers[i].length() : 0;
    }

    // Check row data
    for (String[] row : rows) {
      for (int i = 0; i < Math.min(row.length, widths.length); i++) {
        if (row[i] != null) {
          widths[i] = Math.max(widths[i], row[i].length());
        }
      }
    }

    // Add padding
    for (int i = 0; i < widths.length; i++) {
      widths[i] += 2; // Add 2 spaces padding
    }

    return widths;
  }

  /** Formats a single table row. */
  private static String formatRow(String[] columns, int[] widths) {
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < columns.length; i++) {
      String column = columns[i] != null ? columns[i] : "";
      int width = i < widths.length ? widths[i] : column.length() + 2;

      sb.append(String.format("%-" + width + "s", column));
    }

    return sb.toString().trim();
  }

  /** Creates a separator line for tables. */
  private static String createSeparator(int[] widths) {
    return "-".repeat(java.util.Arrays.stream(widths).sum());
  }

  /** Formats a number with specified decimal places. */
  public static String formatDecimal(BigDecimal number, int decimalPlaces) {
    if (number == null) return "N/A";

    return String.format("%." + decimalPlaces + "f", number);
  }

  /** Truncates text to specified length with ellipsis. */
  public static String truncate(String text, int maxLength) {
    if (text == null) return "";
    if (text.length() <= maxLength) return text;

    return text.substring(0, maxLength - 3) + "...";
  }

  /** Centers text within specified width. */
  public static String center(String text, int width) {
    if (text == null) text = "";
    if (text.length() >= width) return text;

    int padding = width - text.length();
    int leftPadding = padding / 2;
    int rightPadding = padding - leftPadding;

    return " ".repeat(leftPadding) + text + " ".repeat(rightPadding);
  }
}
