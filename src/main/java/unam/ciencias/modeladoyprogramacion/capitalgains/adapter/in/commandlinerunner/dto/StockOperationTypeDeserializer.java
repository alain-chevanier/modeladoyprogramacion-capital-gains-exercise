package unam.ciencias.modeladoyprogramacion.capitalgains.adapter.in.commandlinerunner.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;

public class StockOperationTypeDeserializer extends JsonDeserializer<StockOperationType> {
  @Override
  public StockOperationType deserialize(
      JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
    String value = jsonParser.getText();
    return StockOperationType.valueOf(value.toUpperCase());
  }
}
