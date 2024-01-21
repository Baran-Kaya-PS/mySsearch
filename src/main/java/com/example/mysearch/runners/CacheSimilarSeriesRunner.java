package com.example.mysearch.runners;

import com.example.mysearch.service.SerieService;
import com.example.mysearch.repository.SerieRepository;
import com.example.mysearch.model.Series;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

/**
 * Cette classe représente un exécuteur de cache pour les séries similaires.
 * 
 * Elle implémente l'interface CommandLineRunner et est annotée avec @Component pour être gérée par Spring.
 * 
 * Le constructeur de la classe prend en paramètre le service de gestion des séries (SerieService) et le repository des séries (SerieRepository).
 * 
 * Lorsque la méthode run est appelée, elle récupère toutes les séries à partir du repository et vérifie si le cache des séries similaires est vide.
 * Si le cache est vide, elle utilise le service de gestion des séries pour mettre en cache les séries similaires.
 * Sinon, elle ne fait rien.
 */
/**
    * Constructeur de la classe CacheSimilarSeriesRunner.
    * 
    * @param serieService    le service de gestion des séries
    * @param serieRepository le repository des séries
    */
@Component
public class CacheSimilarSeriesRunner implements CommandLineRunner {
    private final SerieService serieService;
    private final SerieRepository serieRepository;
    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CacheSimilarSeriesRunner.class);

    public CacheSimilarSeriesRunner(SerieService serieService, SerieRepository serieRepository) {
        this.serieService = serieService;
        this.serieRepository = serieRepository;
    }

    @Override
    public void run(String... args) {
        /*
        List<Series> allSeries = serieRepository.findAll();
        for (Series series : allSeries) {
            if (series.getSimilarSeriesCache().isEmpty()) {
                serieService.cacheSimilarSeries(series.getId());
            } else {
                logger.info("Similar series already cached for series with id: " + series.getId());
            }
        }
         */
    }
}