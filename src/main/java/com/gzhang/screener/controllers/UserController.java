package com.gzhang.screener.controllers;

import com.gzhang.screener.models.AppUser;
import com.gzhang.screener.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public class UserController {

    @Autowired
    UserRepository userRepository;

    @PostMapping("/user")
    public ResponseEntity<AppUser> createUser(@RequestBody AppUser user) {
        return ResponseEntity.status(HttpStatus.OK)
                .header("Message", "User created")
                .body(userRepository.save(user));
    }

}
