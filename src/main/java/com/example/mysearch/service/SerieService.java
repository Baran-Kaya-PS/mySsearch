package com.example.mysearch.service;
import com.example.mysearch.model.History;
import com.example.mysearch.model.Series;
import com.example.mysearch.model.SeriesSimilarity;
import com.example.mysearch.model.User;
import com.example.mysearch.repository.SerieRepository;
import com.example.mysearch.repository.SeriesSimilarityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
@Service
public class SerieService {
    private final SerieRepository serieRepository;
    private final SeriesSimilarityRepository seriesSimilarityRepository;
    private final HistoryService historyService;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(SerieService.class);
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private final Map<String, List<String>> seriesCache = new ConcurrentHashMap<>();
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
        // Prendre en compte le nombre de vues, les likes et les dislikes
        double viewScore = series.getViews();
        double likeScore = series.getLikes();
        double dislikeScore = series.getDislikes();

        // Vous pouvez ajuster ces poids en fonction de l'importance que vous accordez aux vues, aux likes et aux dislikes
        double viewWeight = 0.05;
        double likeWeight = 0.1;
        double dislikeWeight = -0.08;

        return viewScore * viewWeight + likeScore * likeWeight + dislikeScore * dislikeWeight;
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

        seriesList.parallelStream().forEach(series1 -> {
            seriesList.parallelStream().forEach(series2 -> {
                if (!series1.equals(series2)) {
                    double similarity = cosineSimilarity(series1.getVecteursTFIDF(), series2.getVecteursTFIDF());
                    // Stockez cette valeur de similarité pour une utilisation ultérieure
                    similarities.put(new Pair<>(series1, series2), similarity);
                    // Stockez cette valeur de similarité dans la base de données
                    saveSimilarityToDatabase(series1, series2, similarity);
                }
            });
        });
    }

    public void saveSimilarityToDatabase(Series series1, Series series2, double similarity) {
        SeriesSimilarity seriesSimilarity = new SeriesSimilarity();
        seriesSimilarity.setSeries1Id(series1.getId());
        seriesSimilarity.setSeries2Id(series2.getId());
        seriesSimilarity.setSimilarity(similarity);
        seriesSimilarityRepository.save(seriesSimilarity);
    }
    public List<Series> getSimilarSeries(String seriesId) {
        // Récupérer la série par son ID
        Series series = serieRepository.findById(seriesId).orElse(null);

        // Vérifier si la série existe
        if (series == null) {
            logger.error("Series avec id " + seriesId + " non trouvée");
            return Collections.emptyList();
        }

        // Récupérer les séries similaires depuis le cache
        List<String> similarSeriesIds = series.getSimilarSeriesCache().get(seriesId);

        // Récupérer les objets Series correspondants aux IDs
        List<Series> similarSeries = similarSeriesIds.stream()
                .map(id -> serieRepository.findById(id).orElse(null))
                .collect(Collectors.toList());

        logger.info("Séries similaires trouvées pour seriesId " + seriesId + ": " + similarSeries.stream().map(Series::getTitre).collect(Collectors.joining(", ")));

        return similarSeries;
    }
    public List<Map<Series, String>> recommendSeries(String userId) {
        // Récupérer les séries que l'utilisateur a visionnées
        List<String> watchedSeries = historyService.getWatchedSeries(userId);

        // Si l'utilisateur est nouveau ou peu actif, recommander les séries les plus populaires
        if (watchedSeries.size() < 2) {
            return getPopularSeries().stream().limit(10).map(series -> {
                Map<Series, String> map = new HashMap<>();
                map.put(series, "Cette série est très populaire.");
                return map;
            }).collect(Collectors.toList());
        }

        // Limiter le nombre de séries visionnées à prendre en compte
        watchedSeries = watchedSeries.subList(Math.max(watchedSeries.size() - 10, 0), watchedSeries.size());

        // Inclure les séries visionnées dans le processus de recommandation
        Map<Series, Double> recommendedSeriesScore = new ConcurrentHashMap<>();
        Map<Series, String> recommendedSeriesExplanations = new ConcurrentHashMap<>();
        ExecutorService executorService = Executors.newFixedThreadPool(4); // Créer un pool de threads

        for (String serieId : watchedSeries) {
            // Récupérer les séries similaires depuis le cache
            List<String> similarSeriesIds = cacheSimilarSeries(serieId);

            // Limiter le nombre de séries similaires à traiter
            similarSeriesIds = similarSeriesIds.subList(0, Math.min(similarSeriesIds.size(), 10));

            for (String similarSeriesId : similarSeriesIds) {
                List<String> finalSimilarSeriesIds = similarSeriesIds;
                executorService.submit(() -> {
                    Series similarSeries = getSerieById(similarSeriesId);
                    double score = recommendedSeriesScore.getOrDefault(similarSeries, 0.0);

                    // Calculer le score de recommandation basé sur la position dans la liste, le nombre de likes, de vues et de dislikes
                    score += (finalSimilarSeriesIds.size() - finalSimilarSeriesIds.indexOf(similarSeriesId)) + similarSeries.getLikes() * 0.5 + similarSeries.getViews() * 0.5 - similarSeries.getDislikes() * 0.5;

                    // Réduire le score des séries très populaires pour augmenter la diversité
                    if (similarSeries.getViews() > 10000) {
                        score *= 0.9;
                    }

                    recommendedSeriesScore.put(similarSeries, score);
                    recommendedSeriesExplanations.put(similarSeries, "Recommandée en fonction de vos recherches.");
                });
            }
        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Ajouter les séries les plus populaires à la liste des séries recommandées
        List<Series> popularSeries = getPopularSeries().stream().limit(5).collect(Collectors.toList()); // Limiter à 5 séries populaires
        for (Series series : popularSeries) {
            double score = recommendedSeriesScore.getOrDefault(series, 0.0);
            // Ajouter un bonus au score pour les séries populaires
            score += 100;
            recommendedSeriesScore.put(series, score);
            recommendedSeriesExplanations.put(series, "Populaire chez les utilisateurs.");
        }

        // Filtrage collaboratif
        List<User> allUsers = (List<User>) userService.getAllUsers();
        for (User user : allUsers) {
            if (!user.getId().equals(userId)) {
                History otherUserHistory = historyService.getHistoryByUserId(user.getId());
                if (otherUserHistory == null) {
                    continue;
                }

                List<String> otherUserLikes = otherUserHistory.getSerieLike();
                for (String serieId : otherUserLikes) {
                    if (!watchedSeries.contains(serieId)) {
                        Series series = getSerieById(serieId);
                        if (series != null) {
                            double score = recommendedSeriesScore.getOrDefault(series, 0.0);
                            score += 50; // Ajouter un bonus au score pour les séries aimées par des utilisateurs similaires
                            recommendedSeriesScore.put(series, score);
                            recommendedSeriesExplanations.put(series, "Recommandée en fonction de vos recherches.");
                        }
                    }
                }
            }
        }

        // Trier les séries recommandées par score de recommandation
        List<Series> recommendedSeries = recommendedSeriesScore.entrySet().stream()
                .sorted((e1, e2) -> {
                    // Ajouter un petit nombre aléatoire au score de chaque série
                    double score1 = e1.getValue() + Math.random() * 0.1;
                    double score2 = e2.getValue() + Math.random() * 0.1;
                    return Double.compare(score2, score1);
                })
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // Mélanger les séries recommandées
        Collections.shuffle(recommendedSeries);

        Collections.shuffle(recommendedSeries);
        // Créer une liste de maps pour stocker les séries recommandées et leurs explications
        List<Map<Series, String>> finalRecommendations = new ArrayList<>();
        for (Series series : recommendedSeries) {
            Map<Series, String> map = new HashMap<>();
            map.put(series, recommendedSeriesExplanations.get(series));
            finalRecommendations.add(map);
        }

        return finalRecommendations;
    }
    private double getRecommendationScore(Series series, List<String> userLikes) {
        // Prendre en compte le nombre de vues, les likes et les dislikes
        double viewScore = series.getViews();
        double likeScore = userLikes.contains(series.getId()) ? 1.0 : 0.0;
        double dislikeScore = series.getDislikes();

        // Vous pouvez ajuster ces poids en fonction de l'importance que vous accordez aux vues, aux likes et aux dislikes
        double viewWeight = 0.5;
        double likeWeight = 1.0;
        double dislikeWeight = -0.5;

        return viewScore * viewWeight + likeScore * likeWeight + dislikeScore * dislikeWeight;
    }

    private List<Series> getPopularSeries() {
        // Implémentez cette méthode pour retourner les séries les plus populaires ou tendance
        // Par exemple, vous pouvez retourner les séries avec le plus grand nombre de vues
        return serieRepository.findAll().stream()
                .sorted(Comparator.comparing(Series::getViews).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }
    public void resetSeriesStats() {
        List<Series> allSeries = serieRepository.findAll();
        for (Series series : allSeries) {
            series.setLikes(0);
            series.setDislikes(0);
            series.setViews(0);
            serieRepository.save(series);
        }
    }
    public List<String> cacheSimilarSeries(String seriesId){
        // Si le cache pour la série existe déjà, retournez les ID des séries similaires à partir du cache
        if (seriesCache.containsKey(seriesId)) {
            return seriesCache.get(seriesId);
        }

        // Si le cache n'existe pas, calculez les séries similaires et remplissez le cache
        List<Series> similarSeries = getSimilarSeries(seriesId);

        // Convertir la liste des séries similaires en une liste d'ID de séries
        List<String> similarSeriesIds = similarSeries.stream().map(Series::getId).collect(Collectors.toList());

        // Enregistrez les ID des séries similaires dans le cache en mémoire
        seriesCache.put(seriesId, similarSeriesIds);

        // Enregistrez les ID des séries similaires dans le cache dans la base de données
        Series series = getSerieById(seriesId);
        series.getSimilarSeriesCache().put(seriesId, similarSeriesIds);
        serieRepository.save(series);

        return similarSeriesIds;
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
