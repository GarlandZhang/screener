package com.gzhang.screener.repositories;

import com.gzhang.screener.models.DailyStockData;

import java.util.List;

public interface DailyStockDataRepository {

    DailyStockData save (DailyStockData dailyStockData);

    List<DailyStockData> getDailyStockDataByMetadataId(int metadataId);
}
