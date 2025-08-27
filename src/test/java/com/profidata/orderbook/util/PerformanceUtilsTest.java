package com.profidata.orderbook.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;

class PerformanceUtilsTest {

  @Test
  void shouldMeasureExecutionTimeForSupplier() {
    var result = PerformanceUtils.timeExecution(() -> "test result");

    assertThat(result.isSuccess()).isTrue();
    assertThat(result.result()).isEqualTo("test result");
    assertThat(result.durationNanos()).isPositive();
    assertThat(result.exception()).isNull();
  }

  @Test
  void shouldMeasureExecutionTimeForRunnable() {
    AtomicBoolean executed = new AtomicBoolean(false);

    var result = PerformanceUtils.timeExecution(() -> executed.set(true));

    assertThat(result.isSuccess()).isTrue();
    assertThat(result.result()).isNull(); // Runnable returns void
    assertThat(result.durationNanos()).isPositive();
    assertThat(executed.get()).isTrue();
  }

  @Test
  void shouldCaptureExceptionDuringExecution() {
    var result =
        PerformanceUtils.timeExecution(
            () -> {
              throw new RuntimeException("Test exception");
            });

    assertThat(result.isSuccess()).isFalse();
    assertThat(result.result()).isNull();
    assertThat(result.exception()).isNotNull();
    assertThat(result.exception().getMessage()).isEqualTo("Test exception");
    assertThat(result.durationNanos()).isPositive();
  }

  @Test
  void shouldFormatNanoseconds() {
    String formatted = PerformanceUtils.formatDuration(500);
    assertThat(formatted).isEqualTo("500ns");
  }

  @Test
  void shouldFormatMicroseconds() {
    String formatted = PerformanceUtils.formatDuration(5000); // 5000ns = 5μs
    assertThat(formatted).isEqualTo("5.00μs");
  }

  @Test
  void shouldFormatMilliseconds() {
    String formatted = PerformanceUtils.formatDuration(5_000_000); // 5ms
    assertThat(formatted).isEqualTo("5.00ms");
  }

  @Test
  void shouldFormatSeconds() {
    String formatted = PerformanceUtils.formatDuration(2_000_000_000L); // 2s
    assertThat(formatted).isEqualTo("2.00s");
  }

  @Test
  void shouldCalculateDurationInMs() {
    var result = PerformanceUtils.timeExecution(() -> "test");
    double durationMs = result.getDurationMs();

    assertThat(durationMs).isPositive();
    assertThat(durationMs).isEqualTo(result.durationNanos() / 1_000_000.0);
  }

  @Test
  void shouldFormatDurationFromTimedResult() {
    var result = PerformanceUtils.timeExecution(() -> "test");
    String formatted = result.getFormattedDuration();

    assertThat(formatted).matches("\\d+\\.\\d{2}(ns|μs|ms|s)");
  }
}
