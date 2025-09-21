package unam.ciencias.modeladoyprogramacion.capitalgains.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import static unam.ciencias.modeladoyprogramacion.capitalgains.common.ArithmeticUtils.calculateWeightedMean;

import static org.junit.jupiter.api.Assertions.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ArithmeticUtilsTest {

  @Test
  void testCalculateWeightedAverage() {
    List<BigDecimal> values = List.of(
      new BigDecimal("10.5"),
      new BigDecimal("8.0"),
      new BigDecimal("6.3")
    );
    List<BigDecimal> weights = List.of(
      new BigDecimal("0.2"),
      new BigDecimal("0.3"),
      new BigDecimal("0.5")
    );

    BigDecimal actualAverage = calculateWeightedMean(values, weights);

    BigDecimal expectedAverage = new BigDecimal("7.65").setScale(2, RoundingMode.HALF_UP);
    assertThat(actualAverage).isEqualTo(expectedAverage);
  }

  @Test
  void testCalculateWeightedAverageWithEmptyLists() {
    List<BigDecimal> values = List.of();
    List<BigDecimal> weights = List.of();

    assertThrows(IllegalArgumentException.class,
      () -> calculateWeightedMean(values, weights));
  }

  @Test
  void testCalculateWeightedAverageWithMismatchedSizes() {
    List<BigDecimal> values = Arrays.asList(new BigDecimal("10.5"));
    List<BigDecimal> weights = Arrays.asList(
      new BigDecimal("0.2"),
      new BigDecimal("0.3"),
      new BigDecimal("0.5")
    );

    assertThrows(IllegalArgumentException.class,
      () -> calculateWeightedMean(values, weights));
  }

  @Test
  void testCalculateWeightedAverageWithZeroTotalWeight() {
    List<BigDecimal> values = Arrays.asList(
      new BigDecimal("10.5"),
      new BigDecimal("8.0")
    );

    List<BigDecimal> weights = Arrays.asList(
      new BigDecimal("0"),
      new BigDecimal("0")
    );

    assertThrows(ArithmeticException.class,
      () -> calculateWeightedMean(values, weights));
  }
}
