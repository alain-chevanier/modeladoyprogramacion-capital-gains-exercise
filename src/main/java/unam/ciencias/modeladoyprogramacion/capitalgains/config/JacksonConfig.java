package unam.ciencias.modeladoyprogramacion.capitalgains.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {
  @Bean
  public ObjectMapper initObjectMapper() {
    return new ObjectMapper();
  }
}
