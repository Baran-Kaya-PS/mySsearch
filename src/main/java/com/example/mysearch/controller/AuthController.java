package com.example.mysearch.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/connexion")
    public String getLoginPage() {
        return "login"; // Cela renvoie à 'login.html' situé dans 'src/main/resources/templates' si vous utilisez Thymeleaf
    }
}
