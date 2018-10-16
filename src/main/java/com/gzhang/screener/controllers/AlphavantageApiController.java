package com.gzhang.screener.controllers;

import com.gzhang.screener.models.DailyStockData;
import com.gzhang.screener.models.StockMetadata;
import com.gzhang.screener.repositories.DailyStockDataRepository;
import com.gzhang.screener.repositories.StockMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.sql.Date;
import java.util.Scanner;

/**
 * to fetch daily stock data
 */
@RestController
public class AlphavantageApiController {

    @Autowired
    StockMetadataRepository stockMetadataRepository;

    @Autowired
    DailyStockDataRepository dailyStockDataRepository;

    /**
     * parseStockData() parses through the text file that I created to temporarily store API responses
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/parse")
    public String parseStockData() throws Exception {
        Scanner sc = new Scanner(new File("./src/main/resources/new-stock-data.txt"));

        int index = 0;

        while(sc.hasNextLine()) {

            boolean isRepeat = true;

            ++index;
            String openBrace = sc.nextLine();
            if(!openBrace.equals("{")) {
                //System.out.println("index: " + index);
                throw new Exception("Open brace is not here but it should be.");
            }
            String metaDataTitle = sc.nextLine();
            if(metaDataTitle.contains("\"Information\": \"Thank you for using Alpha Vantage! ")) {
                sc.nextLine();
                continue;
            } else if(metaDataTitle.contains("\"Error Message\": \"Invalid API call.")) {
                sc.nextLine();
                continue;
            }

            String infoPair = sc.nextLine(); // not needed

            /* **** WHAT I WANT **** */
            String symbolPair = sc.nextLine();
            //additional string parsing for symbol
            String symbol = symbolPair.substring(symbolPair.indexOf(':') + ": \"".length(), symbolPair.lastIndexOf("\""));

            //System.out.println("Symbol: " + symbol);

            /* **** WHAT I WANT **** */

            StockMetadata stockMetadata = stockMetadataRepository.getByTickerSymbol(symbol);
            if(stockMetadata == null) {
                isRepeat = false;
                stockMetadata = new StockMetadata();
                stockMetadata.setTicker(symbol);
                stockMetadata = stockMetadataRepository.save(stockMetadata);
            }

            //clutter not needed
            String lastRefreshedPair = sc.nextLine();
            String outputSizePair = sc.nextLine();
            String timeZonePair = sc.nextLine();

            String closeMetaDataTitle = sc.nextLine();
            if(!closeMetaDataTitle.contains("},") && !closeMetaDataTitle.contains("}")) throw new Exception("Meta data close brace is not here but it should be.");

            String timerSeriesTitle = sc.nextLine();
            if(!timerSeriesTitle.contains("\"Time Series (Daily)\": {")) throw new Exception("Time Series title is not here but should be.");

            String dateEntry;
            while(!(dateEntry = sc.nextLine()).contains("}")) {

                //System.out.println("================");

                String dateStr = dateEntry.substring(dateEntry.indexOf('\"') + 1, dateEntry.lastIndexOf('\"'));

                //System.out.println("Date: " + dateStr);

                Date date = Date.valueOf(dateEntry.substring(dateEntry.indexOf('\"') + 1, dateEntry.lastIndexOf('\"')));

                String openPriceEntry = sc.nextLine();
                float openPrice = Float.parseFloat(openPriceEntry.substring(openPriceEntry.indexOf(':') + ": \"".length(), openPriceEntry.lastIndexOf("\"")));

                //System.out.println("Open Price: " + openPrice);

                String highPriceEntry = sc.nextLine();
                float highPrice = Float.parseFloat(highPriceEntry.substring(highPriceEntry.indexOf(':') + ": \"".length(), highPriceEntry.lastIndexOf("\"")));

                //System.out.println("High Price: " + highPrice);

                String lowPriceEntry = sc.nextLine();
                float lowPrice = Float.parseFloat(lowPriceEntry.substring(lowPriceEntry.indexOf(':') + ": \"".length(), lowPriceEntry.lastIndexOf("\"")));

                //System.out.println("Low Price: " + lowPrice);

                String closePriceEntry = sc.nextLine();
                float closePrice = Float.parseFloat(closePriceEntry.substring(closePriceEntry.indexOf(':') + ": \"".length(), closePriceEntry.lastIndexOf("\"")));

                //System.out.println("Close Price: " + closePrice);

                String volumeEntry = sc.nextLine();
                long volume = Long.parseLong(volumeEntry.substring(volumeEntry.indexOf(':') + ": \"".length(), volumeEntry.lastIndexOf("\"")));

                //System.out.println("Volume: " + volume);

                String endingBrace = sc.nextLine();
                if(!endingBrace.contains("}") && !endingBrace.contains("},")) throw new Exception("Found: " + endingBrace + ", instead of \"},\"");

                //System.out.println("================");

                DailyStockData dailyStockData = new DailyStockData();
                dailyStockData.setMetadataId(stockMetadata.getId());
                dailyStockData.setDateCreated(date);
                dailyStockData.setOpenPrice(openPrice);
                dailyStockData.setHighPrice(highPrice);
                dailyStockData.setLowPrice(lowPrice);
                dailyStockData.setClosePrice(closePrice);
                dailyStockData.setVolume(volume);

                if(!isRepeat) dailyStockDataRepository.save(dailyStockData);
                // of course this is only cause im not getting any new information (all historic) so im assuming any new data is repeated data for the same ticker company
            }

            String finalBrace = sc.nextLine();

            //System.out.println("================================");
        }

        return "success";
    }

    /**
     * getAlphavantageStockDailyData()
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    @GetMapping("/stock/daily")
    public String getAlphavantageStockDailyData() throws IOException, InterruptedException {

        //api.key=RD164SI5XMGA32WL
        RestTemplate restTemplate = new RestTemplate();

        File file = new File("./src/main/resources/stock-tickers.txt");
        Scanner sc = new Scanner(file);

        // TESTING
/*        int low = 1000;
        int high = 5000;*/

        // OFFICIAL

        int low = 5000;
        int high = 10000;

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("./src/main/resources/stock-data.txt"));

        while(sc.hasNext()) {
            int sleepTime = (int) (Math.random() * high + low);
            Thread.sleep(sleepTime);
            ResponseEntity<String> responseEntity = restTemplate.getForEntity("https://www.alphavantage.co/query?" +
                    "function=TIME_SERIES_DAILY" +
                    "&symbol=" + sc.next() +
                    "&outputsize=full" +
                    "&apikey=RD164SI5XMGA32WL", String.class);
            bufferedWriter.write(responseEntity.getBody() + ", \n");
        }

        return "";
    }
}
