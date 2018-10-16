package com.gzhang.screener.controllers;

import com.gzhang.screener.models.AppUser;
import com.gzhang.screener.models.StockTwitsUser;
import com.gzhang.screener.models.WatchList;
import com.gzhang.screener.models.WatchedTicker;
import com.gzhang.screener.models.iomodels.*;
import com.gzhang.screener.models.metamodels.StockTwitsWatchListResponse;
import com.gzhang.screener.repositories.StockTwitsUserRepository;
import com.gzhang.screener.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@RestController
public class StockTwitsController {

    @Autowired
    StockTwitsUserRepository stockTwitsUserRepository;

    @Autowired
    UserRepository userRepository;

    final String CLIENT_ID="d1b3de24ed05b9c5";
    final String APP_URL="http://localtest.me:8080/stock-twits/oauth/response/user/";
    final String CLIENT_SECRET="ea9d2a804c7607d10a3e1fbfdec8d717aaf1f3b3";

    @GetMapping("/stock-twits/oauth/url/user/{userId}")
    public String getOAuthUrl(int userId) {
        return "https://api.stocktwits.com/api/2/oauth/authorize?" +
                "client_id=" + CLIENT_ID +
                "&response_type=code" +
                "&redirect_uri=" + APP_URL + userId +
                "&scope=read,watch_lists,publish_messages,publish_watch_lists,direct_messages,follow_users,follow_stocks";
    }

    @GetMapping("/stock-twits/oauth/response/user/{userId}")
    public ResponseEntity<AccessTokenResponse> receiveCodeAndFetchAccessToken(@PathVariable int userId, @RequestParam String code) {

        RequestForAccessTokenResponse accessTokenResponse = getAccessToken(userId, code);

        // save token in StockTwitsUser instance
        StockTwitsUser stockTwitsUser = new StockTwitsUser(accessTokenResponse.getUser_id(),
                                                           accessTokenResponse.getAccess_token(),
                                                           accessTokenResponse.getScope(),
                                                           accessTokenResponse.getUsername(),
                                                           userId);
        // save token, and other useful information
        stockTwitsUserRepository.save(stockTwitsUser);


        return ResponseEntity.status(HttpStatus.OK)
                .header("Message", "Access token retrieved.")
                .body(new AccessTokenResponse(accessTokenResponse.getAccess_token()));
    }

    @GetMapping("/stock-twits/watchlist/user/{userId}")
    public ResponseEntity<WatchListOutput> importWatchList(@PathVariable int userId) {
        // get access token
        AppUser appUser = userRepository.getUserById(userId);
        String accessToken = appUser.getStockTwitsUser().getAccessToken();

        // only handling 1 watchlist for now
        // check if details url is working
        String watchListDetailsUrl = getWatchListDetailsUrl(userId, accessToken);
        if(watchListDetailsUrl.equals("")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("Message", "No watchlist extracted.")
                    .body(new WatchListOutput(appUser.getWatchList()));
        }

        // extract list of tickers, store in user and save
        if(appUser.getWatchList() == null) appUser.setWatchList(new WatchList());
        appUser.getWatchList().addTickers(getTickersFromWatchlist(watchListDetailsUrl));
        userRepository.save(appUser);

        return ResponseEntity.status(HttpStatus.OK)
                .header("Message", "Watchlist retrieved")
                .body(new WatchListOutput(appUser.getWatchList()));
    }

    private List<WatchedTicker> getTickersFromWatchlist(String watchListDetailsUrl) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<StockTwitsWatchListShowResponse> responseEntity
                = restTemplate.getForEntity(watchListDetailsUrl, StockTwitsWatchListShowResponse.class);

        return responseEntity.getBody().standardizeWatchList();
    }

    private String getWatchListDetailsUrl(int userId, String accessToken) {
        String watchListUrl = getWatchListUrl(userId, accessToken);
        int watchListId = getWatchListId(watchListUrl);
        if(watchListId == -1) return "";

        return "https://api.stocktwits.com/api/2/watchlists/show/"
                + watchListId
                + ".json?"
                + "access_token="
                + accessToken;
    }

    private int getWatchListId(String watchListUrl) {
        // make GET request
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<StockTwitsWatchListsResponse> responseEntity =
                restTemplate.getForEntity(watchListUrl, StockTwitsWatchListsResponse.class);

        if(responseEntity.getBody().getWatchlists().size() == 0) return -1;
        return responseEntity.getBody().getWatchlists().get(0).getId();
    }

    private String getWatchListUrl(int userId, String accessToken) {
        return "https://api.stocktwits.com/api/2/watchlists.json?" +
                "access_token=" + accessToken;
    }

    private String getAccessTokenUrl(int userId, String code) {
        return "https://api.stocktwits.com/api/2/oauth/token?" +
                "client_id=" + CLIENT_ID +
                "&client_secret=" + CLIENT_SECRET +
                "&code=" + code +
                "&grant_type=authorization_code" +
                "&redirect_uri=" + APP_URL + userId;
    }

    private RequestForAccessTokenResponse getAccessToken(int userId, String code) {
        String tokenUrl = getAccessTokenUrl(userId, code);

        // make GET request for access token
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<RequestForAccessTokenResponse> responseEntity =
                restTemplate.postForEntity(tokenUrl,
                null,
                RequestForAccessTokenResponse.class);
        return responseEntity.getBody();
    }


}
