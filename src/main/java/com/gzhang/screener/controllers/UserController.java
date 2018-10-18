package com.gzhang.screener.controllers;

import com.gzhang.screener.models.AppUser;
import com.gzhang.screener.models.ScreenIndicatorGrouping;
import com.gzhang.screener.models.iomodels.GroupingsOutput;
import com.gzhang.screener.models.metamodels.ListOfScreenIndicatorGroupings;
import com.gzhang.screener.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    UserRepository userRepository;

    /**
     * url endpoint for creating new user
     * @param user
     * @return
     */
    @PostMapping("/user")
    public ResponseEntity<AppUser> createUser(@RequestBody AppUser user) {
        AppUser newUser = new AppUser();
        return ResponseEntity.status(HttpStatus.OK)
                .header("Message", "User created")
                .body(userRepository.save(newUser));
    }

    /**
     * gets groupings for a user
     * @param userId
     * @return
     */
    @GetMapping("/user/{userId}/groupings")
    public ResponseEntity<GroupingsOutput> getGroupings (@PathVariable int userId) {
        AppUser user = userRepository.getUserById(userId);

        if(user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("Message", "User not found")
                    .body(null);
        }

        // retrieve list of groupings; if null, then return an empty list
        List<ScreenIndicatorGrouping> groupings = user.getScreenIndicatorGroupingList();
        if(groupings == null) {
            return ResponseEntity.status(HttpStatus.OK)
                    .header("Message", "Empty")
                    .body(new GroupingsOutput());
        }

        return ResponseEntity.status(HttpStatus.OK)
                .header("Message", "Retrieved groupings")
                .body(new GroupingsOutput(groupings));
    }

}
