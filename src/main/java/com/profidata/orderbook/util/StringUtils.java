package com.profidata.orderbook.util;

/**
 * Utility class for string operations.
 *
 * @author Profidata Developer
 */
public final class StringUtils {

  private StringUtils() {
    // Utility class
  }

  /** Checks if a string is null, empty, or contains only whitespace. */
  public static boolean isBlank(String str) {
    return str == null || str.trim().isEmpty();
  }

  /** Checks if a string is not blank. */
  public static boolean isNotBlank(String str) {
    return !isBlank(str);
  }

  /** Capitalizes the first letter of a string. */
  public static String capitalize(String str) {
    if (isBlank(str)) return str;

    return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
  }

  /** Repeats a string a specified number of times. */
  public static String repeat(String str, int count) {
    if (str == null || count <= 0) return "";

    return str.repeat(count);
  }

  /** Pads a string to the left with specified character. */
  public static String padLeft(String str, int totalLength, char padChar) {
    if (str == null) str = "";
    if (str.length() >= totalLength) return str;

    return String.valueOf(padChar).repeat(totalLength - str.length()) + str;
  }

  /** Pads a string to the right with specified character. */
  public static String padRight(String str, int totalLength, char padChar) {
    if (str == null) str = "";
    if (str.length() >= totalLength) return str;

    return str + String.valueOf(padChar).repeat(totalLength - str.length());
  }

  /** Removes all whitespace from a string. */
  public static String removeWhitespace(String str) {
    if (str == null) return null;
    return str.replaceAll("\\s+", "");
  }

  /** Masks sensitive information in a string. */
  public static String maskSensitive(String str, int visibleChars) {
    if (isBlank(str)) return str;
    if (str.length() <= visibleChars) return "*".repeat(str.length());

    return str.substring(0, visibleChars) + "*".repeat(str.length() - visibleChars);
  }
}
