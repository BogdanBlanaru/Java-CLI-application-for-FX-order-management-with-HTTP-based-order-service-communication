package com.profidata.orderbook.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class StringUtilsTest {

  @Test
  void shouldDetectBlankStrings() {
    assertThat(StringUtils.isBlank(null)).isTrue();
    assertThat(StringUtils.isBlank("")).isTrue();
    assertThat(StringUtils.isBlank("   ")).isTrue();
    assertThat(StringUtils.isBlank("text")).isFalse();
  }

  @Test
  void shouldDetectNotBlankStrings() {
    assertThat(StringUtils.isNotBlank("text")).isTrue();
    assertThat(StringUtils.isNotBlank(null)).isFalse();
    assertThat(StringUtils.isNotBlank("")).isFalse();
    assertThat(StringUtils.isNotBlank("   ")).isFalse();
  }

  @Test
  void shouldCapitalizeFirstLetter() {
    assertThat(StringUtils.capitalize("hello")).isEqualTo("Hello");
    assertThat(StringUtils.capitalize("HELLO")).isEqualTo("Hello");
    assertThat(StringUtils.capitalize("hELLO")).isEqualTo("Hello");
  }

  @Test
  void shouldHandleBlankStringInCapitalize() {
    assertThat(StringUtils.capitalize(null)).isNull();
    assertThat(StringUtils.capitalize("")).isEmpty();
    assertThat(StringUtils.capitalize("   ")).isEqualTo("   ");
  }

  @Test
  void shouldRepeatString() {
    assertThat(StringUtils.repeat("abc", 3)).isEqualTo("abcabcabc");
    assertThat(StringUtils.repeat("x", 5)).isEqualTo("xxxxx");
  }

  @Test
  void shouldReturnEmptyForInvalidRepeat() {
    assertThat(StringUtils.repeat("abc", 0)).isEmpty();
    assertThat(StringUtils.repeat("abc", -1)).isEmpty();
    assertThat(StringUtils.repeat(null, 3)).isEmpty();
  }

  @Test
  void shouldPadLeft() {
    assertThat(StringUtils.padLeft("123", 6, '0')).isEqualTo("000123");
    assertThat(StringUtils.padLeft("hello", 8, '*')).isEqualTo("***hello");
  }

  @Test
  void shouldNotPadIfAlreadyLongEnough() {
    assertThat(StringUtils.padLeft("hello", 3, '0')).isEqualTo("hello");
  }

  @Test
  void shouldHandleNullInPadLeft() {
    assertThat(StringUtils.padLeft(null, 5, '0')).isEqualTo("00000");
  }

  @Test
  void shouldPadRight() {
    assertThat(StringUtils.padRight("123", 6, '0')).isEqualTo("123000");
    assertThat(StringUtils.padRight("hello", 8, '*')).isEqualTo("hello***");
  }

  @Test
  void shouldNotPadRightIfAlreadyLongEnough() {
    assertThat(StringUtils.padRight("hello", 3, '0')).isEqualTo("hello");
  }

  @Test
  void shouldHandleNullInPadRight() {
    assertThat(StringUtils.padRight(null, 5, '0')).isEqualTo("00000");
  }

  @Test
  void shouldRemoveAllWhitespace() {
    assertThat(StringUtils.removeWhitespace("hello world")).isEqualTo("helloworld");
    assertThat(StringUtils.removeWhitespace("  a b c  ")).isEqualTo("abc");
    assertThat(StringUtils.removeWhitespace("a\tb\nc\rd")).isEqualTo("abcd");
  }

  @Test
  void shouldReturnNullForNullInRemoveWhitespace() {
    assertThat(StringUtils.removeWhitespace(null)).isNull();
  }

  @Test
  void shouldMaskSensitiveInformation() {
    assertThat(StringUtils.maskSensitive("password123", 4)).isEqualTo("pass*******");
    assertThat(StringUtils.maskSensitive("secret", 2)).isEqualTo("se****");
  }

  @Test
  void shouldMaskEntireShortString() {
    assertThat(StringUtils.maskSensitive("abc", 5)).isEqualTo("***");
  }

  @Test
  void shouldHandleBlankInMask() {
    assertThat(StringUtils.maskSensitive(null, 3)).isNull();
    assertThat(StringUtils.maskSensitive("", 3)).isEmpty();
    assertThat(StringUtils.maskSensitive("   ", 1)).isEqualTo("   ");
  }
}
