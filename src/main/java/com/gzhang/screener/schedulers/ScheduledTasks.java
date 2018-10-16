package com.gzhang.screener.schedulers;

import com.gzhang.screener.models.DailyStockData;
import com.gzhang.screener.models.StockMetadata;
import com.gzhang.screener.models.metamodels.AlphavantageStockHistory;
import com.gzhang.screener.models.metamodels.AlphavantageStockQuote;
import com.gzhang.screener.models.metamodels.MetaData;
import com.gzhang.screener.models.metamodels.TimeEntry;
import com.gzhang.screener.repositories.DailyStockDataRepository;
import com.gzhang.screener.repositories.StockMetadataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;

@Component
public class ScheduledTasks {

    private static final int NUM_STOCKS = 6608;
    @Autowired
    StockMetadataRepository stockMetadataRepository;

    @Autowired
    DailyStockDataRepository dailyStockDataRepository;
    
    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final String API_KEY = "RD164SI5XMGA32WL";

    private static final int MAX_FAILS = 5;
    private static int numFailedAttempts;
    private static boolean successApiCall;
    private static String tickerSymbol;
    private static Scanner sc;
    private static int tickerIndex;

    public ScheduledTasks() {
        successApiCall = true;
        tickerSymbol = "";
        numFailedAttempts = 0;
        tickerIndex = 0;
    }

    // ideal: "*/15 * 9-16 * * MON-FRI"
    @Scheduled(cron = "*/15 * * * * MON-FRI")
    public void getStockData() throws FileNotFoundException {
        refreshScanner();

        // get ticker symbol from file

        if(successApiCall) {
            tickerSymbol = sc.nextLine();
            numFailedAttempts = 0;
        }


        AlphavantageStockHistory alphavantageStockHistory = getStockHistory(tickerSymbol);
        if(alphavantageStockHistory == null) return;

        StockMetadata stockMetadata = stockMetadataRepository.getByTickerSymbol(alphavantageStockHistory.getMetaData().getSymbol());
        if(stockMetadata == null) {
            stockMetadata = getStockMetadata(alphavantageStockHistory);
            System.out.println("added: " + stockMetadata.getTicker());
        } else {
            addNewestEntries(stockMetadata, alphavantageStockHistory.getTimeEntries());
            System.out.println("updated: " + stockMetadata.getTicker());
        }

        writeNewTickerIndex();
    }

    private StockMetadata getStockMetadata(AlphavantageStockHistory alphavantageStockHistory) {
        StockMetadata stockMetadata= new StockMetadata();
        stockMetadata.setTicker(alphavantageStockHistory.getMetaData().getSymbol());
        stockMetadata = stockMetadataRepository.save(stockMetadata);
        stockMetadata.setDailyStockDataList(new ArrayList<>());
        for(TimeEntry timeEntry : alphavantageStockHistory.getTimeEntries()) {
            stockMetadata.getDailyStockDataList().add(timeEntry.toDailyStockData(stockMetadata.getId()));
        }
        return stockMetadataRepository.save(stockMetadata);
    }

