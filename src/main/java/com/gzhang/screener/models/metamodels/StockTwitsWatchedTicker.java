package com.gzhang.screener.models.metamodels;

import com.gzhang.screener.models.WatchedTicker;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockTwitsWatchedTicker {
    int id;
    String symbol;
    String title;

    public WatchedTicker toWatchedTicker() {
        return new WatchedTicker(symbol, title);
    }
}
