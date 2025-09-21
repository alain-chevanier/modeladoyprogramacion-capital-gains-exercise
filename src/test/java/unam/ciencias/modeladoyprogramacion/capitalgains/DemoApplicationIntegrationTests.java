package unam.ciencias.modeladoyprogramacion.capitalgains;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.*;
import java.util.Scanner;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest()
class DemoApplicationIntegrationTests {
  /*
   * Note check the clase below this StdInAndStdOutTestConfig to check bean overrides
   */
  public static ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
  public static PrintStream printStream = new PrintStream(outputStreamCaptor);

  public static final String commandInput = """
    [{"operation":"buy", "unit-cost":10.00, "quantity": 100}, {"operation":"sell", "unit-cost":15.00, "quantity": 50}, {"operation":"sell", "unit-cost":15.00, "quantity": 50}]
    [{"operation":"buy", "unit-cost":10.00, "quantity": 10000}, {"operation":"sell", "unit-cost":20.00, "quantity": 5000}, {"operation":"sell", "unit-cost":5.00, "quantity": 5000}]

    """;

  public static final String commandExpectedOutput = """
    [{"tax":0.00},{"tax":0.00},{"tax":0.00}]
    [{"tax":0.00},{"tax":10000.00},{"tax":0.00}]
    """;

  @Test
  void runTestCase() {
    assertThat(outputStreamCaptor).hasToString(commandExpectedOutput);
  }
}

// This is my best try to override scanner bean and printStream bean with something I can handle
@Configuration
class StdInAndStdOutTestConfig {
  @Primary
  @Bean
  public Scanner stdInputStreamWrapper() {
    ByteArrayInputStream inputStream = new ByteArrayInputStream(
      DemoApplicationIntegrationTests.commandInput.getBytes()
    );
    return new Scanner(inputStream);
  }

  @Primary
  @Bean
  public PrintStream stdPrintStreamWrapper() {
    return DemoApplicationIntegrationTests.printStream;
  }
}
