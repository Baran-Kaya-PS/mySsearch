package com.example.mysearch.service;
import com.example.mysearch.repository.UserRepository;
import org.springframework.stereotype.Service;
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    // Ajoutez ici des méthodes pour ajouter, rechercher, mettre à jour, supprimer des utilisateurs
}
