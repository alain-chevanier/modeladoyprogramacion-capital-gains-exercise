package unam.ciencias.modeladoyprogramacion.capitalgains.adapter.in.commandlinerunner;

import unam.ciencias.modeladoyprogramacion.capitalgains.adapter.in.commandlinerunner.dto.StockOperation;
import unam.ciencias.modeladoyprogramacion.capitalgains.adapter.in.commandlinerunner.dto.StockOperationTax;
import unam.ciencias.modeladoyprogramacion.capitalgains.application.port.in.CalculateStockOperationsTaxesUseCase;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CommandLineRunnerImpl implements CommandLineRunner {
  private final ObjectMapper objectMapper;
  private final CalculateStockOperationsTaxesUseCase calculateStockOperationsTaxesUseCase;
  private final Scanner scanner;
  private final PrintStream printStream;

  @Override
  public void run(String... args) throws Exception {
    String line;
    do {
      line = this.scanner.nextLine();
      if (!line.isEmpty()) {
        List<StockOperation> stockOperations =
            this.objectMapper.readValue(line, new TypeReference<>() {});
        List<StockOperationTax> taxes =
            calculateStockOperationsTaxesUseCase.calculateTaxes(stockOperations);
        this.printStream.println(objectMapper.writeValueAsString(taxes));
      }
    } while (!line.isEmpty());
  }
}
