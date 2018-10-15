package com.gzhang.screener.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class WatchedTicker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    int watchListId;
    String ticker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "watchListId", insertable = false, updatable = false)
    WatchList watchList;
}
