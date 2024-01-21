package com.example.mysearch.repository;
import com.example.mysearch.model.Series;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
/**
 * Cette interface représente le repository pour l'entité Serie.
 * Elle étend l'interface MongoRepository pour bénéficier des fonctionnalités de base de MongoDB.
 */
public interface SerieRepository extends MongoRepository<Series, String>{
    /**
     * Recherche les séries par titre.
     * 
     * @param titre Le titre de la série à rechercher.
     * @return Une liste des séries correspondantes au titre donné.
     */
    List<Series> findByTitre(String titre);

    /**
     * Vérifie si une série existe avec le titre donné.
     * 
     * @param name Le titre de la série à vérifier.
     * @return true si une série avec le titre donné existe, sinon false.
     */
    boolean existsByTitre(String name);
}
