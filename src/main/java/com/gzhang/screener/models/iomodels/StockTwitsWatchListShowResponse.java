package com.gzhang.screener.models.iomodels;

import com.gzhang.screener.models.WatchedTicker;
import com.gzhang.screener.models.metamodels.StockTwitsWatchListResponse;
import com.gzhang.screener.models.metamodels.StockTwitsWatchedTicker;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.awt.event.WindowAdapter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockTwitsWatchListShowResponse {
    Object response;
    StockTwitsWatchListResponse watchlist;

    public List<WatchedTicker> standardizeWatchList() {
        if(watchlist.getSymbols() == null) return new ArrayList<>();
        List<WatchedTicker> watchedTickers = new ArrayList<>();
        for(StockTwitsWatchedTicker stockTwitsWatchedTicker : watchlist.getSymbols()) {
            watchedTickers.add(stockTwitsWatchedTicker.toWatchedTicker());
        }
        return watchedTickers;
    }
}
