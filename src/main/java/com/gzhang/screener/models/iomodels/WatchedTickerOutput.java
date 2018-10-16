package com.gzhang.screener.models.iomodels;

import com.gzhang.screener.models.WatchedTicker;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WatchedTickerOutput {
    String ticker;

    public WatchedTickerOutput(String ticker) {
        this.ticker = ticker;
    }

    public WatchedTickerOutput(WatchedTicker watchedTicker) {
        ticker = watchedTicker.getTicker();
    }
}
