package com.example.mysearch.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.mysearch.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserControler {
    private final UserService userService;

    public UserControler(UserService userService) {
        this.userService = userService;
    }
}
