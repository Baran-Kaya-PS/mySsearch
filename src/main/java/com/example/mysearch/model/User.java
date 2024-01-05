package com.example.mysearch.model;

// importation
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document; // importation de Document -> signifie que la classe est un document MongoDB
import org.springframework.data.mongodb.core.mapping.Field; // importation de Field -> signifie que le champ est un champ MongoDB
import jakarta.validation.constraints.NotBlank; // Fonction NotBlank -> signifie que le champ ne doit pas être vide
import jakarta.validation.constraints.Email; // Fonction Email -> signifie que le champ doit être un email

import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
public class User {
    @Id
    private String id;

    @NotBlank(message = "Nom obligatoire")
    private String name;

    @NotBlank(message = "Email obligatoire")
    @Email(message = "Email invalide")
    private String email;

    @NotBlank(message = "Mot de passe obligatoire")
    private String password;

    @Field("historique_recherches")
    private List<String> historiqueRecherches;

    private List<Evaluation> evaluations;


    public User() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String nom) {
        this.name = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getHistoriqueRecherches() { return historiqueRecherches; }

    public void setHistoriqueRecherches(List<String> historiqueRecherches) {this.historiqueRecherches = historiqueRecherches;}

    public List<Evaluation> getEvaluations() {
        return evaluations;
    }

    public void setEvaluations(List<Evaluation> evaluations) {
        this.evaluations = evaluations;
    }

    public void addEvaluation(Evaluation evaluation) {
        if (this.evaluations == null) {
            this.evaluations = new ArrayList<>();
        }
        this.evaluations.add(evaluation);
    }

    public User(String nom, String email, String motDePasse, List<String> historiqueRecherches, List<Evaluation> evaluations) {
        this.name = nom;
        this.email = email;
        this.password = motDePasse;
        this.historiqueRecherches = historiqueRecherches;
        this.evaluations = evaluations;
    }
}