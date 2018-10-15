package com.gzhang.screener.repositories;

import com.gzhang.screener.iomodels.StockMetadata;

public interface StockMetadataRepository {

    StockMetadata save(StockMetadata stockMetadata);

    StockMetadata getById(int id);

    StockMetadata getByTickerSymbol(String ticker);
}
