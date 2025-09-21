package unam.ciencias.modeladoyprogramacion.capitalgains.adapter.in.commandlinerunner.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class StockOperation {
  @JsonDeserialize(using = StockOperationTypeDeserializer.class)
  @JsonProperty("operation")
  private StockOperationType type;

  @JsonDeserialize(using = BigDecimalDeserializer.class)
  @JsonSerialize(using = BigDecimalSerializer.class)
  @JsonProperty("unit-cost")
  private BigDecimal unitCost;

  @JsonProperty("quantity")
  private Integer units;
}
