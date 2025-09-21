package unam.ciencias.modeladoyprogramacion.capitalgains.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppConfigProperties {
  private String noTaxUpperLimit;
  private String taxPercentage;
}
