package com.gzhang.screener.repositories;

import com.gzhang.screener.models.StockMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockMetadataJpaRepository extends JpaRepository<StockMetadata, Integer> {

    StockMetadata findStockMetadataById(int id);

    StockMetadata findStockMetadataByTicker(String ticker);
}
