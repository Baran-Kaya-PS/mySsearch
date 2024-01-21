package com.example.mysearch.service;

import com.example.mysearch.model.User;
import com.example.mysearch.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


/**
 * Service responsable de l'enregistrement des utilisateurs.
 */
@Service
public class SignupService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Enregistre un nouvel utilisateur avec les informations fournies.
     *
     * @param username le nom d'utilisateur
     * @param email l'adresse e-mail de l'utilisateur
     * @param password le mot de passe de l'utilisateur
     */
    public void enregistrer(String username, String email, String password) {
        System.out.println("Enregistrement utilisateur : " + username);
        User newUser = new User();
        newUser.setName(username);
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(password));
        userRepository.save(newUser);
    }

    /**
     * Vérifie si un utilisateur avec le nom donné existe déjà.
     *
     * @param name le nom d'utilisateur à vérifier
     * @return true si l'utilisateur existe, false sinon
     */
    public boolean userExists(String name) {
        return userRepository.existsByName(name);
    }
}
