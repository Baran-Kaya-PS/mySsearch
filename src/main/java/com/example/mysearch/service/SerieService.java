package com.example.mysearch.service;
import com.example.mysearch.model.History;
import com.example.mysearch.model.Series;
import com.example.mysearch.repository.SerieRepository;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SerieService {
    private final SerieRepository serieRepository;
    private final HistoryService historyService;

    public SerieService(SerieRepository serieRepository) {
        this.serieRepository = serieRepository;
    }

    public Iterable<Series> getAllSeries() {
        return serieRepository.findAll();
    }

    public Series getSerieById(String serieId) {
        return serieRepository.findById(serieId).orElse(null);
    }

    public Series addSerie(Series serie) {
        return serieRepository.save(serie);
    }

    public void deleteSerie(String serieId) {
        serieRepository.deleteById(serieId);
    }
    public Iterable<Series> searchSeriesByKeyword(String keyword) {
        String[] keywords = keyword.split("\\s+"); // Divisez la chaîne en mots clés
        List<Series> allSeries = serieRepository.findAll();

        // Calcul des scores TFIDF pour chaque série basé sur tous les mots clés
        return allSeries.stream()
                .map(serie -> new AbstractMap.SimpleEntry<>(
                        serie,
                        Arrays.stream(keywords)
                                .mapToDouble(key -> serie.getVecteursTFIDF().getOrDefault(key.toLowerCase(), 0.0))
                                .sum()
                ))
                .filter(entry -> entry.getValue() > 0) // Filtrez les séries avec un score > 0
                .sorted(Map.Entry.<Series, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    public Iterable<Series> searchSeriesByName(String name) {
        // si le nom est dans la base de données on retourne la série
        if (serieRepository.existsByTitre(name)) {
            return serieRepository.findByTitre(name);
        }
        return serieRepository.findByTitre(name);
    }

    public List<Series> getRecommendedSeries(String userId) {
        History history = historyService.getHistoryByUserId(userId);
    }
}
