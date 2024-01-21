package com.example.mysearch.service;

import com.example.mysearch.model.Series;
import com.example.mysearch.repository.SerieRepository;
import com.example.mysearch.utils.TFIDFCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service de recherche pour les séries.
 */
@Service
public class SearchService {
    private final SerieRepository serieRepository;
    private final TFIDFCalculator tfidfCalculator;

    /**
     * Constructeur du service de recherche.
     *
     * @param serieRepository   le repository des séries
     * @param tfidfCalculator   le calculateur TF-IDF
     */
    @Autowired
    public SearchService(SerieRepository serieRepository, TFIDFCalculator tfidfCalculator) {
        this.serieRepository = serieRepository;
        this.tfidfCalculator = tfidfCalculator;
    }

    /**
     * Recherche les séries par mot-clé.
     *
     * @param keyword   le mot-clé de recherche
     * @return          la liste des séries correspondantes, triées par score décroissant
     */
    public List<Series> searchSeriesByKeyword(String keyword) {
        List<Series> allSeries = serieRepository.findAll();
        return allSeries.stream()
                .filter(serie -> {
                    Map<String, Double> tfidfVectors = serie.getVecteursTFIDF();
                    return tfidfVectors != null && tfidfVectors.containsKey(keyword) && tfidfVectors.get(keyword) > 0;
                })
                .sorted((serie1, serie2) -> {
                    double score1 = serie1.getVecteursTFIDF().get(keyword);
                    double score2 = serie2.getVecteursTFIDF().get(keyword);
                    return Double.compare(score2, score1);
                })
                .collect(Collectors.toList());
    }

}
