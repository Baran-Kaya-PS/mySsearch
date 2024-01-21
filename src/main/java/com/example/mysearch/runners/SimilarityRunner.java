package com.example.mysearch.runners;

import com.example.mysearch.service.SerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Cette classe est responsable de l'exécution du calcul des similarités entre les séries.
 * Elle implémente l'interface CommandLineRunner de Spring Boot pour pouvoir être exécutée au démarrage de l'application.
 */
@Component
public class SimilarityRunner implements CommandLineRunner {

    private final SerieService serieService;

    /**
     * Constructeur de la classe SimilarityRunner.
     * 
     * @param serieService le service responsable du calcul des similarités entre les séries
     */
    @Autowired
    public SimilarityRunner(SerieService serieService) {
        this.serieService = serieService;
    }

    /**
     * Méthode exécutée au démarrage de l'application.
     * Elle appelle la méthode calculateSeriesSimilarities du service SerieService pour calculer les similarités entre les séries.
     * 
     * @param args les arguments de la ligne de commande
     * @throws Exception en cas d'erreur lors du calcul des similarités
     */
    @Override
    public void run(String... args) throws Exception {
        serieService.calculateSeriesSimilarities();
    }
}