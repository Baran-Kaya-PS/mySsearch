package com.example.mysearch;

import com.example.mysearch.model.Serie;
import com.example.mysearch.repository.SerieRepository;
import com.example.mysearch.utils.TFIDFCalculator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MySearchApplicationTests {

    @Autowired // injecte le bean dans le test
    private SerieRepository serieRepository;
    @Autowired
    private TFIDFCalculator tfidfCalculator;


    @Test
    void shouldFetchData() {
        String expectedTitle = "breakingbad";


        List<Serie> series = serieRepository.findByTitre(expectedTitle);


        assertThat(series).isNotEmpty();
        assertThat(series.get(0).getTitre()).isEqualTo(expectedTitle);
    }
    @Test
    void tfidfScoreForBreakingBadShouldBeHighestForMeth() {
        // Given
        String keyword = "meth";
        String expectedTopTitle = "breakingbad";

        // When
        List<Map<String, Object>> topSeries = tfidfCalculator.findTopSeries(Collections.singletonList(keyword), 10);

        // Then
        assertThat(topSeries).isNotEmpty();
        assertThat(topSeries.get(0)).containsEntry("title", expectedTopTitle);
    }
}
