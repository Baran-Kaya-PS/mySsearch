package com.example.mysearch.runners;

import com.example.mysearch.utils.TFIDFCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.*;

@Component
public class TFIDFRunner implements CommandLineRunner {

    private final TFIDFCalculator calculator;

    @Autowired
    public TFIDFRunner(TFIDFCalculator calculator) {
        this.calculator = calculator;
    }

    @Override
    public void run(String... args) throws Exception {
        /*
        List<String> searchTerms = asList("prison", "évader", "détenu");
        searchTerms = TFIDFCalculator.removeAccents(searchTerms);
        int topN = 10;
        List<Map<String, Object>> topSeries = calculator.findTopSeries(searchTerms, topN);
        // Affichage des résultats
        topSeries.forEach(entry -> System.out.println("Série : " + entry.get("title") + ", Score TF-IDF : " + entry.get("similarity")));
         */
    }
}
