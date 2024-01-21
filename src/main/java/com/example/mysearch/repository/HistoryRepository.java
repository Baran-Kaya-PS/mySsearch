package com.example.mysearch.repository;

import com.example.mysearch.model.History;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * Cette interface représente le repository pour l'entité History.
 * Elle étend l'interface MongoRepository pour bénéficier des fonctionnalités de base de MongoDB.
 */
public interface HistoryRepository extends MongoRepository<History, String> {

    /**
     * Recherche et renvoie un objet History en fonction de l'identifiant de l'utilisateur.
     *
     * @param utilisateurId l'identifiant de l'utilisateur
     * @return un objet Optional contenant l'objet History correspondant, s'il existe
     */
    Optional<History> findByUtilisateurId(String utilisateurId);

    /**
     * Recherche et renvoie une liste d'objets History en fonction de l'identifiant de la série.
     *
     * @param serieId l'identifiant de la série
     * @return une liste d'objets History correspondant à la série
     */
    List<History> findBySerieId(String serieId);

    /**
     * Supprime les objets History en fonction de l'identifiant de l'utilisateur.
     *
     * @param id l'identifiant de l'utilisateur
     */
    void deleteByUtilisateurId(String id);
}