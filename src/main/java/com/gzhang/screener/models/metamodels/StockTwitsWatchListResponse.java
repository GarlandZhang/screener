package com.gzhang.screener.models.metamodels;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockTwitsWatchListResponse {
    int id;
    String name;
    String updated_at;
    String created_at;
    List<StockTwitsWatchedTicker> symbols;
}
