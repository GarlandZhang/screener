package com.gzhang.screener.models.metamodels;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GlobalQuote {
    String symbol;
    float openPrice;
    float highPrice;
    float lowPrice;
    float currentPrice;
    long volume;
    Date latestTradingDay; // could use this for something else
    float previousClose;
    float change;
    String changePercent;
}
