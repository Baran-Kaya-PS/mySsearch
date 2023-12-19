package com.example.mysearch.controller;

import com.example.mysearch.model.Serie;
import com.example.mysearch.service.SerieService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
/*
GetBestSeries via un mot clé avec la tf-idf.
 */
@RestController
@RequestMapping("/api/serie")
public class SerieControler {
    private final SerieService serieService;

    public SerieControler(SerieService serieService) {
        this.serieService = serieService;
    }
    public ResponseEntity<Iterable<Serie>> getAllSeries() {
        Iterable<Serie> series = serieService.getAllSeries();
        return ResponseEntity.ok(series);
    }
    public ResponseEntity<Serie> getSerieById(String serieId) {
        Serie serie = serieService.getSerieById(serieId);
        return serie != null ? ResponseEntity.ok(serie): ResponseEntity.notFound().build();
    }
    public ResponseEntity<Serie> addSerie(Serie serie) {
        Serie savedSerie = serieService.addSerie(serie);
        return ResponseEntity.ok(savedSerie);
    }
    public ResponseEntity<Serie> deleteSerie(String serieId) {
        serieService.deleteSerie(serieId);
        return ResponseEntity.ok().build();
    }
    public ResponseEntity<Iterable<Serie>> getBestSeries(@RequestParam String keyword) {
        try {
            Iterable<Serie> bestSeries = serieService.searchSeriesByKeyword(keyword);
            return new ResponseEntity<>(bestSeries, HttpStatus.OK);
        } catch (Exception e) {
            // Gérer l'exception si nécessaire et retourner une réponse appropriée
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
