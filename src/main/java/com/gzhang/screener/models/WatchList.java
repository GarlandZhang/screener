package com.gzhang.screener.models;

import com.gzhang.screener.models.metamodels.StockTwitsWatchedTicker;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class WatchList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    int userId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    AppUser appUser;

    @OneToMany(fetch = FetchType.EAGER,
            mappedBy = "watchList",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    List<WatchedTicker> watchedTickers;

    public void addTickers(List<WatchedTicker> tickers) {
        if(watchedTickers == null) watchedTickers = new ArrayList<>();
        for(WatchedTicker ticker : tickers) {
            if(!tickerExists(ticker.getTicker(), watchedTickers)) watchedTickers.add(ticker);
        }
    }

    private boolean tickerExists(String ticker, List<WatchedTicker> tickers) {
        for(WatchedTicker watchedTicker : tickers) {
            if(watchedTicker.getTicker().equals(ticker)) return true;
        }
        return false;
    }


}
