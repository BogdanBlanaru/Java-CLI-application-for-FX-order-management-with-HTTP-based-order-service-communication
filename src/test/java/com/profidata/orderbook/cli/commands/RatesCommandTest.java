package com.profidata.orderbook.cli.commands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.profidata.orderbook.domain.CurrencyPair;
import com.profidata.orderbook.domain.FXRate;
import com.profidata.orderbook.service.RateService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RatesCommandTest {

  @Mock private RateService rateService;

  private RatesCommand command;

  @BeforeEach
  void setUp() {
    command = new RatesCommand(rateService);
  }

  @Test
  void shouldExecuteRatesCommand() throws Exception {
    // Given
    var rates =
        List.of(
            new FXRate(
                new CurrencyPair("EUR", "USD"), new BigDecimal("1.19"), new BigDecimal("1.21")));
    when(rateService.getCurrentRates()).thenReturn(rates);

    String[] args = {};

    // When
    var result = command.execute(args);

    // Then
    assertThat(result).contains("Current FX Exchange Rates");
    assertThat(result).contains("EUR/USD");
    verify(rateService).getCurrentRates();
  }

  @Test
  void shouldHandleEmptyRates() throws Exception {
    // Given
    when(rateService.getCurrentRates()).thenReturn(List.of());

    String[] args = {};

    // When
    var result = command.execute(args);

    // Then
    assertThat(result).contains("No FX rates available at this time");
  }

  @Test
  void shouldGetCommandMetadata() {
    assertThat(command.getCommandName()).isEqualTo("rates");
    assertThat(command.getUsage()).contains("rates");
    assertThat(command.getDescription()).contains("Displays current FX exchange rates");
  }
}
