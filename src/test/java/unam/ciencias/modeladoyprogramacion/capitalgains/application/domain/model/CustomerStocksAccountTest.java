package unam.ciencias.modeladoyprogramacion.capitalgains.application.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static unam.ciencias.modeladoyprogramacion.capitalgains.common.BigDecimalFactory.createMoneyAmount;

class CustomerStocksAccountTest {

  static final BigDecimal NO_TAX_UPPER_LIMIT = new BigDecimal("20000.00");
  static final BigDecimal TAX_PERCENTAGE = new BigDecimal("0.20");
  static final CustomerStocksAccountConfig config = CustomerStocksAccountConfig.builder()
    .taxPercentage(TAX_PERCENTAGE)
    .noTaxLimitUpperLimit(NO_TAX_UPPER_LIMIT)
    .build();

  CustomerStocksAccount customerAccount;

  @Test
  void testBuy_SingleStock() {
    customerAccount = CustomerStocksAccount.create(config);

    customerAccount.buyStocks(10000, createMoneyAmount(10));

    var expectedState = CustomerStocksAccountState.builder()
      .totalUnits(10000)
      .weightedAverageCostPerUnit(createMoneyAmount(10))
      .losses(createMoneyAmount(0))
      .build();
    assertThat(customerAccount.getCurrentState()).isEqualTo(expectedState);
  }

  @Test
  void testBuy_MultipleStocks() {
    customerAccount = CustomerStocksAccount.create(config);

    customerAccount.buyStocks(10000, createMoneyAmount(10));
    customerAccount.buyStocks(5000, createMoneyAmount(25));
    customerAccount.buyStocks(2000, createMoneyAmount(12));

    var expectedState = CustomerStocksAccountState.builder()
      .totalUnits(17000)
      .weightedAverageCostPerUnit(createMoneyAmount("14.65"))
      .losses(createMoneyAmount(0))
      .build();
    assertThat(customerAccount.getCurrentState()).isEqualTo(expectedState);
  }

  @Test
  void testSell_NoPrevLosses_HasProfit_ExceedingNoTaxUpperLimit() {
    var initialState = CustomerStocksAccountState.builder()
      .totalUnits(10000)
      .weightedAverageCostPerUnit(createMoneyAmount(10))
      .losses(createMoneyAmount(0)) // No losses for this case
      .build();
    customerAccount = CustomerStocksAccount.createWithInitialState(config, initialState);

    var saleResult = customerAccount.sellStocks(7000, createMoneyAmount(13));

    // total amount sold is 7k * 13 -> $91k
    // no losses to deduct, profit is $91k - $70k -> $21k
    // tax is $21k * .2 -> $4.2k
    var expectedSaleResult = StockSaleResult.builder()
      .totalAmountSold(createMoneyAmount(91000))
      .profit(createMoneyAmount(21000))
      .tax(createMoneyAmount(4200))
      .build();
    assertThat(saleResult).isEqualTo(expectedSaleResult);

    // we had 10k units, and sold 7k, then we have 10k - 7k = 3k remaining units
    // avg unit cost and losses are not affected
    var expectedState = CustomerStocksAccountState.builder()
      .totalUnits(3000)
      .weightedAverageCostPerUnit(createMoneyAmount(10))
      .losses(createMoneyAmount(0))
      .build();
    assertThat(customerAccount.getCurrentState()).isEqualTo(expectedState);
  }

  @Test
  void testSell_WithPrevLosses_HasProfit_ExceedingNoTaxUpperLimit() {
    var initialState = CustomerStocksAccountState.builder()
      .totalUnits(10000)
      .weightedAverageCostPerUnit(createMoneyAmount(10))
      .losses(createMoneyAmount(-10000))
      .build();
    customerAccount = CustomerStocksAccount.createWithInitialState(config, initialState);

    var saleResult = customerAccount.sellStocks(7000, createMoneyAmount(13));

    // total amount sold is 7k * 13 -> $91k
    // brute profit is $91k - $70k -> $21k
    // currently 10k losses to be deducted, net profit is $21k - 10k -> $11k
    // tax is $11k * .2 -> $2.2k
    var expectedSaleResult = StockSaleResult.builder()
      .totalAmountSold(createMoneyAmount(91000))
      .profit(createMoneyAmount(11000))
      .tax(createMoneyAmount(2200))
      .build();
    assertThat(saleResult).isEqualTo(expectedSaleResult);

    // we had 10k units, and sold 7k, then we have 10k - 7k = 3k remaining units
    // avg unit cost is not affected
    // we already deduct all of our losses
    var expectedState = CustomerStocksAccountState.builder()
      .totalUnits(3000)
      .weightedAverageCostPerUnit(createMoneyAmount(10))
      .losses(createMoneyAmount(0))
      .build();
    assertThat(customerAccount.getCurrentState()).isEqualTo(expectedState);
  }

