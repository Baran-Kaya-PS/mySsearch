package com.example.mysearch.utils;

import java.util.*;

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

    public static void main(String[] args) {
        //
    }
}
