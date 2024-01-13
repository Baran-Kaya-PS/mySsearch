package com.example.mysearch.service;

import com.example.mysearch.model.Series;
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

    public List<Series> searchSeriesByKeyword(String keyword) {
        List<Series> allSeries = serieRepository.findAll();
        return allSeries.stream()
                .filter(serie -> {
                    Map<String, Double> tfidfVectors = serie.getvecteursTFIDF();
                    return tfidfVectors != null && tfidfVectors.containsKey(keyword) && tfidfVectors.get(keyword) > 0;
                })
                .sorted((serie1, serie2) -> {
                    double score1 = serie1.getvecteursTFIDF().get(keyword);
                    double score2 = serie2.getvecteursTFIDF().get(keyword);
                    return Double.compare(score2, score1);
                })
                .collect(Collectors.toList());
    }

}