  @Test
  void testSell_WithPrevLossesBiggerThanProfit_HasProfit_ExceedingNoTaxUpperLimit() {
    var initialState = CustomerStocksAccountState.builder()
      .totalUnits(10000)
      .weightedAverageCostPerUnit(createMoneyAmount(10))
      .losses(createMoneyAmount(-30000))
      .build();
    customerAccount = CustomerStocksAccount.createWithInitialState(config, initialState);

    var saleResult = customerAccount.sellStocks(7000, createMoneyAmount(13));

    // total amount sold is 7k * 13 -> $91k
    // brute profit is $91k - $70k -> $21k
    // currently 30k losses to be deducted, net profit is $21k - 30k -> -$9k
    // tax is $0k * .2 -> $0k
    var expectedSaleResult = StockSaleResult.builder()
      .totalAmountSold(createMoneyAmount(91000))
      .profit(createMoneyAmount(-9000))
      .tax(createMoneyAmount(0))
      .build();
    assertThat(saleResult).isEqualTo(expectedSaleResult);

    // we had 10k units, and sold 7k, then we have 10k - 7k = 3k remaining units
    // avg unit cost is not affected
    // we had a profit of $21k, that is we most have $30k - $21k -> 9k remaining losses
    var expectedState = CustomerStocksAccountState.builder()
      .totalUnits(3000)
      .weightedAverageCostPerUnit(createMoneyAmount(10))
      .losses(createMoneyAmount(-9000))
      .build();
    assertThat(customerAccount.getCurrentState()).isEqualTo(expectedState);
  }

  @Test
  void testSell_NoPrevLosses_HasProfit_TotalAmountSoldEqualsToNoTaxUpperLimit() {
    var initialState = CustomerStocksAccountState.builder()
      .totalUnits(10000)
      .weightedAverageCostPerUnit(createMoneyAmount(10))
      .losses(createMoneyAmount(0)) // No prev losses for this case
      .build();
    customerAccount = CustomerStocksAccount.createWithInitialState(config, initialState);

    var saleResult = customerAccount.sellStocks(1000, createMoneyAmount(20));

    // total amount sold is 1k * 20 -> $20k, exactly the no tax upper limit
    // profit is $20k - $10k -> $10k
    // tax is $0, as we don't exceed the no tax upper limit
    var expectedSaleResult = StockSaleResult.builder()
      .totalAmountSold(createMoneyAmount(20000))
      .profit(createMoneyAmount(10000))
      .tax(createMoneyAmount(0))
      .build();
    assertThat(saleResult).isEqualTo(expectedSaleResult);

    // we had 10k units, and sold 1k, then we have 10k - 1k = 9k remaining units
    // avg unit cost and losses are not affected
    var expectedState = CustomerStocksAccountState.builder()
      .totalUnits(9000)
      .weightedAverageCostPerUnit(createMoneyAmount(10))
      .losses(createMoneyAmount(0))
      .build();
    assertThat(customerAccount.getCurrentState()).isEqualTo(expectedState);
  }

