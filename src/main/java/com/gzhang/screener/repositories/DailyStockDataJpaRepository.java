package com.gzhang.screener.repositories;

import com.gzhang.screener.iomodels.DailyStockData;
import com.gzhang.screener.iomodels.StockMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DailyStockDataJpaRepository extends JpaRepository<DailyStockData, Integer> {

    @Query ("SELECT d FROM DailyStockData d WHERE d.metadataId = :metadataId ORDER BY d.dateCreated DESC")
    List<DailyStockData> findAllByMetadataId(@Param("metadataId") int metadataId);
}
