package com.profidata.orderbook.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class CollectionUtilsTest {

  @Test
  void shouldReturnTrueForNullCollection() {
    assertThat(CollectionUtils.isEmpty(null)).isTrue();
  }

  @Test
  void shouldReturnTrueForEmptyCollection() {
    assertThat(CollectionUtils.isEmpty(List.of())).isTrue();
  }

  @Test
  void shouldReturnFalseForNonEmptyCollection() {
    assertThat(CollectionUtils.isEmpty(List.of("item"))).isFalse();
  }

  @Test
  void shouldReturnTrueForNotEmptyCollection() {
    assertThat(CollectionUtils.isNotEmpty(List.of("item"))).isTrue();
  }

  @Test
  void shouldReturnFalseForEmptyCollectionInNotEmpty() {
    assertThat(CollectionUtils.isNotEmpty(List.of())).isFalse();
  }

  @Test
  void shouldGetFirstElementFromList() {
    var list = List.of("first", "second", "third");
    Optional<String> first = CollectionUtils.getFirst(list);

    assertThat(first).isPresent();
    assertThat(first.get()).isEqualTo("first");
  }

  @Test
  void shouldReturnEmptyForFirstOfEmptyList() {
    Optional<String> first = CollectionUtils.getFirst(List.of());
    assertThat(first).isEmpty();
  }

  @Test
  void shouldGetLastElementFromList() {
    var list = List.of("first", "second", "third");
    Optional<String> last = CollectionUtils.getLast(list);

    assertThat(last).isPresent();
    assertThat(last.get()).isEqualTo("third");
  }

  @Test
  void shouldReturnEmptyForLastOfEmptyList() {
    Optional<String> last = CollectionUtils.getLast(List.of());
    assertThat(last).isEmpty();
  }

  @Test
  void shouldPartitionListIntoChunks() {
    var list = List.of("a", "b", "c", "d", "e");
    var partitions = CollectionUtils.partition(list, 2);

    assertThat(partitions).hasSize(3);
    assertThat(partitions.get(0)).containsExactly("a", "b");
    assertThat(partitions.get(1)).containsExactly("c", "d");
    assertThat(partitions.get(2)).containsExactly("e");
  }

  @Test
  void shouldReturnEmptyForInvalidPartitionSize() {
    var list = List.of("a", "b", "c");
    assertThat(CollectionUtils.partition(list, 0)).isEmpty();
    assertThat(CollectionUtils.partition(list, -1)).isEmpty();
  }

  @Test
  void shouldCreateSafeCopyOfList() {
    var original = new ArrayList<String>();
    original.add("a");
    original.add("b");
    original.add("c");

    var copy = CollectionUtils.safeCopy(original);

    assertThat(copy).containsExactlyElementsOf(original);

    original.add("d");
    assertThat(original).hasSize(4);
    assertThat(copy).hasSize(3);

    assertThat(copy).isNotSameAs(original);
  }

  @Test
  void shouldReturnEmptyListForNullInput() {
    var copy = CollectionUtils.safeCopy(null);
    assertThat(copy).isEmpty();
  }
}
