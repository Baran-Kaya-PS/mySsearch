package com.example.mysearch.controller;

import com.example.mysearch.model.Serie;
import com.example.mysearch.service.SerieService;
import com.example.mysearch.service.UserService;
import com.example.mysearch.utils.TFIDFCalculator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.List;

@Controller // Remplacer @RestController par @Controller car on retourne une vue HTML et non du JSON
@RequestMapping("/api/serie")
public class SerieControler {
    private final SerieService serieService;
    private final UserService userService;
    private final TFIDFCalculator tfidfCalculator;

    public SerieControler(SerieService serieService, UserService userService, TFIDFCalculator tfidfCalculator) {
        this.serieService = serieService;
        this.userService = userService;
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
    @GetMapping("/search")
    public String search(@RequestParam(required = false) String keyword, Model model, Principal principal) {
        if (keyword != null && !keyword.isEmpty()) {
            try {
                List<Serie> bestSeries = (List<Serie>) serieService.searchSeriesByKeyword(keyword);
                model.addAttribute("series", bestSeries);
                String username = principal.getName();
                userService.addSearchToHistory(principal.getName(),keyword);
            } catch (Exception e) {
                model.addAttribute("error", "Erreur lors de la recherche : " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            model.addAttribute("error", "Veuillez entrer un mot-clé pour la recherche.");
        }
        return "index";
    }

}