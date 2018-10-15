package com.gzhang.screener.controllers;

import com.gzhang.screener.iomodels.DailyStockData;
import com.gzhang.screener.iomodels.ScreenIndicator;
import com.gzhang.screener.iomodels.ScreenIndicatorGrouping;
import com.gzhang.screener.iomodels.StockMetadata;
import com.gzhang.screener.iomodels.metamodels.ListOfScreenIndicatorGroupings;
import com.gzhang.screener.iomodels.metamodels.SymbolList;
import com.gzhang.screener.iomodels.metamodels.TimeInterval;
import com.gzhang.screener.repositories.DailyStockDataRepository;
import com.gzhang.screener.repositories.StockMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

@RestController
public class ScreenerController {

    @Autowired
    StockMetadataRepository stockMetadataRepository;

    @Autowired
    DailyStockDataRepository dailyStockDataRepository;

    @GetMapping("/screen/stocks")
    public ResponseEntity<SymbolList> screenStocksWithPerformanceIndicators(@RequestBody ScreenIndicatorGrouping grouping) {
        // sanitize input
        if(!validGrouping(grouping)) {
            return ResponseEntity.status(HttpStatus.OK)
                    .header("Status", "200: OK")
                    .body(new SymbolList());
        }

        // fetch all stocks
        List<StockMetadata> listOfStocks = stockMetadataRepository.getAll();

        // list to return
        SymbolList symbolList = new SymbolList();

        // check each stock
        for(StockMetadata stock : listOfStocks) {
            // check all performance indicator
            if(stockMeetsScreenGrouping(stock, grouping)) {
                symbolList.add(stock);
                System.out.println(stock.getTicker());
            }
        }

        return ResponseEntity.status(HttpStatus.OK)
                .header("Status", "200: OK")
                .body(symbolList);
    }

    private boolean stockMeetsScreenGrouping(StockMetadata stock, ScreenIndicatorGrouping screenIndicatorGrouping) {
        // check each performance indicator
        for(ScreenIndicator screenIndicator : screenIndicatorGrouping.getScreenIndicatorList()) {
            if(!stockMeetsPerformanceCriteria(stock,
                    screenIndicator.getParameterPercentChange(),
                    screenIndicator.getParameterTimeInterval(),
                    screenIndicator.isParameterDirection())) return false;
        }

        return true;
    }

    private boolean validGrouping(ScreenIndicatorGrouping grouping) {
        return grouping.getScreenIndicatorList() != null && grouping.getScreenIndicatorList().size() > 0;
    }

    private boolean stockMeetsPerformanceCriteria(StockMetadata stock, float performancePercentChange, String performanceTimeInterval, boolean performanceDirection) {

        // get list of daily stock data for the given stock
        // assumption: sorted in reverse chronological order
        List<DailyStockData> dailyStockData = dailyStockDataRepository.getDailyStockDataByMetadataId(stock.getId());

        // get entries to compare for meeting performance criteria
        DailyStockData latestDayEntry = getDayEntry(dailyStockData, "LATEST");
        DailyStockData performance1DayEntry = getDayEntry(dailyStockData, performanceTimeInterval);

        return dayEntryMeetsCriteria(latestDayEntry, performance1DayEntry, performancePercentChange, performanceDirection);
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
