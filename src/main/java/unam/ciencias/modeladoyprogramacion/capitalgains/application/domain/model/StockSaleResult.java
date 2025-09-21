package unam.ciencias.modeladoyprogramacion.capitalgains.application.domain.model;

import java.math.BigDecimal;
import lombok.*;

@Getter
@EqualsAndHashCode
@ToString
@Builder
public class StockSaleResult {
  private BigDecimal totalAmountSold;
  private BigDecimal profit;
  private BigDecimal tax;
}
