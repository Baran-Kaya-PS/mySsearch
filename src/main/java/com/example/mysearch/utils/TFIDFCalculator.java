package com.example.mysearch.utils;

import com.example.mysearch.model.Series;
import com.example.mysearch.repository.SerieRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.text.Normalizer;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Cette classe est utilisée pour calculer le score TF-IDF (Term Frequency-Inverse Document Frequency)
 * et effectuer des opérations associées sur une liste de documents.
 */
@Component
public class TFIDFCalculator {
    private final Map<String, Double> idfCache;
    private final SerieRepository serieRepository;

    /**
     * Constructeur de la classe TFIDFCalculator.
     * 
     * @param serieRepository le repository utilisé pour accéder aux séries.
     */
    @Autowired
    public TFIDFCalculator(SerieRepository serieRepository) {
        idfCache = new HashMap<>();
        this.serieRepository = serieRepository;
    }

    /**
     * Met à jour le cache IDF avec les nouvelles valeurs IDF.
     * 
     * @param docs la liste de documents utilisée pour calculer les valeurs IDF.
     */
    public void updateIdfCache(List<List<String>> docs) {
        Set<String> uniqueTerms = new HashSet<>();
        docs.forEach(uniqueTerms::addAll);
        uniqueTerms.forEach(term -> idfCache.put(term, idf(docs, term)));
    }

    /**
     * Calcule le score TF (Term Frequency) d'un terme dans un document.
     * 
     * @param doc  le document.
     * @param term le terme.
     * @return le score TF du terme dans le document.
     */
    private double tf(List<String> doc, String term) {
        long count = doc.stream().filter(term::equalsIgnoreCase).count();
        return (double) count / doc.size();
    }

    /**
     * Calcule la valeur IDF (Inverse Document Frequency) d'un terme dans une liste de documents.
     * 
     * @param docs la liste de documents.
     * @param term le terme.
     * @return la valeur IDF du terme dans la liste de documents.
     */
    private double idf(List<List<String>> docs, String term) {
        long count = docs.stream().filter(doc -> doc.contains(term.toLowerCase())).count();
        if (count == 0) return 0;
        return Math.log((double) docs.size() / count);
    }

    /**
     * Calcule le score TF-IDF d'un terme dans un document.
     * 
     * @param doc  le document.
     * @param term le terme.
     * @return le score TF-IDF du terme dans le document.
     */
    public double tfIdf(List<String> doc, String term) {
        // Utilise la valeur IDF mise en cache si elle est disponible
        double termIdf = idfCache.containsKey(term) ? idfCache.get(term) : idf(Collections.singletonList(doc), term);
        return tf(doc, term) * termIdf;
    }

    /**
     * Lit un fichier JSON et retourne son contenu sous forme de liste de maps.
     * 
     * @param jsonFilePath le chemin du fichier JSON.
     * @return le contenu du fichier JSON sous forme de liste de maps.
     * @throws Exception si une erreur se produit lors de la lecture du fichier JSON.
     */
    public List<Map<String, Object>> readJsonFile(String jsonFilePath) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(jsonFilePath), new TypeReference<List<Map<String, Object>>>() {});
    }

    /**
     * Recherche les séries les plus similaires en fonction des termes de recherche donnés.
     * 
     * @param terms les termes de recherche.
     * @param n     le nombre de séries à retourner.
     * @return une liste de maps contenant les séries les plus similaires et leur score de similarité.
     */
    public List<Map<String, Object>> findTopSeries(List<String> terms, int n) {
        List<Series> seriesList = serieRepository.findAll();
        List<Map<String, Object>> seriesSimilarities = new ArrayList<>();

        for (Series serie : seriesList) {
            double cumulativeTfidfScore = terms.stream()
                    .mapToDouble(term -> serie.getVecteursTFIDF().getOrDefault(term.toLowerCase(), 0.0))
                    .sum();

            if (cumulativeTfidfScore > 0) {
                Map<String, Object> seriesWithSimilarity = new HashMap<>();
                seriesWithSimilarity.put("title", serie.getTitre());
                seriesWithSimilarity.put("similarity", cumulativeTfidfScore);
                seriesSimilarities.add(seriesWithSimilarity);
            }
        }

        return seriesSimilarities.stream()
                .sorted(Comparator.comparingDouble((Map<String, Object> series) -> (Double) series.get("similarity")).reversed())
                .limit(n)
                .collect(Collectors.toList());
    }

    /**
     * Supprime les accents des termes de recherche donnés.
     * 
     * @param searchTerms les termes de recherche.
     * @return une liste de termes de recherche sans accents.
     */
    public static List<String> removeAccents(List<String> searchTerms) {
        return searchTerms.stream()
                .map(term -> Normalizer.normalize(term, Normalizer.Form.NFD))
                .map(term -> term.replaceAll("[\\p{InCombiningDiacriticalMarks}]", ""))
                .collect(Collectors.toList());
    }

}