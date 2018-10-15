package com.gzhang.screener.repositories;

import com.gzhang.screener.iomodels.StockMetadata;

import java.util.List;

public interface StockMetadataRepository {

    StockMetadata save(StockMetadata stockMetadata);

    StockMetadata getById(int id);

    List<StockMetadata> getAll();

    StockMetadata getByTickerSymbol(String ticker);
}
