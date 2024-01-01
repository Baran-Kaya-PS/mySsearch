package com.example.mysearch.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.text.Normalizer;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class TFIDFCalculator {
    private final Map<String, Double> idfCache;

    public TFIDFCalculator() {
        idfCache = new HashMap<>();
    }
    // Call this method when your corpus changes to update the IDF values
    public void updateIdfCache(List<List<String>> docs) {
        Set<String> uniqueTerms = new HashSet<>();
        docs.forEach(uniqueTerms::addAll);
        uniqueTerms.forEach(term -> idfCache.put(term, idf(docs, term)));
    }

    private double tf(List<String> doc, String term) {
        long count = doc.stream().filter(term::equalsIgnoreCase).count();
        return (double) count / doc.size();
    }

    private double idf(List<List<String>> docs, String term) {
        long count = docs.stream().filter(doc -> doc.contains(term.toLowerCase())).count();
        if (count == 0) return 0;
        return Math.log((double) docs.size() / count);
    }

    public double tfIdf(List<String> doc, String term) {
        // Use cached IDF value if available
        double termIdf = idfCache.containsKey(term) ? idfCache.get(term) : idf(Collections.singletonList(doc), term);
        return tf(doc, term) * termIdf;
    }
    public List<Map<String, Object>> readJsonFile(String jsonFilePath) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(jsonFilePath), new TypeReference<List<Map<String, Object>>>() {});
    }

    public List<Map<String, Object>> findTopSeries(List<String> terms, int n, String jsonFilePath) throws Exception {
        // Lire les données TF-IDF depuis le fichier JSON
        List<Map<String, Object>> seriesList = readJsonFile(jsonFilePath);

        // Calculer la similarité pour chaque série en fonction de tous les termes
        List<Map<String, Object>> seriesSimilarities = new ArrayList<>();
        for (Map<String, Object> seriesEntry : seriesList) {
            String seriesName = (String) seriesEntry.get("title");
            Map<String, Double> tfidfValues = (Map<String, Double>) seriesEntry.get("tfidf_vectors");

            // Calculer le score TF-IDF cumulatif pour tous les termes donnés dans cette série
            double cumulativeTfidfScore = terms.stream()
                    .mapToDouble(term -> tfidfValues.getOrDefault(term.toLowerCase(), 0.0))
                    .sum();

            // Si le score cumulatif est supérieur à 0, créer une nouvelle entrée de série avec la similarité
            if (cumulativeTfidfScore > 0) {
                Map<String, Object> seriesWithSimilarity = new HashMap<>(seriesEntry);
                seriesWithSimilarity.put("similarity", cumulativeTfidfScore);
                seriesSimilarities.add(seriesWithSimilarity);
            }
        }

        // Trier les séries en fonction de la similarité cumulée et prendre le top n
        return seriesSimilarities.stream()
                .sorted(Comparator.comparing(series -> (Double) series.get("similarity"), Comparator.reverseOrder()))
                .limit(n)
                .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        TFIDFCalculator calculator = new TFIDFCalculator();
        try {
            String file = "C:\\Users\\admin\\projects\\mySsearch\\src\\main\\java\\com\\example\\mysearch\\utils\\tf_idf_matrix.json";
            List<String> searchTerms = List.of("meth","pollo","hermanos");
            searchTerms = removeAccents(searchTerms);
            int topN = 10;
            List<Map<String, Object>> topSeries = calculator.findTopSeries(searchTerms, topN, file);
            topSeries.forEach(entry -> System.out.println("Série : " + entry.get("title") + ", Score TF-IDF : " + entry.get("similarity")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static List<String> removeAccents(List<String> searchTerms) {
        return searchTerms.stream()
                .map(term -> Normalizer.normalize(term, Normalizer.Form.NFD))
                .map(term -> term.replaceAll("[\\p{InCombiningDiacriticalMarks}]", ""))
                .collect(Collectors.toList());
    }
}