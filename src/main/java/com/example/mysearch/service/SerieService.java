package com.example.mysearch.service;
import com.example.mysearch.model.History;
import com.example.mysearch.model.Series;
import com.example.mysearch.model.SeriesSimilarity;
import com.example.mysearch.model.User;
import com.example.mysearch.repository.SerieRepository;
import com.example.mysearch.repository.SeriesSimilarityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
@Service
public class SerieService {
    private final SerieRepository serieRepository;
    private final SeriesSimilarityRepository seriesSimilarityRepository;
    private final HistoryService historyService;
    private final UserService userService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    @Autowired
    public SerieService(SerieRepository serieRepository, SeriesSimilarityRepository seriesSimilarityRepository, HistoryService historyService, UserService userService) {
        this.serieRepository = serieRepository;
        this.seriesSimilarityRepository = seriesSimilarityRepository;
        this.historyService = historyService;
        this.userService = userService;
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
        return allSeries.stream() // Pour chaque série
                .map(serie -> new AbstractMap.SimpleEntry<>( // série deviens une entrée de la map
                        serie, // clé = série
                        Arrays.stream(keywords) // flux de mots clés
                                .mapToDouble(key -> serie.getVecteursTFIDF().getOrDefault(key.toLowerCase(), 0.0)) // pour chaque mot clé, récupérez le score TFIDF de la série
                                .sum() // somme des scores TFIDF
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
    public Series getSerieByTitle(String serieName) {
        return serieRepository.findByTitre(serieName).get(0);
    }
    public List<Series> getRecommendedSeries(String userId) {
        // Récupérer l'historique de l'utilisateur
        History userHistory = historyService.getHistoryByUserId(userId);
        if (userHistory == null) {
            return Collections.emptyList();
        }

        // Récupérer les séries que l'utilisateur a aimées
        List<String> userLikes = userHistory.getSerieLike();

        // Récupérer tous les utilisateurs
        List<User> allUsers = (List<User>) userService.getAllUsers();

        // Créer une liste pour stocker les séries recommandées
        List<Series> recommendedSeries = new ArrayList<>();

        // Parcourir tous les utilisateurs
        for (User user : allUsers) {
            // Ne pas comparer l'utilisateur avec lui-même
            if (!user.getId().equals(userId)) {
                // Récupérer l'historique de l'autre utilisateur
                History otherUserHistory = historyService.getHistoryByUserId(user.getId());
                if (otherUserHistory == null) {
                    continue;
                }

                // Récupérer les séries que l'autre utilisateur a aimées
                List<String> otherUserLikes = otherUserHistory.getSerieLike();

                // Parcourir les séries que l'autre utilisateur a aimées
                for (String serieName : otherUserLikes) {
                    // Si l'utilisateur n'a pas déjà aimé cette série et qu'il ne l'a pas déjà dislikée
                    if (!userLikes.contains(serieName) && !userHistory.getSerieDislike().contains(serieName)) {
                        // Ajouter la série à la liste des séries recommandées
                        Series serie = getSerieByTitle(serieName);
                        if (serie != null && !recommendedSeries.contains(serie)) {
                            recommendedSeries.add(serie);
                        }
                    }
                }
            }
        }

        // Trier les séries recommandées en fonction de leur score de recommandation
        recommendedSeries.sort((s1, s2) -> Double.compare(getRecommendationScore(s2), getRecommendationScore(s1)));

        // Retourner les séries recommandées
        return recommendedSeries;
    }

    private double getRecommendationScore(Series series) {
        // Calculer le score de recommandation en fonction du nombre de "likes" et de "dislikes"
        double likeScore = series.getLikes();
        double dislikeScore = series.getDislikes();

        // Vous pouvez ajuster ces poids en fonction de l'importance que vous accordez aux "likes" et aux "dislikes"
        double likeWeight = 1.0;
        double dislikeWeight = -0.5;

        return likeScore * likeWeight + dislikeScore * dislikeWeight;
    }

    public void incrementLikeCount(String serieId) {
        Series serie = getSerieById(serieId);
        if (serie != null) {
            serie.setLikes(serie.getLikes() + 1);
            serieRepository.save(serie);
        }
    }

    public void incrementDislikeCount(String serieId) {
        Series serie = getSerieById(serieId);
        if (serie != null) {
            serie.setDislikes(serie.getDislikes() + 1);
            serieRepository.save(serie);
        }
    }
    public void incrementViewCount(String serieId) {
        Series serie = getSerieById(serieId);
        if (serie != null) {
            serie.setViews(serie.getViews() + 1);
            serieRepository.save(serie);
        }
    }
    public double cosineSimilarity(Map<String, Double> vectorA, Map<String, Double> vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (String key : vectorA.keySet()) {
            dotProduct += vectorA.get(key) * vectorB.getOrDefault(key, 0.0);
            normA += Math.pow(vectorA.get(key), 2);
        }
        for (double value : vectorB.values()) {
            normB += Math.pow(value, 2);
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
    public void calculateSeriesSimilarities() {
        if (seriesSimilarityRepository.count() > 0) {
            return;
        }
        List<Series> seriesList = serieRepository.findAll();
        Map<Pair<Series, Series>, Double> similarities = new ConcurrentHashMap<>();

        for (int i = 0; i < seriesList.size(); i++) {
            for (int j = i + 1; j < seriesList.size(); j++) {
                int finalI = i;
                int finalJ = j;
                executorService.submit(() -> {
                    double similarity = cosineSimilarity(seriesList.get(finalI).getVecteursTFIDF(), seriesList.get(finalJ).getVecteursTFIDF());
                    // Stockez cette valeur de similarité pour une utilisation ultérieure
                    similarities.put(new Pair<>(seriesList.get(finalI), seriesList.get(finalJ)), similarity);
                    // Stockez cette valeur de similarité dans la base de données
                    saveSimilarityToDatabase(seriesList.get(finalI), seriesList.get(finalJ), similarity);
                });
            }
        }
    }

    public void saveSimilarityToDatabase(Series series1, Series series2, double similarity) {
        SeriesSimilarity seriesSimilarity = new SeriesSimilarity();
        seriesSimilarity.setSeries1Id(series1.getId());
        seriesSimilarity.setSeries2Id(series2.getId());
        seriesSimilarity.setSimilarity(similarity);
        seriesSimilarityRepository.save(seriesSimilarity);
    }

    public class Pair<F, S> { // paire pour stocker les séries et leur similarité
        private F first;
        private S second;

        public Pair(F first, S second) {
            this.first = first;
            this.second = second;
        }

        public F getFirst() {
            return first;
        }

        public S getSecond() {
            return second;
        }
    }
}
