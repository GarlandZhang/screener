package com.gzhang.screener.models.metamodels;

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
public class AlphavantageObject {
    MetaData metaData;
    List<TimeEntry> timeEntries;

    public AlphavantageObject() {
        metaData = new MetaData();
        timeEntries = new ArrayList<>();
    }

    public StockMetadata toStockMetadata() {
        StockMetadata stockMetadata = new StockMetadata();
        stockMetadata.setDailyStockDataList(new ArrayList<>());
        stockMetadata.setTicker(metaData.getSymbol());

        for(TimeEntry timeEntry : timeEntries) {
            stockMetadata.getDailyStockDataList().add(timeEntry.toDailyStockData());
        }

        return stockMetadata;
    }
}
