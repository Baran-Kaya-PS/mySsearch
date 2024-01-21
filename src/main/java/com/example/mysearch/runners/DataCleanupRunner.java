package com.example.mysearch.runners;

import com.example.mysearch.service.HistoryService;
import com.example.mysearch.service.SerieService;
import com.example.mysearch.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
/**
 * Cette classe représente un exécuteur de nettoyage des données.
 * Elle implémente l'interface CommandLineRunner et est annotée avec @Component et @Order(1).
 * L'exécuteur de nettoyage des données est responsable de la suppression des données obsolètes ou inutiles.
 * Il utilise les services UserService, HistoryService et SerieService pour effectuer les opérations de nettoyage.
 */
@Order(1)
@Component
public class DataCleanupRunner implements CommandLineRunner {

    private final UserService userService;
    private final HistoryService historyService;
    private final SerieService serieService;

    /**
     * Constructeur de la classe DataCleanupRunner.
     *
     * @param userService    Le service utilisateur.
     * @param historyService Le service historique.
     * @param serieService   Le service série.
     */
    public DataCleanupRunner(UserService userService, HistoryService historyService, SerieService serieService) {
        this.userService = userService;
        this.historyService = historyService;
        this.serieService = serieService;
    }

    @Override
    public void run(String... args) throws Exception {
        /*
        userService.getAllUsers().forEach(user -> userService
                .deleteUser(user.getId()));
        historyService.getAllHistoryRecords().forEach(history -> historyService.deleteHistoryRecord(history.getId()));
        serieService.resetSeriesStats();
         */
    }
}