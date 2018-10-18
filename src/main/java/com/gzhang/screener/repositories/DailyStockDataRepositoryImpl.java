package com.gzhang.screener.repositories;

import com.gzhang.screener.models.DailyStockData;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@NoArgsConstructor
public class DailyStockDataRepositoryImpl implements DailyStockDataRepository {

    @Autowired
    DailyStockDataJpaRepository dailyStockDataJpaRepository;

    @Override
    public DailyStockData save(DailyStockData dailyStockData) {
        return dailyStockDataJpaRepository.save(dailyStockData);
    }

    @Override
    public List<DailyStockData> getDailyStockDataByMetadataId(int metadataId) {
        return dailyStockDataJpaRepository.findAllByMetadataId(metadataId);
    }

    @Override
    public List<DailyStockData> getNumDailyStockDataWithMetadataId(int id, int numEntries) {
//        return dailyStockDataJpaRepository.findTopNByMetadataId(id, numEntries);
        return dailyStockDataJpaRepository.findAllByMetadataId(id);
    }
}
