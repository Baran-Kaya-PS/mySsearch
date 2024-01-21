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
/**
 * Service de gestion des séries.
 * Ce service permet de gérer les opérations liées aux séries telles que la recherche, l'ajout, la suppression, etc.
 * Il fournit également des fonctionnalités de recommandation de séries basées sur l'historique de l'utilisateur.
 */
@Service
public class SerieService {
    private final SerieRepository serieRepository;
    private final SeriesSimilarityRepository seriesSimilarityRepository;
    private final HistoryService historyService;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(SerieService.class);
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private final Map<String, List<String>> seriesCache = new ConcurrentHashMap<>();
    // Cache pour les séries similaires
    private final Map<String, List<Series>> similarSeriesCache = new ConcurrentHashMap<>();

    // Cache pour les séries recommandées
    private final Map<String, List<Series>> recommendationCache = new ConcurrentHashMap<>();

    // Cache pour les similarités de séries
    private final Map<Pair<Series, Series>, Double> seriesSimilaritiesCache = new ConcurrentHashMap<>();

    /**
     * Constructeur du service de gestion des séries.
     */
    @Autowired // signifie que Spring va automatiquement injecter les dépendances nécessaires à ce service
    public SerieService(SerieRepository serieRepository, SeriesSimilarityRepository seriesSimilarityRepository, HistoryService historyService, UserService userService) {
        this.serieRepository = serieRepository;
        this.seriesSimilarityRepository = seriesSimilarityRepository;
        this.historyService = historyService;
        this.userService = userService;
    }
    /**
     * Récupère toutes les séries.
     * @return  la liste de toutes les séries
     */
    public Iterable<Series> getAllSeries() {
        return serieRepository.findAll();
    }
    /**
     * Récupère une série par son ID.
     * @param serieId   l'ID de la série à récupérer
     */
    public Series getSerieById(String serieId) {
        return serieRepository.findById(serieId).orElse(null);
    }
    /**
     * Ajoute une série.
     * @param serie la série à ajouter
     */
    public Series addSerie(Series serie) {
        return serieRepository.save(serie);
    }
    /**
     * Met à jour une série.
     * @param serieId   l'ID de la série à mettre à jour
     */
    public void deleteSerie(String serieId) {
        serieRepository.deleteById(serieId);
    }
    /**
     * Cherche les séries par mot-clé.
     * @param keyword   le mot-clé de recherche
     * @return          la liste des séries correspondantes, triées par score décroissant
     */
    public Iterable<Series> searchSeriesByKeyword(String keyword) {
        String[] keywords = keyword.split("\\s+");
        List<Series> allSeries = serieRepository.findAll();

        return allSeries.stream()
                .map(serie -> new AbstractMap.SimpleEntry<>(
                        serie,
                        Arrays.stream(keywords) // flux de mots clés
                                .mapToDouble(key -> serie.getVecteursTFIDF().getOrDefault(key.toLowerCase(), 0.0))
                                .sum() // somme des scores TFIDF
                ))
                .filter(entry -> entry.getValue() > 0)
                .sorted(Map.Entry.<Series, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    /**
     * Cherche les séries par nom.
     * @param name   le nom de la série à rechercher
     * @return          la liste des séries correspondantes, triées par score décroissant
     */
    public Iterable<Series> searchSeriesByName(String name) {
        // si le nom est dans la base de données on retourne la série
        if (serieRepository.existsByTitre(name)) {
            return serieRepository.findByTitre(name);
        }
        return serieRepository.findByTitre(name);
    }
    /**
     * Cherche les séries par genre.
     * @param genre   le genre de la série à rechercher
     * @return          la liste des séries correspondantes, triées par score décroissant
     */
    public Series getSerieByTitle(String serieName) {
        return serieRepository.findByTitre(serieName).get(0);
    }
    /**
     * Cherche les séries par genre.
     * @param genre   le genre de la série à rechercher
     * @return          la liste des séries correspondantes, triées par score décroissant
     */
    public List<Series> getRecommendedSeries(String userId) {
        if (recommendationCache.containsKey(userId)) {
            return recommendationCache.get(userId);
        }

        List<Series> recommendedSeries = calculateRecommendedSeries(userId);

        recommendationCache.put(userId, recommendedSeries);

        return recommendedSeries;
    }

    public List<Series> calculateRecommendedSeries(String userId) {
        History userHistory = historyService.getHistoryByUserId(userId);
        if (userHistory == null) {
            return Collections.emptyList();
        }

        List<String> userLikes = userHistory.getSerieLike();

        List<User> allUsers = (List<User>) userService.getAllUsers();

        List<Series> recommendedSeries = Collections.synchronizedList(new ArrayList<>());

        allUsers.parallelStream().forEach(user -> {
            if (!user.getId().equals(userId)) {
                // Récupérer l'historique de l'autre utilisateur
                History otherUserHistory = historyService.getHistoryByUserId(user.getId());
                if (otherUserHistory == null) {
                    return;
                }

                // Récupérer les séries que l'autre utilisateur a aimées
                List<String> otherUserLikes = otherUserHistory.getSerieLike();

                // Parcourir les séries que l'autre utilisateur a aimées
                otherUserLikes.parallelStream().forEach(serieName -> {
                    // Si l'utilisateur n'a pas déjà aimé cette série et qu'il ne l'a pas déjà dislikée
                    if (!userLikes.contains(serieName) && !userHistory.getSerieDislike().contains(serieName)) {
                        // Ajouter la série à la liste des séries recommandées
                        Series serie = getSerieByTitle(serieName);
                        if (serie != null && !recommendedSeries.contains(serie)) {
                            recommendedSeries.add(serie);
                        }
                    }
                });
            }
        });

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
        double viewWeight = 0.05;
        double likeWeight = 0.1;
        double dislikeWeight = -0.08;
        // les poids sont léger pour éviter de fausser les résultats
        return viewScore * viewWeight + likeScore * likeWeight + dislikeScore * dislikeWeight;
    }
    /**
     * Incrémente le nombre de likes d'une série.
     * @param serieId   l'ID de la série
     * @return          la série mise à jour
     */
    public void incrementLikeCount(String serieId) {
        Series serie = getSerieById(serieId);
        if (serie != null) {
            serie.setLikes(serie.getLikes() + 1);
            serieRepository.save(serie);
        }
    }
    /**
     * Incrémente le nombre de dislikes d'une série.
     * @param serieId   l'ID de la série
     * @return          la série mise à jour
     */
    public void incrementDislikeCount(String serieId) {
        Series serie = getSerieById(serieId);
        if (serie != null) {
            serie.setDislikes(serie.getDislikes() + 1);
            serieRepository.save(serie);
        }
    }
    /**
     * Incrémente le nombre de vues d'une série.
     * @param serieId   l'ID de la série
     * @return          la série mise à jour
     */
    public void incrementViewCount(String serieId) {
        Series serie = getSerieById(serieId);
        if (serie != null) {
            serie.setViews(serie.getViews() + 1);
            serieRepository.save(serie);
        }
    }
    /**
     * Calcule les similarités entre les séries.
     * Cette méthode calcule les similarités entre les séries et les stocke dans la base de données.
     * Fonction utilisé pour calculer les similarités entre les séries.
     */
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
    /**
     * Calcule les similarités entre les séries.
     * Cette méthode calcule les similarités entre les séries et les stocke dans la base de données.
     * Fonction utilisé dans le projet.
     * Elle récupère les vecteurs TF-IDF de chaque série et calcule la similarité cosinus entre chaque paire de séries.
     */
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
    /**
     * Enregistre la similarité entre deux séries dans la base de données.
     * @param series1       la première série
     * @param series2       la deuxième série
     * @param similarity    la similarité entre les deux séries
     * @return              la similarité enregistrée
     */
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
    /**
     * Recommande des séries à un utilisateur.
     * Cette méthode recommande des séries à un utilisateur en fonction de son historique de recherche.
     * Elle utilise le filtrage collaboratif et le filtrage basé sur le contenu pour recommander des séries.
     * Malheureusement, cette méthode n'est pas très efficace car elle est très lente.
     * Pour l'améliorer, on à utilisé des threads pour paralléliser les calculs, mis en cache les resultats et limité le nombre de séries similaires à traiter.
     * @param userId    l'ID de l'utilisateur
     * @return          la liste des séries recommandées
     */
    public List<Map<Series, String>> recommendSeries(String userId) {
        // Récupérer les séries que l'utilisateur a visionnées
        List<String> watchedSeries = historyService.getWatchedSeries(userId);

        // Si l'utilisateur est nouveau ou peu actif, recommander les séries les plus populaires
        if (watchedSeries.size() < 2) { // 2 série pour avoir un minimum de données
            return getPopularSeries().stream().limit(10).map(series -> {
                Map<Series, String> map = new HashMap<>();
                map.put(series, "Cette série est très populaire.");
                return map;
            }).collect(Collectors.toList()); // on retourne les 10 premières séries les plus populaires via le calcul du score de recommandation
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

                    // Calculer le score de recommandation par apport à la position dans la liste, le nombre de likes, de vues et de dislikes
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

        executorService.shutdown(); // Arrêter le pool de threads
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

        // Filtrage collaboratif, recommander des séries aimées par des utilisateurs qui aiment les mêmes séries que l'utilisateur actuel
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

        // Tri des séries recommandées par score de recommandation
        List<Series> recommendedSeries = recommendedSeriesScore.entrySet().stream()
                .sorted((e1, e2) -> {
                    // Ajouter un petit nombre aléatoire au score de chaque série pour éviter les séries avec le même score
                    double score1 = e1.getValue() + Math.random() * 0.1;
                    double score2 = e2.getValue() + Math.random() * 0.1;
                    return Double.compare(score2, score1);
                })
                .map(Map.Entry::getKey)
                .collect(Collectors.toList()); // On retourne la liste

        // Mélanger les séries recommandées

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
    /**
     * Calcule le score de recommandation d'une série.
     * @param series    la série
     * @return          le score de recommandation
     */
    private double getRecommendationScore(Series series, List<String> userLikes) {
        double viewScore = series.getViews();
        double likeScore = userLikes.contains(series.getId()) ? 1.0 : 0.0;
        double dislikeScore = series.getDislikes();

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
        if (seriesCache.containsKey(seriesId)) {
            return seriesCache.get(seriesId);
        }

        List<Series> similarSeries = getSimilarSeries(seriesId);

        List<String> similarSeriesIds = similarSeries.stream().map(Series::getId).collect(Collectors.toList());

        seriesCache.put(seriesId, similarSeriesIds);

        Series series = getSerieById(seriesId);
        series.getSimilarSeriesCache().put(seriesId, similarSeriesIds);
        serieRepository.save(series);

        return similarSeriesIds;
    }


    public class Pair<F, S> { // Pratique pour le calcul des similarités
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
