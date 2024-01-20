package com.example.mysearch.controller;

import com.example.mysearch.model.Series;
import com.example.mysearch.model.User;
import com.example.mysearch.service.HistoryService;
import com.example.mysearch.service.SerieService;
import com.example.mysearch.service.UserService;
import com.example.mysearch.utils.TFIDFCalculator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller // Remplacer @RestController par @Controller car on retourne une vue HTML et non du JSON
@RequestMapping("/api/serie")
public class SerieControler {
    private final SerieService serieService;
    private final UserService userService;
    private final TFIDFCalculator tfidfCalculator;
    private final HistoryService historyService;
    private static final Logger logger = LoggerFactory.getLogger(SerieControler.class);

    public SerieControler(SerieService serieService, UserService userService, TFIDFCalculator tfidfCalculator, HistoryService historyService) {
        this.serieService = serieService;
        this.userService = userService;
        this.tfidfCalculator = tfidfCalculator;
        this.historyService = historyService;
    }
    public ResponseEntity<Iterable<Series>> getAllSeries() {
        Iterable<Series> series = serieService.getAllSeries();
        return ResponseEntity.ok(series);
    }
    public ResponseEntity<Series> getSerieById(String serieId) {
        Series serie = serieService.getSerieById(serieId);
        return serie != null ? ResponseEntity.ok(serie): ResponseEntity.notFound().build();
    }
    public ResponseEntity<Series> addSerie(Series serie) {
        Series savedSerie = serieService.addSerie(serie);
        return ResponseEntity.ok(savedSerie);
    }
    public ResponseEntity<Series> deleteSerie(String serieId) {
        serieService.deleteSerie(serieId);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/search")
    public String search(@RequestParam(required = false) String keyword, Model model, Principal principal) {
        if (keyword != null && !keyword.isEmpty()) {
            try {
                List<Series> bestSeries = (List<Series>) serieService.searchSeriesByKeyword(keyword);
                model.addAttribute("series", bestSeries);

                String username = principal.getName();
                User user = userService.getUserByUsername(username);
                if (user != null) {
                    String userId = user.getId();
                    historyService.addSearchToHistory(userId, keyword);
                }

            } catch (Exception e) {
                model.addAttribute("error", "Erreur lors de la recherche : " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            model.addAttribute("error", "Veuillez entrer un mot-clé pour la recherche.");
        }
        return "index";
    }
    @GetMapping("/click")
    public String handleSerieClick(@RequestParam String serieId, Principal principal,Model model) {
        String username = principal.getName();
        User user = userService.getUserByUsername(username);
        if (user != null) {
            historyService.addSerieClick(user.getId(), serieId);
            serieService.incrementViewCount(serieId);
        }
        Series serie = serieService.getSerieById(serieId);
        model.addAttribute("serie", serie);
        return "serie"; // ou toute autre page appropriée
    }
    @GetMapping("/dislike")
    public String handleDislike(@RequestParam String serieName, Principal principal, Model model) {
        String username = principal.getName();
        User user = userService.getUserByUsername(username);
        if (user != null) {
            String userId = user.getId();
            historyService.addSerieDislike(userId, serieName);
            String serieId = serieService.getSerieByTitle(serieName).getId();
            serieService.incrementDislikeCount(serieService.getSerieByTitle(serieName).getId());
        }
        Series serie = serieService.getSerieByTitle(serieName);
        model.addAttribute("serie", serie);
        return "serie";
    }

    @GetMapping("/like")
    public String handleLike(@RequestParam String serieName, Principal principal, Model model) {
        String username = principal.getName();
        User user = userService.getUserByUsername(username);
        if (user != null) {
            String userId = user.getId();
            historyService.addSerieLike(userId, serieName);

            serieService.incrementLikeCount(serieService.getSerieByTitle(serieName).getId());
        }
        Series serie = serieService.getSerieByTitle(serieName);
        model.addAttribute("serie", serie);
        return "serie";
    }
    @GetMapping("/recommendations")
    public String getRecommendations(Principal principal, Model model) {
        String username = principal.getName();
        User user = userService.getUserByUsername(username);
        if (user != null) {
            List<Map<Series, String>> recommendedSeries = serieService.recommendSeries(user.getId());
            model.addAttribute("series", recommendedSeries);
        }
        return "recommandation";
    }

}