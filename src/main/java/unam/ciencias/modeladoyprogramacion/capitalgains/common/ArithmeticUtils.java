package unam.ciencias.modeladoyprogramacion.capitalgains.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class ArithmeticUtils {

  /**
   * Calculates the weighted average of a list of values using corresponding weights. Let N =
   * values.length-1 Let v_i in values for i in [0, 1, ..., N] Let w_i in values for i in [0, 1,
   * ..., N] Then weighted_mean = (v_0*w_0 + v_1*w_1 + ... + v_N*w_N) / (w_0 + w_1 + ... + w_N)
   *
   * @param values A list of BigDecimal values.
   * @param weights A list of BigDecimal weights corresponding to the values.
   * @return The calculated weighted average.
   * @throws IllegalArgumentException If the sizes of the 'values' and 'weights' lists are
   *     different, or if either list is empty.
   * @throws ArithmeticException If the total weight is zero.
   */
  public static BigDecimal calculateWeightedMean(
      List<BigDecimal> values, List<BigDecimal> weights) {
    if (values.size() != weights.size() || values.isEmpty()) {
      throw new IllegalArgumentException(
          "Lists of values and weights must have the same size and must not be empty.");
    }

    BigDecimal weightedSum = BigDecimal.ZERO;
    BigDecimal totalWeight = BigDecimal.ZERO;

    for (int i = 0; i < values.size(); i++) {
      BigDecimal value = values.get(i);
      BigDecimal weight = weights.get(i);

      weightedSum = weightedSum.add(value.multiply(weight));
      totalWeight = totalWeight.add(weight);
    }

    if (totalWeight.compareTo(BigDecimal.ZERO) == 0) {
      throw new ArithmeticException("Total weight cannot be zero.");
    }

    return weightedSum.divide(totalWeight, 2, RoundingMode.HALF_UP);
  }
}
