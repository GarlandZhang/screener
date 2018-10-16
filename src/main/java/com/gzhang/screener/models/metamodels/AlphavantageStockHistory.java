package com.gzhang.screener.models.metamodels;

import com.gzhang.screener.models.DailyStockData;
import com.gzhang.screener.models.StockMetadata;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class AlphavantageStockHistory {
    MetaData metaData;
    List<TimeEntry> timeEntries;

    public AlphavantageStockHistory() {
        metaData = new MetaData();
        timeEntries = new ArrayList<>();
    }
/*
    public StockMetadata toStockMetadata(int id) {
        StockMetadata stockMetadata = new StockMetadata();

        List<DailyStockData> dailyStockDataList = new ArrayList<>();
        stockMetadata.setTicker(metaData.getSymbol());

        for(TimeEntry timeEntry : timeEntries) {
            dailyStockDataList.add(timeEntry.toDailyStockData(id));
        }

        stockMetadata.setDailyStockDataList(dailyStockDataList);
        return stockMetadata;
    }*/
}
