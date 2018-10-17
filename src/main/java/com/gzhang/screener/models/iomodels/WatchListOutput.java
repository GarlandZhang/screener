package com.gzhang.screener.models.iomodels;

import com.gzhang.screener.models.WatchList;
import com.gzhang.screener.models.WatchedTicker;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class WatchListOutput {
    List<WatchedTickerOutput> tickers;

    public WatchListOutput() {
        tickers = new ArrayList<>();
    }

    public WatchListOutput(WatchList watchList) {
        this();
        if(watchList != null && watchList.getWatchedTickers() != null) {
            for (WatchedTicker watchedTicker : watchList.getWatchedTickers()) {
                tickers.add(new WatchedTickerOutput(watchedTicker));
            }
        }
    }
}
