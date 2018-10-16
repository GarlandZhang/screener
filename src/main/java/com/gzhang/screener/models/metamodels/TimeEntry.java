package com.gzhang.screener.models.metamodels;

import com.gzhang.screener.models.DailyStockData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimeEntry {
    Date date;
    float openPrice;
    float  highPrice;
    float lowPrice;
    float closePrice;
    long volume;

    public DailyStockData toDailyStockData() {
        DailyStockData dailyStockData = new DailyStockData();
        dailyStockData.setDateCreated(date);
        dailyStockData.setOpenPrice(openPrice);
        dailyStockData.setClosePrice(closePrice);
        dailyStockData.setHighPrice(highPrice);
        dailyStockData.setLowPrice(lowPrice);
        dailyStockData.setVolume(volume);

        return dailyStockData;
    }

    public DailyStockData toDailyStockData(int id) {
        DailyStockData data = toDailyStockData();
        data.setMetadataId(id);
        return data;
    }
}
