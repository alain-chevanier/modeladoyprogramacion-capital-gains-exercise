package unam.ciencias.modeladoyprogramacion.capitalgains.application.domain.service;

import static unam.ciencias.modeladoyprogramacion.capitalgains.common.BigDecimalFactory.createMoneyAmount;

import unam.ciencias.modeladoyprogramacion.capitalgains.adapter.in.commandlinerunner.dto.StockOperation;
import unam.ciencias.modeladoyprogramacion.capitalgains.adapter.in.commandlinerunner.dto.StockOperationTax;
import unam.ciencias.modeladoyprogramacion.capitalgains.adapter.in.commandlinerunner.dto.StockOperationType;
import unam.ciencias.modeladoyprogramacion.capitalgains.application.domain.model.CustomerStocksAccount;
import unam.ciencias.modeladoyprogramacion.capitalgains.application.domain.model.CustomerStocksAccountConfig;
import unam.ciencias.modeladoyprogramacion.capitalgains.application.port.in.CalculateStockOperationsTaxesUseCase;
import unam.ciencias.modeladoyprogramacion.capitalgains.config.AppConfigProperties;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CalculateStockOperationsTaxesService implements CalculateStockOperationsTaxesUseCase {

  private final AppConfigProperties appConfigProperties;

  @Override
  public List<StockOperationTax> calculateTaxes(List<StockOperation> stockOperations) {
    var config =
        CustomerStocksAccountConfig.builder()
            .noTaxLimitUpperLimit(createMoneyAmount(appConfigProperties.getNoTaxUpperLimit()))
            .taxPercentage(createMoneyAmount(appConfigProperties.getTaxPercentage()))
            .build();
    CustomerStocksAccount account = CustomerStocksAccount.create(config);
    return stockOperations.stream()
        .map(operation -> addOperationToHistory(operation, account))
        .toList();
  }

  private StockOperationTax addOperationToHistory(
      StockOperation operation, CustomerStocksAccount account) {
    if (operation.getType().equals(StockOperationType.BUY)) {
      account.buyStocks(operation.getUnits(), operation.getUnitCost());
      return StockOperationTax.builder().tax(createMoneyAmount(BigDecimal.ZERO)).build();
    } else {
      var result = account.sellStocks(operation.getUnits(), operation.getUnitCost());
      return StockOperationTax.builder().tax(result.getTax()).build();
    }
  }
}
