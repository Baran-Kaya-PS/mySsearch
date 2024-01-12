package com.example.mysearch.service;
import com.example.mysearch.controller.SerieControler;
import com.example.mysearch.model.Serie;
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

    public SerieService(SerieRepository serieRepository) {
        this.serieRepository = serieRepository;
    }

    public Iterable<Serie> getAllSeries() {
        return serieRepository.findAll();
    }

    public Serie getSerieById(String serieId) {
        return serieRepository.findById(serieId).orElse(null);
    }

    public Serie addSerie(Serie serie) {
        return serieRepository.save(serie);
    }

    public void deleteSerie(String serieId) {
        serieRepository.deleteById(serieId);
    }
    public Iterable<Serie> searchSeriesByKeyword(String keyword) {
        String[] keywords = keyword.split("\\s+"); // Divisez la chaîne en mots clés
        List<Serie> allSeries = serieRepository.findAll();

        // Calcul des scores TFIDF pour chaque série basé sur tous les mots clés
        return allSeries.stream()
                .map(serie -> new AbstractMap.SimpleEntry<>(
                        serie,
                        Arrays.stream(keywords)
                                .mapToDouble(key -> serie.getTfidfVectors().getOrDefault(key.toLowerCase(), 0.0))
                                .sum()
                ))
                .filter(entry -> entry.getValue() > 0) // Filtrez les séries avec un score > 0
                .sorted(Map.Entry.<Serie, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    public Iterable<Serie> searchSeriesByName(String name) {
        // si le nom est dans la base de données on retourne la série
        if (serieRepository.existsByTitre(name)) {
            return serieRepository.findByTitre(name);
        }
        return serieRepository.findByTitre(name);
    }
}
