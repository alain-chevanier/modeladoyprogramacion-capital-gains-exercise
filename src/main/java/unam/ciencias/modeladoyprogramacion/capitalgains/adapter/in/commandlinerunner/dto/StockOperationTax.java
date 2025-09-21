package unam.ciencias.modeladoyprogramacion.capitalgains.adapter.in.commandlinerunner.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class StockOperationTax {
  @JsonSerialize(using = BigDecimalSerializer.class)
  private BigDecimal tax;
}
