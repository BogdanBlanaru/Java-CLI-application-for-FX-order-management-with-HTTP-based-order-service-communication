package com.profidata.orderbook.util;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Utility class for collection operations.
 *
 * @author Profidata Developer
 */
public final class CollectionUtils {

  private CollectionUtils() {}

  /** Checks if a collection is null or empty. */
  public static boolean isEmpty(Collection<?> collection) {
    return collection == null || collection.isEmpty();
  }

  /** Checks if a collection is not null and not empty. */
  public static boolean isNotEmpty(Collection<?> collection) {
    return !isEmpty(collection);
  }

  /** Gets the first element of a list safely. */
  public static <T> Optional<T> getFirst(List<T> list) {
    return isEmpty(list) ? Optional.empty() : Optional.ofNullable(list.get(0));
  }

  /** Gets the last element of a list safely. */
  public static <T> Optional<T> getLast(List<T> list) {
    return isEmpty(list) ? Optional.empty() : Optional.ofNullable(list.get(list.size() - 1));
  }

  /** Partitions a list into smaller chunks. */
  public static <T> List<List<T>> partition(List<T> list, int chunkSize) {
    if (isEmpty(list) || chunkSize <= 0) {
      return List.of();
    }

    return IntStream.range(0, (list.size() + chunkSize - 1) / chunkSize)
        .mapToObj(i -> list.subList(i * chunkSize, Math.min((i + 1) * chunkSize, list.size())))
        .collect(Collectors.toList());
  }

  /** Creates a safe copy of a list. */
  public static <T> List<T> safeCopy(List<T> list) {
    return list != null ? List.copyOf(list) : List.of();
  }
}
