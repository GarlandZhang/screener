package com.gzhang.screener.schedulers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

@Component
public class ScheduledTasks {
    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

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

        ++index;
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
