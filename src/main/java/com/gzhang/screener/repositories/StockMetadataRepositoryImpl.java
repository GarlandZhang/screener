package com.gzhang.screener.repositories;

import com.gzhang.screener.models.StockMetadata;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

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
    public List<StockMetadata> getAll() {
        return stockMetadataJpaRepository.findAll();

    }

    @Override
    public StockMetadata getByTickerSymbol(String ticker) {
        return stockMetadataJpaRepository.findStockMetadataByTicker(ticker);
    }
}
