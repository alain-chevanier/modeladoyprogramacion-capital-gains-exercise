package unam.ciencias.modeladoyprogramacion.capitalgains.application.domain.model;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
@Builder
public class CustomerStocksAccountConfig {
  private final BigDecimal noTaxLimitUpperLimit;
  private final BigDecimal taxPercentage;
}
