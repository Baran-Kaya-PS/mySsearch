package com.example.mysearch.service;
import com.example.mysearch.controller.SerieControler;
import com.example.mysearch.model.Serie;
import com.example.mysearch.repository.SerieRepository;
import org.springframework.stereotype.Service;

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
        return serieRepository.findAll().stream()
                .filter(serie -> serie.getVecteursTFIDF().containsKey(keyword))
                .sorted((s1,s2) -> s2.getVecteursTFIDF().get(keyword).compareTo(s1.getVecteursTFIDF().get(keyword)))
                .collect(Collectors.toList());
    }
}
