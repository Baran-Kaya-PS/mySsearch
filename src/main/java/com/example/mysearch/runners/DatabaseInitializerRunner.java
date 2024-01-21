package com.example.mysearch.runners;

import com.example.mysearch.model.History;
import com.example.mysearch.model.Series;
import com.example.mysearch.model.User;
import com.example.mysearch.repository.HistoryRepository;
import com.example.mysearch.repository.SerieRepository;
import com.example.mysearch.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
    * Initialiseur de base de données qui s'exécute au démarrage de l'application.
    *
    * @param serieRepository    le repository pour les séries
    * @param userRepository    le repository pour les utilisateurs
    * @param historyRepository  le repository pour l'historique
    * @param objectMapper      l'objet ObjectMapper pour la désérialisation JSON
    */
@Component
public class DatabaseInitializerRunner implements CommandLineRunner {

    private final SerieRepository serieRepository;
    private final UserRepository userRepository;
    private final HistoryRepository historyRepository;
    private final ObjectMapper objectMapper;

    private final MongoTemplate mongoTemplate;

    public DatabaseInitializerRunner(SerieRepository serieRepository, UserRepository userRepository, HistoryRepository historyRepository, ObjectMapper objectMapper, MongoTemplate mongoTemplate) {
        this.serieRepository = serieRepository;
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
        this.objectMapper = objectMapper;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!mongoTemplate.collectionExists(Series.class)) {
            mongoTemplate.createCollection(Series.class);
        }
        if (serieRepository.count() == 0) {
            InputStream seriesStream = new ClassPathResource("data/SAE.series.json").getInputStream();
            List<Series> seriesList = Arrays.asList(objectMapper.readValue(seriesStream, Series[].class));
            serieRepository.saveAll(seriesList);
        }

        if (!mongoTemplate.collectionExists(User.class)) {
            mongoTemplate.createCollection(User.class);
        }
        if (userRepository.count() == 0) {
            InputStream usersStream = new ClassPathResource("data/SAE.users.json").getInputStream();
            List<User> userList = Arrays.asList(objectMapper.readValue(usersStream, User[].class));
            userRepository.saveAll(userList);
        }

        if (!mongoTemplate.collectionExists(History.class)) {
            mongoTemplate.createCollection(History.class);
        }
        if (historyRepository.count() == 0) {
            InputStream historyStream = new ClassPathResource("data/SAE.historique.json").getInputStream();
            List<History> historyList = Arrays.asList(objectMapper.readValue(historyStream, History[].class));
            historyRepository.saveAll(historyList);
        }
    }
}