    private void writeNewTickerIndex() {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File("./src/main/resources/ticker-index.txt")));
            bufferedWriter.write("" + (++tickerIndex) % NUM_STOCKS);
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getStockHistoryUrl(String tickerSymbol) {
        return "https://www.alphavantage.co/query?" +
                "function=TIME_SERIES_DAILY" +
                "&symbol=" + tickerSymbol +
                "&outputsize=full" +
                "&apikey=" + API_KEY;
    }

    private void addNewestEntries(StockMetadata stockMetadata, List<TimeEntry> timeEntries) {
        // get latest entries in reverse chronological order
        List<DailyStockData> dailyStockDataList = dailyStockDataRepository.getDailyStockDataByMetadataId(stockMetadata.getId());
        if(dailyStockDataList == null || dailyStockDataList.size() == 0) return; // this should not occur but just in case

        DailyStockData mostUpdatedEntry = dailyStockDataList.get(0);

        // ensures only latest entries are added
        for(TimeEntry timeEntry : timeEntries) {
            if(timeEntry.getDate().getTime() > mostUpdatedEntry.getDateCreated().getTime()) {
                DailyStockData dailyStockData = timeEntry.toDailyStockData();
                dailyStockData.setMetadataId(stockMetadata.getId());
                dailyStockDataRepository.save(timeEntry.toDailyStockData());
            }
            else break;
        }
    }

    private AlphavantageStockHistory getStockHistory(String tickerSymbol) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Object> responseEntity = restTemplate.getForEntity(getStockHistoryUrl(tickerSymbol), Object.class);
        LinkedHashMap<String, LinkedHashMap> body = (LinkedHashMap<String, LinkedHashMap>) responseEntity.getBody();

        try{
            AlphavantageStockHistory alphavantageStockHistory = new AlphavantageStockHistory();

            // handle cases where I do not successfuly get the stock due to non-existing or api call limit
            if(body.get("Error Message") != null) return null;
            if(body.get("Information") != null) {
                successApiCall = false;
                return null;
            }
            String info = (String) body.get("Meta Data").get("1. Information");
            String symbol = (String) body.get("Meta Data").get("2. Symbol");
            String lastRefreshed = (String) body.get("Meta Data").get("3. Last Refreshed");
            String outputSize = (String) body.get("Meta Data").get("4. Output Size");
            String timeZone = (String) body.get("Meta Data").get("5. Time Zone");

            alphavantageStockHistory.setMetaData(new MetaData(info, symbol, lastRefreshed, outputSize, timeZone));

            // this is hardcoded for now... I can later make it more dynamic to search keys and check if this is INTRA_DAY vs DAILY
            body.get("Time Series (Daily)").forEach((dateEntry, timeEntry) -> {
                Date date = null;
                try {
                    date = new Date(dateFormat.parse((String)dateEntry).getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                float openPrice = Float.parseFloat(((LinkedHashMap<String, String>)timeEntry).get("1. open"));
                float highPrice = Float.parseFloat(((LinkedHashMap<String, String>)timeEntry).get("2. high"));
                float lowPrice = Float.parseFloat(((LinkedHashMap<String, String>)timeEntry).get("3. low"));
                float closePrice = Float.parseFloat(((LinkedHashMap<String, String>)timeEntry).get("4. close"));
                long volume = Long.parseLong(((LinkedHashMap<String, String>) timeEntry).get("5. volume"));

                alphavantageStockHistory.getTimeEntries().add(new TimeEntry(date, openPrice, highPrice, lowPrice, closePrice, volume));
            });
            successApiCall = true;
            return alphavantageStockHistory;
        } catch(Exception e) {
            e.printStackTrace();
            if(numFailedAttempts == MAX_FAILS) {
                successApiCall = true;
                numFailedAttempts = 0;
            }
            else {
                ++numFailedAttempts;
                successApiCall = false;
            }
            return null;
        }
    }

    public static AlphavantageStockQuote getStockQuote(String tickerSymbol) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Object> responseEntity = restTemplate.getForEntity(getStockQuoteUrl(tickerSymbol), Object.class);
        LinkedHashMap<String, LinkedHashMap> body = (LinkedHashMap<String, LinkedHashMap>) responseEntity.getBody();
        AlphavantageStockQuote quote = null;
        try {
            if(body.get("Error Message") != null || body.get("Information") != null) return null;

            LinkedHashMap<String, String> globalQuote = body.get("Global Quote");
            String symbol = globalQuote.get("01. symbol");
            float openPrice = Float.parseFloat(globalQuote.get("02. open"));
            float highPrice = Float.parseFloat(globalQuote.get("03. high"));
            float lowPrice = Float.parseFloat(globalQuote.get("04. low"));
            float price = Float.parseFloat(globalQuote.get("05. price"));
            long volume = Long.parseLong(globalQuote.get("06. volume"));
            Date date = new Date(dateFormat.parse((String)globalQuote.get("07. latest trading day")).getTime());
            float previousClose = Float.parseFloat(globalQuote.get("08. previous close"));
            float change = Float.parseFloat(globalQuote.get("09. change"));
            float changePercent = Float.parseFloat(globalQuote.get("10. change percent").replace("%", ""));

            quote = new AlphavantageStockQuote(symbol,
                    openPrice,
                    highPrice,
                    lowPrice,
                    price,
                    volume,
                    date,
                    previousClose,
                    change,
                    changePercent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return quote;
    }

    private static String getStockQuoteUrl(String tickerSymbol) {
        return "https://www.alphavantage.co/query?" +
                "function=GLOBAL_QUOTE" +
                "&symbol=" + tickerSymbol +
                "&apikey=" + API_KEY;
    }

    private void refreshScanner() throws FileNotFoundException {
        if(sc != null && !sc.hasNextLine()) {
            sc.close();
            sc = null;
        }

        if(sc == null) {
            sc = new Scanner(new File("./src/main/resources/stock-tickers.txt"));

            // get ticker index in file
            tickerIndex = getTickerIndex();
            for(int i = tickerIndex; i > 1; --i) sc.nextLine(); // i > 1 instead of i > 0 because check make sure previous entry is clean
        }
    }

    private int getTickerIndex() throws FileNotFoundException {
        Scanner sc = new Scanner(new File("./src/main/resources/ticker-index.txt"));
        int tickerIndex = Integer.parseInt(sc.nextLine());
        return tickerIndex;
    }
}
