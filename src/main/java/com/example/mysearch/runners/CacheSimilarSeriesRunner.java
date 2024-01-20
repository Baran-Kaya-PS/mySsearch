package com.example.mysearch.runners;

import com.example.mysearch.service.SerieService;
import com.example.mysearch.repository.SerieRepository;
import com.example.mysearch.model.Series;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CacheSimilarSeriesRunner implements CommandLineRunner {
    private final SerieService serieService;
    private final SerieRepository serieRepository;

    public CacheSimilarSeriesRunner(SerieService serieService, SerieRepository serieRepository) {
        this.serieService = serieService;
        this.serieRepository = serieRepository;
    }

    @Override
    public void run(String... args) {
        List<Series> allSeries = serieRepository.findAll();
        for (Series series : allSeries) {
            serieService.cacheSimilarSeries(series.getId());
        }
    }
}