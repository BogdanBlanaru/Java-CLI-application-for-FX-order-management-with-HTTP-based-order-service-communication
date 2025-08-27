package com.profidata.orderbook.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class DateUtilsTest {

  @Test
  void shouldFormatDateToString() {
    LocalDate date = LocalDate.of(2025, 12, 31);
    String formatted = DateUtils.formatDate(date);

    assertThat(formatted).isEqualTo("31.12.2025");
  }

  @Test
  void shouldReturnNullForNullDateFormat() {
    assertThat(DateUtils.formatDate(null)).isNull();
  }

  @Test
  void shouldParseDateFromString() {
    LocalDate parsed = DateUtils.parseDate("31.12.2025");

    assertThat(parsed).isEqualTo(LocalDate.of(2025, 12, 31));
  }

  @Test
  void shouldReturnNullForNullDateString() {
    assertThat(DateUtils.parseDate(null)).isNull();
  }

  @Test
  void shouldDetectFutureDate() {
    LocalDate tomorrow = LocalDate.now().plusDays(1);
    String tomorrowStr = DateUtils.formatDate(tomorrow);

    assertThat(DateUtils.isFutureDate(tomorrowStr)).isTrue();
  }

  @Test
  void shouldDetectPastDate() {
    LocalDate yesterday = LocalDate.now().minusDays(1);
    String yesterdayStr = DateUtils.formatDate(yesterday);

    assertThat(DateUtils.isFutureDate(yesterdayStr)).isFalse();
  }

  @Test
  void shouldReturnFalseForInvalidDateString() {
    assertThat(DateUtils.isFutureDate("invalid-date")).isFalse();
  }

  @Test
  void shouldValidateOrderDateInRange() {
    LocalDate validDate = LocalDate.now().plusYears(1);
    assertThat(DateUtils.isValidOrderDate(validDate)).isTrue();
  }

  @Test
  void shouldRejectPastOrderDate() {
    LocalDate pastDate = LocalDate.now().minusDays(1);
    assertThat(DateUtils.isValidOrderDate(pastDate)).isFalse();
  }

  @Test
  void shouldRejectTooFarFutureOrderDate() {
    LocalDate farFuture = LocalDate.now().plusYears(15);
    assertThat(DateUtils.isValidOrderDate(farFuture)).isFalse();
  }

  @Test
  void shouldRejectNullOrderDate() {
    assertThat(DateUtils.isValidOrderDate(null)).isFalse();
  }

  @Test
  void shouldAcceptTodayAsValidOrderDate() {
    LocalDate today = LocalDate.now();
    assertThat(DateUtils.isValidOrderDate(today)).isTrue();
  }
}
