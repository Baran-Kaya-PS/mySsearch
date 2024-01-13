package com.example.mysearch.utils;

import com.example.mysearch.config.MySpringConfiguration;
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
@Component
public class TFIDFCalculator {
    private final Map<String, Double> idfCache;
    private final SerieRepository serieRepository;
    @Autowired
    public TFIDFCalculator(SerieRepository serieRepository) {
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
    public static List<String> removeAccents(List<String> searchTerms) {
        return searchTerms.stream()
                .map(term -> Normalizer.normalize(term, Normalizer.Form.NFD))
                .map(term -> term.replaceAll("[\\p{InCombiningDiacriticalMarks}]", ""))
                .collect(Collectors.toList());
    }
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MySpringConfiguration.class);
        TFIDFCalculator calculator = context.getBean(TFIDFCalculator.class);
    }
}