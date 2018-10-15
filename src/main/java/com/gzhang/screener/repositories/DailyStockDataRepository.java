package com.gzhang.screener.repositories;

import com.gzhang.screener.iomodels.DailyStockData;
import com.gzhang.screener.iomodels.StockMetadata;

import java.util.List;

public interface DailyStockDataRepository {

    DailyStockData save (DailyStockData dailyStockData);

    List<DailyStockData> getDailyStockDataByMetadataId(int metadataId);
}
