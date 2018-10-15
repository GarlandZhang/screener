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
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Date;
import java.util.List;

@Controller
public class ScreenerController {

    @Autowired
    StockMetadataRepository stockMetadataRepository;

    @Autowired
    DailyStockDataRepository dailyStockDataRepository;

    @GetMapping("/screen/stocks")
    public SymbolList screenStocksWithPerformanceIndicators(@RequestParam(defaultValue = "5") Float performance1PercentChange,
                                                            @RequestParam(defaultValue = "LATEST") String performance1TimeInterval,
                                                            @RequestParam(defaultValue = "true") Boolean performance1Direction,
                                                            @RequestParam(required = false) Float performance2PercentChange,
                                                            @RequestParam(required = false) String performance2TimeInterval,
                                                            @RequestParam(required = false) Boolean performance2Direction) {
        // sanitize input
        if(performance2PercentChange == null) performance2PercentChange = performance1PercentChange;
        if(performance2TimeInterval == null) performance2TimeInterval = performance1TimeInterval;
        if(performance2Direction == null) performance2Direction = performance1Direction;

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
                    performance2PercentChange,
                    performance2TimeInterval,
                    performance2Direction)) {
                symbolList.add(stock);
                System.out.println(stock.getTicker());
            }
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
        return performanceDirection ? change >= (performancePercentChange / 100.00) :
                                      change <= (performancePercentChange / 100.00);
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
        return latestDate.getTime()
             - dateEntry.getTime()
                <= timeInterval.getNumMillis();
    }

    private TimeInterval getTimeIntervalFromField(String timeIntervalField) {
        if(timeIntervalField.equals("LATEST")) return TimeInterval.LATEST;
        if(timeIntervalField.equals("PAST THREE DAYS")) return TimeInterval.PAST_THREE_DAYS;
        if(timeIntervalField.equals("PAST WEEK")) return TimeInterval.PAST_WEEK;

        // temporary TODO
        return TimeInterval.LATEST;
    }
}
