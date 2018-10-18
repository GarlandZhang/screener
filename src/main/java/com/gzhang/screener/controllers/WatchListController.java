package com.gzhang.screener.controllers;

import com.gzhang.screener.models.AppUser;
import com.gzhang.screener.models.iomodels.AccessTokenResponse;
import com.gzhang.screener.models.iomodels.WatchListOutput;
import com.gzhang.screener.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WatchListController {

    @Autowired
    UserRepository userRepository;

    /**
     * returns watchlist for specified user
     * @param userId
     * @return
     */
    @GetMapping("/watch-list/user/{userId}")
    public ResponseEntity<WatchListOutput> getWatchList(@PathVariable int userId) {
        AppUser user = userRepository.getUserById(userId);
        WatchListOutput watchListOutput= new WatchListOutput(user.getWatchList());

        return ResponseEntity.status(HttpStatus.OK)
                .header("Message", "Watch list returned.")
                .body(watchListOutput);
    }
}
