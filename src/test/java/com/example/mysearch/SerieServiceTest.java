package com.example.mysearch;

import com.example.mysearch.model.Series;
import com.example.mysearch.service.SerieService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SerieServiceTest {

    @Autowired
    private SerieService serieService;

    @Test
    public void testGetSimilarSeries() {
        // Remplacer par un ID de série valide dans votre base de données
        String seriesId = "65a7fc3243ee036fe9602ad9";

        List<Series> similarSeries = serieService.getSimilarSeries(seriesId);

        // Vérifiez que la liste n'est pas vide
        assertFalse(similarSeries.isEmpty());

        // Récupérez la série avec le score de similarité le plus élevé
        Series mostSimilarSeries = similarSeries.get(0);

        // Vérifiez que la série la plus similaire est bien dans la liste des séries similaires
        assertTrue(similarSeries.contains(mostSimilarSeries));
    }
}