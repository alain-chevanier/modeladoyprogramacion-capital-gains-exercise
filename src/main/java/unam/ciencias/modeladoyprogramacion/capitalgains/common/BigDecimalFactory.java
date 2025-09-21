package unam.ciencias.modeladoyprogramacion.capitalgains.common;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalFactory {
  public static BigDecimal createMoneyAmount(String amount) {
    return new BigDecimal(amount).setScale(2, RoundingMode.HALF_UP);
  }

  public static BigDecimal createMoneyAmount(long amount) {
    return new BigDecimal(amount).setScale(2, RoundingMode.HALF_UP);
  }

  public static BigDecimal createMoneyAmount(BigDecimal amount) {
    return amount.setScale(2, RoundingMode.HALF_UP);
  }
}
