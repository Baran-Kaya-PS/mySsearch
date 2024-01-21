package com.example.mysearch.repository;

/**
 * Cette interface représente un référentiel personnalisé pour les utilisateurs.
 * Elle fournit des méthodes pour ajouter une recherche à l'historique d'un utilisateur.
 */
public interface UserRepositoryCustom {
    /**
     * Ajoute une recherche à l'historique d'un utilisateur.
     *
     * @param userId     l'identifiant de l'utilisateur
     * @param searchTerm le terme de recherche à ajouter à l'historique
     */
    void addSearchToHistory(String userId, String searchTerm);
}