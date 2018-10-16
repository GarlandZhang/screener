package com.gzhang.screener.schedulers;

import com.gzhang.screener.models.metamodels.AlphavantageObject;
import com.gzhang.screener.models.metamodels.MetaData;
import com.gzhang.screener.models.metamodels.TimeEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Scanner;

@Component
public class ScheduledTasks {
    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private static int index;
    private static Scanner sc;

    public ScheduledTasks() {
        index = 0;
    }

    // ideal: "*/10 * 9-16 * * MON-FRI"
    @Scheduled(cron = "*/15 * * * * MON-FRI")
    public void getStockData() throws FileNotFoundException {
        refreshScanner();

        String tickerSymbol = sc.nextLine();

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Object> responseEntity = restTemplate.getForEntity("https://www.alphavantage.co/query?" +
                "function=TIME_SERIES_DAILY" +
                "&symbol=" + tickerSymbol +
                "&outputsize=full" +
                "&apikey=RD164SI5XMGA32WL", Object.class);

        System.out.println(responseEntity.getBody().toString());

        AlphavantageObject alphavantageObject = getAlphavantageObject((LinkedHashMap<String, LinkedHashMap>) responseEntity.getBody());



        ++index;
    }

    private AlphavantageObject getAlphavantageObject(LinkedHashMap<String, LinkedHashMap> body) {
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

        return alphavantageObject;
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
