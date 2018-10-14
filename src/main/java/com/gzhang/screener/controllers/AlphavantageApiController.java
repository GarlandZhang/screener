package com.gzhang.screener.controllers;

import com.gzhang.screener.iomodels.metamodels.AlphavantageObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.Scanner;

@Controller
public class AlphavantageApiController {

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
