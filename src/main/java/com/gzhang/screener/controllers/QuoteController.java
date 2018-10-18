package com.gzhang.screener.controllers;

import com.gzhang.screener.models.StockMetadata;
import com.gzhang.screener.models.metamodels.AlphavantageStockQuote;
import com.gzhang.screener.repositories.DailyStockDataRepository;
import com.gzhang.screener.repositories.StockMetadataRepository;
import com.gzhang.screener.schedulers.ScheduledTasks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * handles request mappings for Quote related API calls
 */
@RestController
public class QuoteController {

    @Autowired
    StockMetadataRepository stockMetadataRepository;

    @Autowired
    DailyStockDataRepository dailyStockDataRepository;

    @GetMapping("/quote/{tickerSymbol}")
    public ResponseEntity<AlphavantageStockQuote> getQuote(@PathVariable String tickerSymbol) {
        StockMetadata stockMetadata = stockMetadataRepository.getByTickerSymbol(tickerSymbol);
        if(stockMetadata == null);/* return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .header("Message", "Bad field input.")
                .body(null);*/
                //should return null but we dont have the data yet so...

        // fetch most up to date information
        AlphavantageStockQuote quote = ScheduledTasks.getStockQuote(tickerSymbol);
        if(quote == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .header("Message", "Bad field input.")
                .body(null);

        return ResponseEntity.status(HttpStatus.OK)
                .header("Message", "Quote retrieved.")
                .body(quote);
    }
}
