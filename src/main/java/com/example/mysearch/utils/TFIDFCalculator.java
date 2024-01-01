package com.example.mysearch.utils;

import com.example.mysearch.model.Serie;
import com.example.mysearch.repository.SerieRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.text.Normalizer;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
@Component
public class TFIDFCalculator {
    private final Map<String, Double> idfCache;
    private SerieRepository serieRepository;

    public TFIDFCalculator() {
        idfCache = new HashMap<>();
        this.serieRepository = serieRepository;
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

    public List<Map<String, Object>> findTopSeries(List<String> terms, int n) {
        List<Serie> seriesList = serieRepository.findAll();
        List<Map<String, Object>> seriesSimilarities = new ArrayList<>();

        for (Serie serie : seriesList) {
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
    public static void main(String[] args) {
        TFIDFCalculator calculator = new TFIDFCalculator();
        try {
            List<String> searchTerms = Arrays.asList("prison","évader","détenu");

            // Appliquer la fonction pour enlever les accents
            searchTerms = removeAccents(searchTerms);

            int topN = 10;

            List<Map<String, Object>> topSeries = calculator.findTopSeries(searchTerms, topN);



            // Affichage des résultats
            topSeries.forEach(entry -> System.out.println("Série : " + entry.get("title") + ", Score TF-IDF : " + entry.get("similarity")));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static List<String> removeAccents(List<String> searchTerms) {
        return searchTerms.stream()
                .map(term -> Normalizer.normalize(term, Normalizer.Form.NFD))
                .map(term -> term.replaceAll("[\\p{InCombiningDiacriticalMarks}]", ""))
                .collect(Collectors.toList());
    }
}