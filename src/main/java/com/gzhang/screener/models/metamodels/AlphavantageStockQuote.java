package com.gzhang.screener.models.metamodels;

import com.gzhang.screener.models.DailyStockData;
import com.gzhang.screener.models.StockMetadata;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class AlphavantageStockQuote {

    String symbol;
    float openPrice;
    float highPrice;
    float lowPrice;
    float currentPrice;
    long volume;
    Date latestTradingDay;
    float previousClose;
    float change;
    float changePercent;
}
