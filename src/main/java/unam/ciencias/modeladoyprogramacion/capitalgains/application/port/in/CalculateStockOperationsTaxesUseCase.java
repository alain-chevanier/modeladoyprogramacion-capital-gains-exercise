package unam.ciencias.modeladoyprogramacion.capitalgains.application.port.in;

import unam.ciencias.modeladoyprogramacion.capitalgains.adapter.in.commandlinerunner.dto.StockOperation;
import unam.ciencias.modeladoyprogramacion.capitalgains.adapter.in.commandlinerunner.dto.StockOperationTax;
import java.util.List;

public interface CalculateStockOperationsTaxesUseCase {
  List<StockOperationTax> calculateTaxes(List<StockOperation> stockOperations);
}
