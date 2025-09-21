package unam.ciencias.modeladoyprogramacion.capitalgains.application.domain.service;

import unam.ciencias.modeladoyprogramacion.capitalgains.adapter.in.commandlinerunner.dto.StockOperation;
import unam.ciencias.modeladoyprogramacion.capitalgains.adapter.in.commandlinerunner.dto.StockOperationTax;
import unam.ciencias.modeladoyprogramacion.capitalgains.adapter.in.commandlinerunner.dto.StockOperationType;
import unam.ciencias.modeladoyprogramacion.capitalgains.application.domain.model.CustomerStocksAccount;
import unam.ciencias.modeladoyprogramacion.capitalgains.application.domain.model.CustomerStocksAccountState;
import unam.ciencias.modeladoyprogramacion.capitalgains.application.domain.model.StockSaleResult;
import unam.ciencias.modeladoyprogramacion.capitalgains.application.port.in.CalculateStockOperationsTaxesUseCase;
import unam.ciencias.modeladoyprogramacion.capitalgains.config.AppConfigProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static unam.ciencias.modeladoyprogramacion.capitalgains.common.BigDecimalFactory.createMoneyAmount;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalculateStockOperationsTaxesServiceTest {

  static final AppConfigProperties appConfigProperties;
  static {
    appConfigProperties = new AppConfigProperties();
    appConfigProperties.setTaxPercentage("0.20");
    appConfigProperties.setNoTaxUpperLimit("20000.00");
  }

  CalculateStockOperationsTaxesUseCase service;

  CustomerStocksAccount mockAccount;

  MockedStatic<CustomerStocksAccount> staticMock;

  @BeforeEach
  void setUp() {
    mockAccount = Mockito.mock(CustomerStocksAccount.class);
    staticMock  = mockStatic(CustomerStocksAccount.class);
    when(CustomerStocksAccount.create(any())).thenReturn(mockAccount);
  }

  @AfterEach
  void tearDown() {
    staticMock.close();
  }

  @Test
  void calculateTaxes() {
    service = new CalculateStockOperationsTaxesService(appConfigProperties);

    var buy = StockOperation.builder()
      .type(StockOperationType.BUY)
      .units(10000)
      .unitCost(createMoneyAmount(10))
      .build();
    when(mockAccount.buyStocks(10000, createMoneyAmount(10)))
      .thenReturn(CustomerStocksAccountState.builder().build());

    var sale = StockOperation.builder()
      .type(StockOperationType.SELL)
      .units(5000)
      .unitCost(createMoneyAmount(12))
      .build();
    when(mockAccount.sellStocks(5000, createMoneyAmount(12)))
      .thenReturn(StockSaleResult.builder().tax(createMoneyAmount(2000)).build());

    var taxes = service.calculateTaxes(List.of(buy, sale));

    var taxBuy = StockOperationTax.builder().tax(createMoneyAmount(0)).build();
    var sellBuy = StockOperationTax.builder().tax(createMoneyAmount(2000)).build();
    var expectedTaxes = List.of(taxBuy, sellBuy);
    assertThat(taxes).isEqualTo(expectedTaxes);
  }
}
