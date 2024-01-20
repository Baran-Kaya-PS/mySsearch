package com.example.mysearch.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/api/home")
    public String home() {
        return "index";
    }
    @GetMapping("/api/recommendations")
    public String recommendations() {
        return "recommandation";
    }

    @GetMapping("/api/series")
    public String series() {
        return "serie";
    }
}