package unam.ciencias.modeladoyprogramacion.capitalgains.application.domain.model;

import static unam.ciencias.modeladoyprogramacion.capitalgains.common.ArithmeticUtils.calculateWeightedMean;
import static unam.ciencias.modeladoyprogramacion.capitalgains.common.BigDecimalFactory.createMoneyAmount;

import java.math.BigDecimal;
import java.util.List;

public class CustomerStocksAccount {
  private int totalUnits;
  private BigDecimal weightedAverageCostPerUnit;
  private BigDecimal losses;
  private final CustomerStocksAccountConfig config;

  private CustomerStocksAccount(CustomerStocksAccountConfig config) {
    this.config = config;
    this.totalUnits = 0;
    this.weightedAverageCostPerUnit = createMoneyAmount(BigDecimal.ZERO);
    this.losses = createMoneyAmount(BigDecimal.ZERO);
  }

  public static CustomerStocksAccount create(CustomerStocksAccountConfig config) {
    return new CustomerStocksAccount(config);
  }

  public static CustomerStocksAccount createWithInitialState(
      CustomerStocksAccountConfig config, CustomerStocksAccountState state) {
    var account = new CustomerStocksAccount(config);
    account.totalUnits = state.getTotalUnits();
    account.weightedAverageCostPerUnit = state.getWeightedAverageCostPerUnit();
    account.losses = state.getLosses();
    return account;
  }

  public CustomerStocksAccountState buyStocks(int units, BigDecimal unitCost) {
    this.weightedAverageCostPerUnit = calculateNewWeightedAverageCostPerUnit(units, unitCost);
    this.totalUnits += units;
    return getCurrentState();
  }

  private BigDecimal calculateNewWeightedAverageCostPerUnit(int units, BigDecimal unitCost) {
    // weight is the units each price adds
    // value is the price
    // we calculated the weighted mean for what we just described
    var currentTotalUnits = BigDecimal.valueOf(this.totalUnits);
    var boughtUnits = BigDecimal.valueOf(units);
    return calculateWeightedMean(
        List.of(this.weightedAverageCostPerUnit, unitCost),
        List.of(currentTotalUnits, boughtUnits));
  }

  public StockSaleResult sellStocks(int units, BigDecimal unitCost) {
    this.totalUnits -= units;
    var soldUnits = BigDecimal.valueOf(units);
    var totalAmountSold = soldUnits.multiply(createMoneyAmount(unitCost));
    var bruteProfit = calculateProfit(soldUnits, totalAmountSold);
    StockSaleResult result = reportStockSaleResult(totalAmountSold, bruteProfit);
    adjustLosses(totalAmountSold, bruteProfit);
    return result;
  }

  private BigDecimal calculateProfit(BigDecimal soldUnits, BigDecimal totalAmountSold) {
    // profit = totalAmountSold - totalAcquisitionAmount
    //   where totalAcquisitionAmount = sold_units * weighted_average_cost
    //      and totalAmountSold = sold_units * sale_cost
    var unitsAcquisitionPrice = soldUnits.multiply(this.weightedAverageCostPerUnit);
    return totalAmountSold.subtract(unitsAcquisitionPrice);
  }

  private StockSaleResult reportStockSaleResult(
      BigDecimal totalAmountSold, BigDecimal bruteProfit) {
    if (exceedsNoTaxUpperLimit(totalAmountSold)) {
      return createStockSaleResultWithTax(totalAmountSold, bruteProfit);
    } else {
      return createStockSaleResult(
          totalAmountSold, bruteProfit, createMoneyAmount(BigDecimal.ZERO));
    }
  }

  private StockSaleResult createStockSaleResultWithTax(
      BigDecimal totalAmountSold, BigDecimal bruteProfit) {
    var netProfit = deductLossesFromProfit(bruteProfit);
    BigDecimal tax = calculateSaleTaxes(netProfit);
    return createStockSaleResult(totalAmountSold, netProfit, tax);
  }

  private BigDecimal deductLossesFromProfit(BigDecimal profit) {
    return profit.add(this.losses);
  }

  private void adjustLosses(BigDecimal totalAmountSold, BigDecimal bruteProfit) {
    if (exceedsNoTaxUpperLimit(totalAmountSold) || isNegative(bruteProfit)) {
      // This function is tricky I'll explain
      // if bruteProfit < 0 then we accumulate losses
      // if totalAmount > noTaxUpperLimit we deducted losses from profit before calculating tax, so
      //   we subtract the profit  from the current losses for the next round
      this.losses = this.losses.add(bruteProfit);
      this.losses =
          this.losses.compareTo(BigDecimal.ZERO) > 0
              ? createMoneyAmount(BigDecimal.ZERO)
              : this.losses;
    }
  }

  private BigDecimal calculateSaleTaxes(BigDecimal netProfit) {
    if (isPositive(netProfit)) {
      return createMoneyAmount(netProfit.multiply(this.config.getTaxPercentage()));
    }
    return createMoneyAmount(BigDecimal.ZERO);
  }

  private StockSaleResult createStockSaleResult(
      BigDecimal totalAmountSold, BigDecimal profit, BigDecimal tax) {
    return StockSaleResult.builder()
        .totalAmountSold(totalAmountSold)
        .profit(profit)
        .tax(tax)
        .build();
  }

  private boolean exceedsNoTaxUpperLimit(BigDecimal amount) {
    return amount.compareTo(this.config.getNoTaxLimitUpperLimit()) > 0;
  }

  private boolean isNegative(BigDecimal amount) {
    return amount.compareTo(BigDecimal.ZERO) < 0;
  }

  private boolean isPositive(BigDecimal amount) {
    return amount.compareTo(BigDecimal.ZERO) > 0;
  }

  public CustomerStocksAccountState getCurrentState() {
    return CustomerStocksAccountState.builder()
        .losses(this.losses)
        .weightedAverageCostPerUnit(this.weightedAverageCostPerUnit)
        .totalUnits(this.totalUnits)
        .build();
  }
}
