package com.gzhang.screener.controllers;

import com.gzhang.screener.models.DailyStockData;
import com.gzhang.screener.models.ScreenIndicator;
import com.gzhang.screener.models.ScreenIndicatorGrouping;
import com.gzhang.screener.models.StockMetadata;
import com.gzhang.screener.models.iomodels.ScreenIndicatorGroupingInput;
import com.gzhang.screener.models.iomodels.ScreenIndicatorGroupingOutput;
import com.gzhang.screener.models.iomodels.ScreenIndicatorInput;
import com.gzhang.screener.models.metamodels.SymbolList;
import com.gzhang.screener.models.metamodels.TimeInterval;
import com.gzhang.screener.repositories.DailyStockDataRepository;
import com.gzhang.screener.repositories.ScreenIndicatorGroupingRepository;
import com.gzhang.screener.repositories.StockMetadataRepository;
import com.gzhang.screener.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

@RestController
public class ScreenerController {

    @Autowired
    StockMetadataRepository stockMetadataRepository;

    @Autowired
    DailyStockDataRepository dailyStockDataRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ScreenIndicatorGroupingRepository screenIndicatorGroupingRepository;

    @PostMapping("/user/{userId}/save/indicator")
    public ResponseEntity<ScreenIndicatorGroupingOutput> saveNewIndicator(@PathVariable int userId, @RequestBody ScreenIndicatorInput screenIndicatorInput) {
        if(!validIndicatorInput(screenIndicatorInput)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("Message", "Bad field input.")
                    .body(null);
        }

        // store in grouping
        ScreenIndicatorGrouping grouping = new ScreenIndicatorGrouping();
        grouping.addIndicator(screenIndicatorInput.toScreenIndicator());
        grouping.setUserId(userId);

        // save and return
        grouping = screenIndicatorGroupingRepository.save(grouping);
        return ResponseEntity.status(HttpStatus.OK)
                .header("Message", "Indicator created.")
                .body(new ScreenIndicatorGroupingOutput(grouping));
    }

    @PostMapping("/user/{userId}/save/grouping")
    public ResponseEntity<ScreenIndicatorGroupingOutput> saveNewGrouping(@PathVariable int userId, @RequestBody ScreenIndicatorGroupingInput groupingInput) {
        if(!validGroupingInput(groupingInput)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("Message", "Bad field input.")
                    .body(null);
        }

        ScreenIndicatorGrouping grouping = groupingInput.toGrouping();
        grouping.setUserId(userId);

        grouping = screenIndicatorGroupingRepository.save(grouping);
        return ResponseEntity.status(HttpStatus.OK)
                .header("Message", "Indicator created.")
                .body(new ScreenIndicatorGroupingOutput(grouping));
    }


    @GetMapping("/screen/stocks")
    public ResponseEntity<SymbolList> screenStocksWithPerformanceIndicators(@RequestBody ScreenIndicatorGroupingInput groupingInput) {
        // sanitize input
        if(!validGroupingInput(groupingInput)) {
            return ResponseEntity.status(HttpStatus.OK)
                    .header("Message", "OK")
                    .body(new SymbolList());
        }

        // fetch all stocks
        List<StockMetadata> listOfStocks = stockMetadataRepository.getAll();

        // list to return
        SymbolList symbolList = new SymbolList();

        // check each stock
        for(StockMetadata stock : listOfStocks) {
            // check all performance indicator
            if(stockMeetsScreenGrouping(stock, groupingInput)) {
                symbolList.add(stock);
                System.out.println(stock.getTicker());
            }
        }

        return ResponseEntity.status(HttpStatus.OK)
                .header("Status", "200: OK")
                .body(symbolList);
    }

    private boolean stockMeetsScreenGrouping(StockMetadata stock, ScreenIndicatorGroupingInput screenIndicatorGroupingInput) {
        // check each performance indicator
        for(ScreenIndicatorInput screenIndicatorInput : screenIndicatorGroupingInput.getScreenIndicatorInputList()) {
            if(!stockMeetsPerformanceCriteria(stock,
                    screenIndicatorInput.getParameterPercentChange(),
                    screenIndicatorInput.getParameterTimeInterval(),
                    screenIndicatorInput.isParameterDirection())) return false;
        }

        return true;
    }

    private boolean validGroupingInput(ScreenIndicatorGroupingInput groupingInput) {
        if(groupingInput.getScreenIndicatorInputList() == null || groupingInput.getScreenIndicatorInputList().size() == 0) return false;

        // validate each indicator
        for(ScreenIndicatorInput screenIndicatorInput : groupingInput.getScreenIndicatorInputList()) {
            if(!validIndicatorInput(screenIndicatorInput)) return false;
        }

        return true;
    }


    private boolean validIndicatorInput(ScreenIndicatorInput screenIndicatorInput) {
        return screenIndicatorInput != null && !screenIndicatorInput.getParameterTimeInterval().equals("");
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
