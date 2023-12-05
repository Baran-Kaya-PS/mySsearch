package com.example.mysearch.controller;

import com.example.mysearch.service.SerieService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/serie")
public class SerieControler {
    private final SerieService serieService;

    public SerieControler(SerieService serieService) {
        this.serieService = serieService;
    }
}
