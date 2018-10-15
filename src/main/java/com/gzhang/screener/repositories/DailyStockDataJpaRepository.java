package com.gzhang.screener.repositories;

import com.gzhang.screener.iomodels.DailyStockData;
import com.gzhang.screener.iomodels.StockMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyStockDataJpaRepository extends JpaRepository<DailyStockData, Integer> {

}
