package com.gzhang.screener.schedulers;

import com.gzhang.screener.models.DailyStockData;
import com.gzhang.screener.models.StockMetadata;
import com.gzhang.screener.models.metamodels.AlphavantageObject;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;

@Component
public class ScheduledTasks {
    
    @Autowired
    StockMetadataRepository stockMetadataRepository;

    @Autowired
    DailyStockDataRepository dailyStockDataRepository;
    
    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final String API_KEY = "RD164SI5XMGA32WL";

    private static boolean successApiCall;
    private static String tickerSymbol;
    private static Scanner sc;

    public ScheduledTasks() {
        successApiCall = true;
        tickerSymbol = "";
    }

    // ideal: "*/15 * 9-16 * * MON-FRI"
    @Scheduled(cron = "*/15 * * * * MON-FRI")
    public void getStockData() throws FileNotFoundException {
        refreshScanner();

        // get ticker symbol from file

        if(successApiCall) {
            tickerSymbol = sc.nextLine();
        }


        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Object> responseEntity = restTemplate.getForEntity(getAlphavantageUrl(tickerSymbol), Object.class);

        AlphavantageObject alphavantageObject = getAlphavantageObject((LinkedHashMap<String, LinkedHashMap>) responseEntity.getBody());
        if(alphavantageObject == null) return;

        StockMetadata stockMetadata = stockMetadataRepository.getByTickerSymbol(alphavantageObject.getMetaData().getSymbol());
        if(stockMetadata == null) {
            stockMetadata = alphavantageObject.toStockMetadata();
            stockMetadataRepository.save(stockMetadata);
            System.out.println("added: " + stockMetadata.getTicker());
        } else {
            addNewestEntries(stockMetadata, alphavantageObject.getTimeEntries());
            System.out.println("updated: " + stockMetadata.getTicker());
        }
    }

    private String getAlphavantageUrl(String tickerSymbol) {
        return "https://www.alphavantage.co/query?" +
                "function=TIME_SERIES_DAILY" +
                "&symbol=" + tickerSymbol +
                "&outputsize=full" +
                "&apikey=" + API_KEY;
    }

    private void addNewestEntries(StockMetadata stockMetadata, List<TimeEntry> timeEntries) {
        // get latest entries in reverse chronological order
        List<DailyStockData> dailyStockDataList = dailyStockDataRepository.getDailyStockDataByMetadataId(stockMetadata.getId());

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


    private AlphavantageObject getAlphavantageObject(LinkedHashMap<String, LinkedHashMap> body) {

        try{
            AlphavantageObject alphavantageObject = new AlphavantageObject();

            String info = (String) body.get("Meta Data").get("1. Information");
            String symbol = (String) body.get("Meta Data").get("2. Symbol");
            String lastRefreshed = (String) body.get("Meta Data").get("3. Last Refreshed");
            String outputSize = (String) body.get("Meta Data").get("4. Output Size");
            String timeZone = (String) body.get("Meta Data").get("5. Time Zone");

            alphavantageObject.setMetaData(new MetaData(info, symbol, lastRefreshed, outputSize, timeZone));

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

                alphavantageObject.getTimeEntries().add(new TimeEntry(date, openPrice, highPrice, lowPrice, closePrice, volume));
            });
            successApiCall = true;
            return alphavantageObject;
        } catch(Exception e) {
            e.printStackTrace();
            successApiCall = false;
            return null;
        }
    }

    private void refreshScanner() throws FileNotFoundException {
        if(sc != null && !sc.hasNextLine()) {
            sc.close();
            sc = null;
        }

        if(sc == null) {
            sc = new Scanner(new File("./src/main/resources/stock-tickers.txt"));
        }
    }
}
