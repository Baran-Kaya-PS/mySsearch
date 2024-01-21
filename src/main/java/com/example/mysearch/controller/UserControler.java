package com.example.mysearch.controller;

import com.example.mysearch.model.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.mysearch.service.UserService;

/**
 * Cette classe est le contrôleur pour les opérations liées aux utilisateurs.
 */
@RestController
@RequestMapping("/api/user")
public class UserControler {
    private final UserService userService;

    public UserControler(UserService userService) {
        this.userService = userService;
    }

    /**
     * Crée un nouvel utilisateur
     * @param user L'utilisateur à créer.
     * @return ResponseEntity contenant l'utilisateur créé et le code de statut HTTP 201 (Created).
     */
    @PostMapping("/create")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User savedUser = userService.createUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    /**
     * Récupère un utilisateur par son identifiant.
     *
     * @param id L'identifiant de l'utilisateur.
     * @return ResponseEntity contenant l'utilisateur récupéré et le code de statut HTTP 200 (OK) s'il existe,
     *         sinon le code de statut HTTP 404 (Not Found).
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return user != null ? ResponseEntity.ok(user): ResponseEntity.notFound().build();
    }

    /**
     * Récupère tous les utilisateurs.
     *
     * @return ResponseEntity contenant la liste des utilisateurs récupérés et le code de statut HTTP 200 (OK).
     */
    @GetMapping("/all")
    public ResponseEntity<Iterable<User>> getAllUsers() {
        Iterable<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Met à jour un utilisateur existant.
     *
     * @param id   L'identifiant de l'utilisateur à mettre à jour.
     * @param user Les nouvelles informations de l'utilisateur.
     * @return ResponseEntity contenant l'utilisateur mis à jour et le code de statut HTTP 200 (OK).
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        User updatedUser = userService.updateUser(id, user);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    /**
     * Supprime un utilisateur.
     *
     * @param id L'identifiant de l'utilisateur à supprimer.
     * @return ResponseEntity avec le code de statut HTTP 204 (No Content).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
