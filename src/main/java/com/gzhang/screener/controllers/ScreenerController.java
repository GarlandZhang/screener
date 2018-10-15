package com.gzhang.screener.controllers;

import com.gzhang.screener.iomodels.DailyStockData;
import com.gzhang.screener.iomodels.StockMetadata;
import com.gzhang.screener.iomodels.metamodels.SymbolList;
import com.gzhang.screener.iomodels.metamodels.TimeInterval;
import com.gzhang.screener.repositories.DailyStockDataRepository;
import com.gzhang.screener.repositories.StockMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.sql.Date;
import java.util.List;

@Controller
public class ScreenerController {

    @Autowired
    StockMetadataRepository stockMetadataRepository;

    @Autowired
    DailyStockDataRepository dailyStockDataRepository;

    @GetMapping("/test")
    public SymbolList screenStocksWithPerformanceIndicators(float performance1PercentChange,
                                                            String performance1TimeInterval,
                                                            boolean performance1Direction,
                                                            float perforomance2PercentChange,
                                                            String performance2TimeInterval,
                                                            boolean performance2Direction) {
        // fetch all stocks
        List<StockMetadata> listOfStocks = stockMetadataRepository.getAll();

        // list to return
        SymbolList symbolList = new SymbolList();

        // check each stock
        for(StockMetadata stock : listOfStocks) {
            if (stockMeetsPerformanceCriteria(stock,
                    performance1PercentChange,
                    performance1TimeInterval,
                    performance1Direction,
                    perforomance2PercentChange,
                    performance2TimeInterval,
                    performance2Direction)) symbolList.add(stock);
        }

        return symbolList;
    }

    private boolean stockMeetsPerformanceCriteria(StockMetadata stock, float performance1PercentChange, String performance1TimeInterval, boolean performance1Direction, float performance2PercentChange, String performance2TimeInterval, boolean performance2Direction) {

        // get list of daily stock data for the given stock
        // assumption: sorted in reverse chronological order
        List<DailyStockData> dailyStockData = dailyStockDataRepository.getDailyStockDataByMetadataId(stock.getId());

        // get entries to compare for meeting performance criteria
        DailyStockData latestDayEntry = getDayEntry(dailyStockData, "LATEST");
        DailyStockData performance1DayEntry = getDayEntry(dailyStockData, performance1TimeInterval);
        DailyStockData performance2DayEntry = getDayEntry(dailyStockData, performance2TimeInterval);

        return dayEntryMeetsCriteria(latestDayEntry, performance1DayEntry, performance1PercentChange, performance1Direction)
            && dayEntryMeetsCriteria(latestDayEntry, performance2DayEntry, performance2PercentChange, performance2Direction);
    }

    private boolean dayEntryMeetsCriteria(DailyStockData latestDayEntry, DailyStockData performanceDayEntry, float performancePercentChange, boolean performanceDirection) {
        // compares close price of latest entry to open price of timeEntry
        // true -> above percentage
        float change = (latestDayEntry.getClosePrice()
                - performanceDayEntry.getOpenPrice())
                        / performanceDayEntry.getOpenPrice();
        return performanceDirection ? change >= performancePercentChange :
                                      change <= performancePercentChange;
    }

    private DailyStockData getDayEntry(List<DailyStockData> dailyStockDataList, String timeIntervalField) {
        TimeInterval timeInterval = getTimeIntervalFromField(timeIntervalField);
        if(timeInterval == TimeInterval.LATEST) return dailyStockDataList.get(0);
        if(timeInterval == TimeInterval.PAST_THREE_DAYS) {
            // get latest date
            Date latestDate = getDayEntry(dailyStockDataList, "LATEST").getDateCreated();
            DailyStockData candidate = dailyStockDataList.get(0);

            // get closest dataEntry to the time interval
            for(DailyStockData dailyStockData : dailyStockDataList) {
                if(withinTimeInterval(dailyStockData.getDateCreated(), latestDate, timeInterval)) candidate = dailyStockData;
                else break;
            }

            return candidate;
        }
        if(timeInterval == TimeInterval.PAST_WEEK) {
            // do something
        }

        // more fields TODO

        // temporary until more fields
        return getDayEntry(dailyStockDataList, "LATEST");
    }

    private boolean withinTimeInterval(Date dateEntry, Date latestDate, TimeInterval timeInterval) {
        return latestDate.getTime() - dateEntry.getTime() < timeInterval.getNumMillis();
    }

    private TimeInterval getTimeIntervalFromField(String timeIntervalField) {
        if(timeIntervalField.equals("LATEST")) return TimeInterval.LATEST;
        if(timeIntervalField.equals("PAST THREE DAYS")) return TimeInterval.PAST_THREE_DAYS;
        if(timeIntervalField.equals("PAST WEEK")) return TimeInterval.PAST_WEEK;

        // temporary TODO
        return TimeInterval.LATEST;
    }
}
