package unam.ciencias.modeladoyprogramacion.capitalgains.adapter.in.commandlinerunner.dto;

public enum StockOperationType {
  BUY("BUY"),
  SELL("SELL");
  final String value;

  StockOperationType(String value) {
    this.value = value;
  }
}
