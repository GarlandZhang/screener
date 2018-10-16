package com.gzhang.screener.models.iomodels;

import com.gzhang.screener.models.metamodels.StockTwitsWatchListResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockTwitsWatchListsResponse {
    Object response;
    List<StockTwitsWatchListResponse> watchlists;
}
