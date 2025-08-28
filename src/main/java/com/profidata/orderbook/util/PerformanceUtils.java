package com.profidata.orderbook.util;

import java.util.function.Supplier;

/**
 * Utility class for performance monitoring and metrics.
 *
 * @author Profidata Developer
 */
public final class PerformanceUtils {

  private PerformanceUtils() {}

  /** Executes a task and measures its execution time. */
  public static <T> TimedResult<T> timeExecution(Supplier<T> task) {
    long startTime = System.nanoTime();
    try {
      T result = task.get();
      long duration = System.nanoTime() - startTime;
      return new TimedResult<>(result, duration, null);
    } catch (Exception e) {
      long duration = System.nanoTime() - startTime;
      return new TimedResult<>(null, duration, e);
    }
  }

  /** Executes a task and measures its execution time (void version). */
  public static TimedResult<Void> timeExecution(Runnable task) {
    return timeExecution(
        () -> {
          task.run();
          return null;
        });
  }

  /** Formats duration in human-readable format. */
  public static String formatDuration(long nanos) {
    if (nanos < 1_000) {
      return nanos + "ns";
    } else if (nanos < 1_000_000) {
      return String.format("%.2fμs", nanos / 1_000.0);
    } else if (nanos < 1_000_000_000) {
      return String.format("%.2fms", nanos / 1_000_000.0);
    } else {
      return String.format("%.2fs", nanos / 1_000_000_000.0);
    }
  }

  /** Result container with timing information. */
  public record TimedResult<T>(T result, long durationNanos, Exception exception) {

    public boolean isSuccess() {
      return exception == null;
    }

    public String getFormattedDuration() {
      return formatDuration(durationNanos);
    }

    public double getDurationMs() {
      return durationNanos / 1_000_000.0;
    }
  }
}
