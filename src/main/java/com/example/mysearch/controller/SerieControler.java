package com.example.mysearch.controller;

import com.example.mysearch.model.Serie;
import com.example.mysearch.service.SerieService;
import com.example.mysearch.utils.TFIDFCalculator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ui.Model;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/serie")
public class SerieControler {
    private final SerieService serieService;
    private final TFIDFCalculator tfidfCalculator;

    public SerieControler(SerieService serieService, TFIDFCalculator tfidfCalculator) {
        this.serieService = serieService;
        this.tfidfCalculator = tfidfCalculator;
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
    @GetMapping("/recherche")
    public String recherche(@RequestParam(required = false) String keyword, Model model) {
        if (keyword != null && !keyword.isEmpty()) {
            try {
                // Utilisation du service pour obtenir les meilleures séries
                List<Serie> bestSeries = (List<Serie>) serieService.searchSeriesByKeyword(keyword);
                model.addAttribute("series", bestSeries);
            } catch (Exception e) {
                model.addAttribute("error", "Erreur lors de la recherche : " + e.getMessage());
                // Logger l'erreur pour le débogage
                e.printStackTrace();
            }
        } else {
            // Gestion du cas où aucun mot-clé n'est fourni
            model.addAttribute("error", "Veuillez entrer un mot-clé pour la recherche.");
        }
        return "home";
    }

}