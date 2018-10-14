package com.gzhang.screener.controllers;

import com.gzhang.screener.iomodels.metamodels.AlphavantageObject;
import com.sun.xml.internal.ws.api.message.ExceptionHasMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.sql.Date;
import java.util.Scanner;

@Controller
public class AlphavantageApiController {

    @GetMapping("/parse")
    public String parseStockData() throws Exception {
        Scanner sc = new Scanner(new File("./src/main/resources/stock-data.txt"));

        int index = 0;

        while(sc.hasNextLine() && index < 3) {
            ++index;
            String openBrace = sc.nextLine();
            if(!openBrace.equals("{")) throw new Exception("Open brace is not here but it should be.");

            String metaDataTitle = sc.nextLine();
            if(!metaDataTitle.equals("\"Meta Data\": {")) throw new Exception("Meta data title is not here but it should be.");

            String infoPair = sc.nextLine(); // not needed

            /* **** WHAT I WANT **** */
            String symbolPair = sc.nextLine();
            //additional string parsing for symbol
            String symbol = symbolPair.substring(symbolPair.indexOf(':') + ": \"".length(), symbolPair.lastIndexOf("\""));

            /* **** WHAT I WANT **** */



            //clutter not needed
            String lastRefreshedPair = sc.nextLine();
            String outputSizePair = sc.nextLine();
            String timeZonePair = sc.nextLine();

            String closeMetaDataTitle = sc.nextLine();
            if(!closeMetaDataTitle.equals("},") && !closeMetaDataTitle.equals("}")) throw new Exception("Meta data close brace is not here but it should be.");

            String timerSeriesTitle = sc.nextLine();
            if(!timerSeriesTitle.equals("\"Time Series (Daily)\": {")) throw new Exception("Time Series title is not here but should be.");

            String dateEntry;
            while(!(dateEntry = sc.nextLine()).equals("}")) {
                Date date = Date.valueOf(dateEntry.substring(dateEntry.indexOf('\"'), dateEntry.lastIndexOf('\"')));

                String openPriceEntry = sc.nextLine();
                double openPrice = Double.parseDouble(openPriceEntry.substring(openPriceEntry.indexOf(':') + ": \"".length(), openPriceEntry.lastIndexOf("\"")));

                String highPriceEntry = sc.nextLine();
                double highPrice = Double.parseDouble(highPriceEntry.substring(highPriceEntry.indexOf(':') + ": \"".length(), highPriceEntry.lastIndexOf("\"")));

                String lowPriceEntry = sc.nextLine();
                double lowPrice = Double.parseDouble(lowPriceEntry.substring(lowPriceEntry.indexOf(':') + ": \"".length(), lowPriceEntry.lastIndexOf("\"")));

                String closePriceEntry = sc.nextLine();
                double closePrice = Double.parseDouble(closePriceEntry.substring(closePriceEntry.indexOf(':') + ": \"".length(), closePriceEntry.lastIndexOf("\"")));

                String volumeEntry = sc.nextLine();
                long volume = Long.parseLong(volumeEntry.substring(volumeEntry.indexOf(':') + ": \"".length(), volumeEntry.lastIndexOf("\"")));

                String endingBrace = sc.nextLine();
                if(!endingBrace.equals("}") && !endingBrace.equals("},")) throw new Exception("Found: " + endingBrace + ", instead of \"},\"");
            }

            String finalBrace = sc.nextLine();
        }

        return "success";
    }

    @GetMapping("/test")
    public String getQuandlData() throws IOException, InterruptedException {

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
