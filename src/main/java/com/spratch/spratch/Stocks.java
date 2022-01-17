package com.spratch.spratch;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Stocks {

  private String stockName;
  private Double ftHigh;
  private Double ftLow;
  private Double buyPrice;
  private Double sellPrice;
  private Double margin;
  private Double profitPercent;

  public Stocks(String stockName, Double ftHigh, Double ftLow, Double buyPrice, Double sellPrice, Double margin, Double profitPercent) {
    this.stockName = stockName;
    this.ftHigh = ftHigh;
    this.ftLow = ftLow;
    this.buyPrice = buyPrice;
    this.sellPrice = sellPrice;
    this.margin = margin;
    this.profitPercent = profitPercent;
  }
  
  @Override
  public String toString() {
    return stockName;
  }




}
