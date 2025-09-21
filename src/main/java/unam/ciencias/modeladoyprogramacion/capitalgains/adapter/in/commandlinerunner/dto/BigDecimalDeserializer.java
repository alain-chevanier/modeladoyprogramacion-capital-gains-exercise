package unam.ciencias.modeladoyprogramacion.capitalgains.adapter.in.commandlinerunner.dto;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalDeserializer extends JsonDeserializer<BigDecimal> {
  @Override
  public BigDecimal deserialize(
      JsonParser jsonParser, DeserializationContext deserializationContext)
      throws IOException, JacksonException {
    String number = jsonParser.getText();
    return new BigDecimal(number).setScale(2, RoundingMode.HALF_UP);
  }
}
