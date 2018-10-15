package com.gzhang.screener.repositories;

import com.gzhang.screener.iomodels.DailyStockData;
import com.gzhang.screener.iomodels.StockMetadata;

public interface DailyStockDataRepository {

    DailyStockData save (DailyStockData dailyStockData);
}