  @Test
  void testSell_NoPrevLosses_WithProfit_TotalAmountSoldIsLessThanNoTaxUpperLimit() {
    var initialState = CustomerStocksAccountState.builder()
      .totalUnits(10000)
      .weightedAverageCostPerUnit(createMoneyAmount(10))
      .losses(createMoneyAmount(0)) // No prev losses for this case
      .build();
    customerAccount = CustomerStocksAccount.createWithInitialState(config, initialState);

    var saleResult = customerAccount.sellStocks(1000, createMoneyAmount(15));

    // total amount sold is 1k * 15 -> $15k, bellow the no tax upper limit
    // profit is $15k - $10k -> $5k
    // tax is $0, as we don't exceed the no tax upper limit
    var expectedSaleResult = StockSaleResult.builder()
      .totalAmountSold(createMoneyAmount(15000))
      .profit(createMoneyAmount(5000))
      .tax(createMoneyAmount(0))
      .build();
    assertThat(saleResult).isEqualTo(expectedSaleResult);

    // we had 10k units, and sold 1k, then we have 10k - 1k = 9k remaining units
    // avg unit cost and losses are not affected
    var expectedState = CustomerStocksAccountState.builder()
      .totalUnits(9000)
      .weightedAverageCostPerUnit(createMoneyAmount(10))
      .losses(createMoneyAmount(0))
      .build();
    assertThat(customerAccount.getCurrentState()).isEqualTo(expectedState);
  }

  @Test
  void testSell_WithPrevLosses_WithProfit_TotalAmountSoldIsLessOrEqualThanNoTaxUpperLimit() {
    var initialState = CustomerStocksAccountState.builder()
      .totalUnits(10000)
      .weightedAverageCostPerUnit(createMoneyAmount(10))
      .losses(createMoneyAmount(-2000)) // No prev losses for this case
      .build();
    customerAccount = CustomerStocksAccount.createWithInitialState(config, initialState);

    var saleResult = customerAccount.sellStocks(1000, createMoneyAmount(15));

    // total amount sold is 1k * 15 -> $15k, bellow the no tax upper limit
    // current losses are $2k, profit is $15k - $10k -> $5k,
    //     we don't deduct losses as we don't exceed the no tax upper limit
    // tax is $0, as we don't exceed the no tax upper limit
    var expectedSaleResult = StockSaleResult.builder()
      .totalAmountSold(createMoneyAmount(15000))
      .profit(createMoneyAmount(5000))
      .tax(createMoneyAmount(0))
      .build();
    assertThat(saleResult).isEqualTo(expectedSaleResult);

    // we had 10k units, and sold 1k, then we have 10k - 1k = 9k remaining units
    // avg unit cost does not change on sales
    // losses are not affected even if profit is positive when if we don't need to pay taxes
    var expectedState = CustomerStocksAccountState.builder()
      .totalUnits(9000)
      .weightedAverageCostPerUnit(createMoneyAmount(10))
      .losses(createMoneyAmount(-2000))
      .build();
    assertThat(customerAccount.getCurrentState()).isEqualTo(expectedState);
  }

  @Test
  void testSell_WithPrevLosses_WithNegativeProfit_TotalSoldAmountIsLessThanNoTaxUpperLimit() {
    var initialState = CustomerStocksAccountState.builder()
      .totalUnits(10000)
      .weightedAverageCostPerUnit(createMoneyAmount(10))
      .losses(createMoneyAmount(-2000))
      .build();
    customerAccount = CustomerStocksAccount.createWithInitialState(config, initialState);

    var saleResult = customerAccount.sellStocks(5000, createMoneyAmount(2));

    // total amount sold is 5k * 2 -> 10k, exactly the no tax upper limit
    // profit is $10k - $50k -> -$40k
    // tax is $0, as profit is negative
    var expectedResult = StockSaleResult.builder()
      .totalAmountSold(createMoneyAmount(10000))
      .profit(createMoneyAmount(-40000))
      .tax(createMoneyAmount(BigDecimal.ZERO))
      .build();
    assertThat(saleResult).isEqualTo(expectedResult);

    // we had 10k units, and sold 5k, then we have 10k - 5k = 5k remaining units
    // avg unit cost is not affected
    // original losses were $2k, and we accumulated new losses of $40k,
    //     so total losses are $40k + $2k -> $42k
    var expectedState = CustomerStocksAccountState.builder()
      .totalUnits(5000)
      .weightedAverageCostPerUnit(createMoneyAmount(10))
      .losses(createMoneyAmount(-42000))
      .build();
    assertThat(customerAccount.getCurrentState()).isEqualTo(expectedState);
  }
}
