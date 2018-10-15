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
        grouping = screenIndicatorGroupingRepository.save(grouping);
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

    @GetMapping("/group/{groupId}")
    public ResponseEntity<ScreenIndicatorGroupingOutput> getGrouping(@PathVariable int groupId) {

        ScreenIndicatorGrouping grouping = screenIndicatorGroupingRepository.getById(groupId);

        if(grouping == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("Message", "Bad field input.")
                    .body(null);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .header("Message", "Group found.")
                .body(new ScreenIndicatorGroupingOutput(grouping));
    }

    @GetMapping("/group/{groupId}/screen/stocks")
    public ResponseEntity<SymbolList> screenStocksWithPerformanceIndicators(@PathVariable int groupId) {
        ScreenIndicatorGrouping grouping = screenIndicatorGroupingRepository.getById(groupId);
        // sanitize input
        if(grouping == null) {
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
            if(stockMeetsScreenGrouping(stock, grouping)) {
                symbolList.add(stock);
                System.out.println(stock.getTicker());
            }
        }

        return ResponseEntity.status(HttpStatus.OK)
                .header("Message", "OK")
                .body(symbolList);
    }

    @PutMapping("/group/{groupId}/add/indicator")
    public ResponseEntity<ScreenIndicatorGroupingOutput> addIndicatorToGrouping(@PathVariable int groupId, @RequestBody ScreenIndicatorInput indicatorInput) {
        if(!validIndicatorInput(indicatorInput)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("Message", "Bad field input.")
                    .body(null);
        }

        ScreenIndicatorGrouping grouping = screenIndicatorGroupingRepository.getById(groupId);

        if(grouping == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("Message", "Bad field input.")
                    .body(null);
        }

        grouping.addIndicator(indicatorInput.toScreenIndicator());
        grouping = screenIndicatorGroupingRepository.save(grouping);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .header("Message", "Added indicator")
                .body(new ScreenIndicatorGroupingOutput(grouping));
    }

    @PutMapping("/group/{groupId}/remove/indicator/{indicatorId}")
    public ResponseEntity<ScreenIndicatorGroupingOutput> removeIndicatorToGrouping(@PathVariable int groupId, @PathVariable int indicatorId) {
        ScreenIndicatorGrouping grouping = screenIndicatorGroupingRepository.getById(groupId);

        if(grouping == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("Message", "Bad field input.")
                    .body(null);
        }

        // check if removing indicator is not possible
        if(!grouping.removeIndicatorById(indicatorId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("Message", "Bad field input.")
                    .body(new ScreenIndicatorGroupingOutput(grouping));
        };

        // check if size is now 0
        if(grouping.getScreenIndicatorList().size() == 0) {
            screenIndicatorGroupingRepository.delete(grouping);

            return ResponseEntity.status(HttpStatus.OK)
                    .header("Message", "Group is now empty. Group deleted.")
                    .body(new ScreenIndicatorGroupingOutput());
        }

        return ResponseEntity.status(HttpStatus.OK)
                .header("Message", "Added indicator")
                .body(new ScreenIndicatorGroupingOutput(grouping));
    }

    @DeleteMapping("/group/{groupId}")
    public ResponseEntity<String> deleteGrouping(@PathVariable int groupId) {
        screenIndicatorGroupingRepository.deleteById(groupId);

        return ResponseEntity.status(HttpStatus.OK)
                .header("Message", "Deleted group")
                .body(null);
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
