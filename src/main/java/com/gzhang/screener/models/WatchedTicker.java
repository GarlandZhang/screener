package com.gzhang.screener.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class WatchedTicker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    int watchListId;
    String ticker;
    String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "watchListId", insertable = false, updatable = false)
    WatchList watchList;

    public WatchedTicker(String ticker, String title) {
        this.ticker = ticker;
        this.title = title;
    }
}
