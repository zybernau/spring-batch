package com.spratch.spratch;

import org.springframework.batch.item.ItemProcessor;

public class StockProcessor implements ItemProcessor<Stocks, Stocks> {
  @Override
  public Stocks process(Stocks stok) throws Exception {
    return stok;
  }
}
