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
import java.util.stream.Collectors;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

/**
 * Contrôleur pour les séries.
 */
@Controller // Remplacer @RestController par @Controller car on retourne une vue HTML et non du JSON
@RequestMapping("/api/serie")
public class SerieControler {
    private final SerieService serieService;
    private final UserService userService;
    private final TFIDFCalculator tfidfCalculator;
    private final HistoryService historyService;
    private static final Logger logger = LoggerFactory.getLogger(SerieControler.class);

    /**
     * Constructeur du contrôleur des séries.
     * @param serieService Le service des séries.
     * @param userService Le service des utilisateurs.
     * @param tfidfCalculator Le calculateur TF-IDF.
     * @param historyService Le service de l'historique.
     */
    public SerieControler(SerieService serieService, UserService userService, TFIDFCalculator tfidfCalculator, HistoryService historyService) {
        this.serieService = serieService;
        this.userService = userService;
        this.tfidfCalculator = tfidfCalculator;
        this.historyService = historyService;
    }

    /**
     * Récupère toutes les séries.
     * @return Les séries.
     */
    public ResponseEntity<Iterable<Series>> getAllSeries() {
        Iterable<Series> series = serieService.getAllSeries();
        return ResponseEntity.ok(series);
    }

    /**
     * Récupère une série par son identifiant.
     * @param serieId L'identifiant de la série.
     * @return La série correspondante.
     */
    public ResponseEntity<Series> getSerieById(String serieId) {
        Series serie = serieService.getSerieById(serieId);
        return serie != null ? ResponseEntity.ok(serie): ResponseEntity.notFound().build();
    }

    /**
     * Ajoute une série.
     * @param serie La série à ajouter.
     * @return La série ajoutée.
     */
    public ResponseEntity<Series> addSerie(Series serie) {
        Series savedSerie = serieService.addSerie(serie);
        return ResponseEntity.ok(savedSerie);
    }

    /**
     * Supprime une série.
     * @param serieId L'identifiant de la série à supprimer.
     * @return La réponse de suppression.
     */
    public ResponseEntity<Series> deleteSerie(String serieId) {
        serieService.deleteSerie(serieId);
        return ResponseEntity.ok().build();
    }

    /**
     * Recherche des séries.
     * @param keyword Le mot-clé de recherche.
     * @param model Le modèle de la vue.
     * @param principal Le principal.
     * @return La vue index.
     */
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
    /**
     * Gère le clic sur une série.
     * @param serieId L'identifiant de la série.
     * @param principal Le principal.
     * @param model Le modèle de la vue.
     * @return La vue de la série.
     */
@GetMapping("/click")
    public String handleSerieClick(@RequestParam String serieId, Principal principal,Model model) {
        String username = principal.getName();
        User user = userService.getUserByUsername(username);
        if (user != null) {
            historyService.addSerieClick(user.getId(), serieId);
            serieService.incrementViewCount(serieId);
        }
        Series serie = serieService.getSerieById(serieId);
        List<Series> similarSeries = serieService.getSimilarSeries(serieId).stream()
                .limit(8)
                .collect(Collectors.toList());
        model.addAttribute("serie", serie);
        model.addAttribute("similarSeries", similarSeries);
        return "serie"; // ou toute autre page appropriée
    }
    /**
     * Gère le dislike d'une série.
     * @param serieName Le nom de la série.
     * @param principal Le principal.
     * @param model Le modèle de la vue.
     * @return La vue de la série.
     */
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
    /**
     * Gère le like d'une série.
     * @param serieName Le nom de la série.^
     * @param principal Le principal.
     * @param model Le modèle de la vue.
     * @return La vue de la série.
     */
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
    /**
     * Gère la recommandation de séries.
     * @param principal Le principal.
     * @param model Le modèle de la vue.
     * @return La vue de la recommandation.
     */
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
    /**
     * retourne la liste des séries.
     * @param model Le modèle de la vue.
     * @return La vue des séries.
     */
    @GetMapping("/all")
    public String getAllSeries(Model model) {
        List<Series> series = StreamSupport.stream(serieService.getAllSeries().spliterator(), false)
                .sorted(Comparator.comparingInt(s -> ((Series) s).getLikes() + ((Series) s).getViews()/10 - ((Series) s).getDislikes()).reversed())
                .collect(Collectors.toList());
        model.addAttribute("series", series);
        return "series";
    }

}