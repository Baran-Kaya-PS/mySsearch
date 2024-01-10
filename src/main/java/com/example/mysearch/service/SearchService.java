package com.example.mysearch.service;

import com.example.mysearch.model.Serie;
import com.example.mysearch.repository.SerieRepository;
import com.example.mysearch.utils.TFIDFCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SearchService {
    private final SerieRepository serieRepository;
    private final TFIDFCalculator tfidfCalculator;

    @Autowired
    public SearchService(SerieRepository serieRepository, TFIDFCalculator tfidfCalculator) {
        this.serieRepository = serieRepository;
        this.tfidfCalculator = tfidfCalculator;
    }

    public List<Serie> searchSeriesByKeyword(String keyword) {
        List<Serie> allSeries = serieRepository.findAll();
        return allSeries.stream()
                .filter(serie -> {
                    Map<String, Double> tfidfVectors = serie.getTfidfVectors();
                    return tfidfVectors != null && tfidfVectors.containsKey(keyword) && tfidfVectors.get(keyword) > 0;
                })
                .sorted((serie1, serie2) -> {
                    double score1 = serie1.getTfidfVectors().get(keyword);
                    double score2 = serie2.getTfidfVectors().get(keyword);
                    return Double.compare(score2, score1);
                })
                .collect(Collectors.toList());
    }

}
