package com.example.mysearch.runners;

import com.example.mysearch.model.Series;
import com.example.mysearch.model.User;
import com.example.mysearch.service.HistoryService;
import com.example.mysearch.service.SerieService;
import com.example.mysearch.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
@Order(2)
public class DataGeneratorRunner implements CommandLineRunner {

    private final UserService userService;
    private final SerieService serieService;
    private final HistoryService historyService;

    public DataGeneratorRunner(UserService userService, SerieService serieService, HistoryService historyService) {
        this.userService = userService;
        this.serieService = serieService;
        this.historyService = historyService;
    }

    @Override
    public void run(String... args) throws Exception {
        /*
        Random random = new Random();

        // Liste des mots-clés possibles
        List<String> keywords = Arrays.asList("action", "drama", "comedy", "thriller", "sci-fi", "romance", "horror", "adventure", "mystery", "fantasy");

        // Générer 1000 utilisateurs
        for (int i = 0; i < 1000; i++) {
            User user = new User();
            user.setName("user" + i);
            user.setEmail("user" + i + "@example.com");
            user.setPassword("password");
            userService.createUser(user);

            // Pour chaque utilisateur, générer des vues, des likes et des recherches aléatoires pour les séries
            List<Series> allSeries = (List<Series>) serieService.getAllSeries();
            for (Series series : allSeries) {
                if (random.nextDouble() < 0.9) { // Augmentez cette valeur pour générer plus de likes
                    historyService.addSerieLike(user.getId(), series.getTitre());
                    serieService.incrementLikeCount(series.getId());
                }
                if (random.nextBoolean()) {
                    historyService.addSerieClick(user.getId(), series.getId());
                    serieService.incrementViewCount(series.getId());
                }
                if (random.nextDouble() < 0.1) { // Ajoutez des dislikes
                    historyService.addSerieDislike(user.getId(), series.getTitre());
                    serieService.incrementDislikeCount(series.getId());
                }
                // Ajouter un mot-clé de recherche aléatoire à l'historique de l'utilisateur
                String randomKeyword = keywords.get(random.nextInt(keywords.size()));
                historyService.addSearchToHistory(user.getId(), randomKeyword);

                // ajouter des prints des data
                System.out.println("Utilisateur généré : " + user.getName());
                System.out.println("Historique généré : " + historyService.getHistoryByUserId(user.getId()));
            }
        }

         */
    }
}