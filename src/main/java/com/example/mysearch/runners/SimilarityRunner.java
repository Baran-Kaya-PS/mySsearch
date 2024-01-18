package com.example.mysearch.runners;

import com.example.mysearch.service.SerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SimilarityRunner implements CommandLineRunner {

    private final SerieService serieService;

    @Autowired
    public SimilarityRunner(SerieService serieService) {
        this.serieService = serieService;
    }

    @Override
    public void run(String... args) throws Exception {
        serieService.calculateSeriesSimilarities();
    }
}