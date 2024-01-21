package com.example.mysearch.repository;

import com.example.mysearch.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interface représentant le référentiel des utilisateurs.
 * Étend l'interface MongoRepository pour les opérations de base sur la base de données MongoDB.
 * Étend également l'interface UserRepositoryCustom pour les opérations personnalisées.
 */
public interface UserRepository extends MongoRepository<User, String>, UserRepositoryCustom {
    /**
     * Recherche les utilisateurs par leur adresse e-mail.
     *
     * @param email l'adresse e-mail à rechercher
     * @return la liste des utilisateurs correspondant à l'adresse e-mail
     */
    List<User> findByEmail(String email);

    /**
     * Recherche un utilisateur par son nom.
     *
     * @param nom le nom de l'utilisateur à rechercher
     * @return l'utilisateur correspondant au nom, s'il existe
     */
    Optional<User> findByName(String nom);

    /**
     * Recherche un utilisateur par son identifiant.
     *
     * @param id l'identifiant de l'utilisateur à rechercher
     * @return l'utilisateur correspondant à l'identifiant, s'il existe
     */
    Optional<User> findById(String id);

    /**
     * Vérifie si un utilisateur existe avec le nom donné.
     *
     * @param name le nom de l'utilisateur à vérifier
     * @return true si un utilisateur avec le nom donné existe, sinon false
     */
    boolean existsByName(String name);

    /**
     * Supprime un utilisateur par son nom.
     *
     * @param username le nom de l'utilisateur à supprimer
     */
    void deleteByName(String username);
}
