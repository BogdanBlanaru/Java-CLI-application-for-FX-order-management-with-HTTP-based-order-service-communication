package com.profidata.orderbook.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class FormatUtilsTest {

  @Test
  void shouldCreateTableWithHeadersAndRows() {
    String[] headers = {"Name", "Age", "City"};
    List<String[]> rows =
        List.of(new String[] {"John", "25", "New York"}, new String[] {"Jane", "30", "London"});

    String table = FormatUtils.createTable(headers, rows);

    assertThat(table).contains("Name");
    assertThat(table).contains("John");
    assertThat(table).contains("Jane");
    assertThat(table).contains("-----"); // Separator line
  }

  @Test
  void shouldReturnEmptyStringForNullHeaders() {
    String table = FormatUtils.createTable(null, List.of());
    assertThat(table).isEmpty();
  }

  @Test
  void shouldReturnEmptyStringForEmptyHeaders() {
    String table = FormatUtils.createTable(new String[0], List.of());
    assertThat(table).isEmpty();
  }

  @Test
  void shouldFormatDecimalWithSpecifiedPlaces() {
    BigDecimal number = new BigDecimal("123.456789");
    String formatted = FormatUtils.formatDecimal(number, 2);

    assertThat(formatted).isEqualTo("123.46");
  }

  @Test
  void shouldReturnNAForNullDecimal() {
    String formatted = FormatUtils.formatDecimal(null, 2);
    assertThat(formatted).isEqualTo("N/A");
  }

  @Test
  void shouldTruncateTextWithEllipsis() {
    String text = "This is a very long text";
    String truncated = FormatUtils.truncate(text, 10);

    assertThat(truncated).hasSize(10);
    assertThat(truncated).endsWith("...");
  }

  @Test
  void shouldNotTruncateShortText() {
    String text = "Short";
    String result = FormatUtils.truncate(text, 10);

    assertThat(result).isEqualTo("Short");
  }

  @Test
  void shouldReturnEmptyStringForNullText() {
    String result = FormatUtils.truncate(null, 10);
    assertThat(result).isEmpty();
  }

  @Test
  void shouldCenterTextWithinWidth() {
    String centered = FormatUtils.center("Hello", 11);

    assertThat(centered).hasSize(11);
    assertThat(centered.trim()).isEqualTo("Hello");
    assertThat(centered).startsWith("   "); // 3 spaces padding
  }

  @Test
  void shouldReturnOriginalTextIfTooLong() {
    String text = "Very long text";
    String result = FormatUtils.center(text, 5);

    assertThat(result).isEqualTo(text);
  }

  @Test
  void shouldHandleNullTextInCenter() {
    String result = FormatUtils.center(null, 10);
    assertThat(result).hasSize(10);
    assertThat(result.trim()).isEmpty();
  }
}
