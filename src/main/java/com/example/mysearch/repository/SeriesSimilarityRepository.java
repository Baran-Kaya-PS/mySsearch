package com.example.mysearch.repository;

import com.example.mysearch.model.SeriesSimilarity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Cette interface représente un référentiel pour la classe SeriesSimilarity.
 * Elle hérite de l'interface MongoRepository pour effectuer des opérations CRUD sur la base de données MongoDB.
 */
public interface SeriesSimilarityRepository extends MongoRepository<SeriesSimilarity, String> {
    /**
     * Récupère une liste de SeriesSimilarity en fonction de l'identifiant de la série 1.
     *
     * @param seriesId l'identifiant de la série 1
     * @return une liste de SeriesSimilarity correspondant à l'identifiant de la série 1
     */
    List<SeriesSimilarity> findBySeries1Id(String seriesId);

    /**
     * Récupère une liste de SeriesSimilarity en fonction de l'identifiant de la série 2.
     *
     * @param seriesId l'identifiant de la série 2
     * @return une liste de SeriesSimilarity correspondant à l'identifiant de la série 2
     */
    List<SeriesSimilarity> findBySeries2Id(String seriesId);

    /**
     * Récupère une liste de SeriesSimilarity en fonction de l'identifiant de la série 1 ou de la série 2.
     *
     * @param seriesId  l'identifiant de la série 1
     * @param seriesId1 l'identifiant de la série 2
     * @return une liste de SeriesSimilarity correspondant à l'identifiant de la série 1 ou de la série 2
     */
    List<SeriesSimilarity> findBySeries1IdOrSeries2Id(java.lang.String seriesId, java.lang.String seriesId1);
}
