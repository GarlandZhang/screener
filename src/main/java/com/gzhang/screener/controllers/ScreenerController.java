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

/**
 * handles request mappings for grouping related api calls; also includes stock screening functionality
 */
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

    /**
     * saves performance indicator (added to a new grouping)
     * @param userId
     * @param screenIndicatorInput
     * @return
     */
    @PostMapping("/user/{userId}/save/indicator")
    public ResponseEntity<ScreenIndicatorGroupingOutput> saveNewIndicator(@PathVariable int userId, @RequestBody ScreenIndicatorInput screenIndicatorInput) {
        if(!validIndicatorInput(screenIndicatorInput)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("Message", "Bad field input.")
                    .body(null);
        }

        // store in grouping
        ScreenIndicatorGrouping grouping = new ScreenIndicatorGrouping();
        grouping = screenIndicatorGroupingRepository.save(grouping); //save to retrieve id (this is not optimal; TODO look into UUID)
        grouping.addIndicator(screenIndicatorInput.toScreenIndicator());
        grouping.setUserId(userId);

        // save and return
        grouping = screenIndicatorGroupingRepository.save(grouping);
        return ResponseEntity.status(HttpStatus.OK)
                .header("Message", "Indicator created.")
                .body(new ScreenIndicatorGroupingOutput(grouping));
    }

    /**
     * saves a new grouping (a wrapper for a list of performance indicators)
     * @param userId
     * @param groupingInput
     * @return
     */
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

    /**
     * retrieves grouping based on groupId
     * @param groupId
     * @return
     */
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

    /**
     * screens for stocks based on grouping performance indicators
     * TODO: extremely unoptimal (look into distributed databases, opening multiple sessions, preprocessed data...)
     * @param groupId
     * @return
     */
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
            }
        }

        return ResponseEntity.status(HttpStatus.OK)
                .header("Message", "OK")
                .body(symbolList);
    }

    /**
     * update existing group by adding another indicator
     * @param groupId
     * @param indicatorInput
     * @return
     */
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

    /**
     * update existing group by removing an indicator
     * @param groupId
     * @param indicatorId
     * @return
     */
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

    /**
     * delete a grouping (and its children indicators)
     * @param groupId
     * @return
     */
    @DeleteMapping("/group/{groupId}")
    public ResponseEntity<String> deleteGrouping(@PathVariable int groupId) {
        screenIndicatorGroupingRepository.deleteById(groupId);

        return ResponseEntity.status(HttpStatus.OK)
                .header("Message", "Deleted group")
                .body(null);
    }

    /**
     * returns true if stock meets grouping indicators
     * @param stock
     * @param screenIndicatorGrouping
     * @return
     */
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

    /**
     * return true if grouping is invalid (checks for bad input)
     * @param groupingInput
     * @return
     */
    private boolean validGroupingInput(ScreenIndicatorGroupingInput groupingInput) {
        if(groupingInput.getScreenIndicatorInputList() == null || groupingInput.getScreenIndicatorInputList().size() == 0) return false;

        // validate each indicator
        for(ScreenIndicatorInput screenIndicatorInput : groupingInput.getScreenIndicatorInputList()) {
            if(!validIndicatorInput(screenIndicatorInput)) return false;
        }

        return true;
    }

    /**
     * returns true if indicatir is invalid (checks for bad input)
     * @param screenIndicatorInput
     * @return
     */
    private boolean validIndicatorInput(ScreenIndicatorInput screenIndicatorInput) {
        return screenIndicatorInput != null && !screenIndicatorInput.getParameterTimeInterval().equals("");
    }

    /**
     * returns true if stock meets specified indicator based on percentchange, time interval, and performance direction
     * @param stock
     * @param performancePercentChange
     * @param performanceTimeInterval
     * @param performanceDirection
     * @return
     */
    private boolean stockMeetsPerformanceCriteria(StockMetadata stock, float performancePercentChange, String performanceTimeInterval, boolean performanceDirection) {

        // get list of daily stock data for the given stock
        // assumption: sorted in reverse chronological order
        List<DailyStockData> dailyStockData = dailyStockDataRepository.getNumDailyStockDataWithMetadataId(stock.getId(), 1 + interpretIntervalIntoDays(performanceTimeInterval));
        if(dailyStockData == null || dailyStockData.size() == 0) return false;

        // get entries to compare for meeting performance criteria
        DailyStockData latestDayEntry = getDayEntry(dailyStockData, "0D");
        DailyStockData performanceDayEntry = getDayEntry(dailyStockData, performanceTimeInterval);

        return dayEntryMeetsCriteria(latestDayEntry, performanceDayEntry, performancePercentChange, performanceDirection);
    }

    /**
     * return number of days based on textual argument
     * @param performanceTimeInterval
     * @return
     */
    private int interpretIntervalIntoDays(String performanceTimeInterval) {
        int length = performanceTimeInterval.length();
        int quantity = Integer.parseInt(performanceTimeInterval.substring(0, length - 1));
        char appendingChar = performanceTimeInterval.charAt(length - 1);
        if(appendingChar == 'D') return quantity;
        if(appendingChar == 'W') return 7 * quantity;
        return quantity; // todo
    }

    /**
     * returns true if a day entry in the stocks history meets requirements of performance indicator
     * @param latestDayEntry
     * @param performanceDayEntry
     * @param performancePercentChange
     * @param performanceDirection
     * @return
     */
    private boolean dayEntryMeetsCriteria(DailyStockData latestDayEntry, DailyStockData performanceDayEntry, float performancePercentChange, boolean performanceDirection) {
        // compares close price of latest entry to close price of timeEntry
        // true -> above percentage
        float change = (latestDayEntry.getClosePrice()
                - performanceDayEntry.getClosePrice())
                        / performanceDayEntry.getOpenPrice();

        if(latestDayEntry.getStockMetadata().getTicker().equals("ABEOW")) {
            System.out.println();
        }

        return performanceDirection ? change >= (performancePercentChange / 100.00) :
                                      change <= (performancePercentChange / 100.00);
    }

    /**
     * return the oldest candidate entry based on the time interval, timeIntervalField
     * @param dailyStockDataList
     * @param timeIntervalField
     * @return
     */
    private DailyStockData getDayEntry(List<DailyStockData> dailyStockDataList, String timeIntervalField) {
        TimeInterval timeInterval = getTimeIntervalFromField(timeIntervalField);

        // get latest date
        Date latestDate = dailyStockDataList.get(0).getDateCreated();
        DailyStockData candidate = null;

        // get closest dataEntry to the time interval
        for(DailyStockData dailyStockData : dailyStockDataList) {
            if(withinTimeInterval(dailyStockData.getDateCreated(), latestDate, timeInterval) || candidate == null) candidate = dailyStockData;
            else break;
        }

        if(candidate == null) return dailyStockDataList.get(0);
        return candidate;

    }

    /**
     * return true if a date is within the timeInterval specified
     * @param dateEntry
     * @param latestDate
     * @param timeInterval
     * @return
     */
    private boolean withinTimeInterval(Date dateEntry, Date latestDate, TimeInterval timeInterval) {
        return latestDate.getTime()
             - dateEntry.getTime()
                <= timeInterval.getNumMillis();
    }

    /**
     * return enum of the type specified by the timeIntervalField
     * TODO: dynamic rather than hardcoded (parse string for value and calculate for numMillis)
     * @param timeIntervalField
     * @return
     */
    private TimeInterval getTimeIntervalFromField(String timeIntervalField) {
        if(timeIntervalField.equals("0D")) return TimeInterval.LATEST;
        if(timeIntervalField.equals("1D")) return TimeInterval.PAST_DAY;
        if(timeIntervalField.equals("3D")) return TimeInterval.PAST_THREE_DAYS;
//        if(timeIntervalField.equals("PAST WEEK")) return TimeInterval.PAST_WEEK;

        // temporary TODO
        return TimeInterval.LATEST;
    }
}
