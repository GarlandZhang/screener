package com.gzhang.screener.repositories;

import com.gzhang.screener.iomodels.StockMetadata;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@NoArgsConstructor
public class StockMetadataRepositoryImpl implements StockMetadataRepository {

    @Autowired
    StockMetadataJpaRepository stockMetadataJpaRepository;


    @Override
    public StockMetadata save(StockMetadata stockMetadata) {
        return stockMetadataJpaRepository.save(stockMetadata);
    }

    @Override
    public StockMetadata getById(int id) {
        return stockMetadataJpaRepository.findStockMetadataById(id);
    }

    @Override
    public StockMetadata getByTickerSymbol(String ticker) {
        return stockMetadataJpaRepository.findStockMetadataByTicker(ticker);
    }
}
