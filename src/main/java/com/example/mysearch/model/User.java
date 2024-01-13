package com.example.mysearch.model;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
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
    @Field("viewed_series_post_search")
    private List<Evaluation> viewedSeriesPostSearch;
    @Field("evaluations")
    private Map<String, Boolean> evaluations;


    public User() {}

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setHistoriqueRecherches(List<String> historiqueRecherches) {
        this.historiqueRecherches = historiqueRecherches;
    }

    public void setViewedSeriesPostSearch(List<Evaluation> viewedSeriesPostSearch) {
        this.viewedSeriesPostSearch = viewedSeriesPostSearch;
    }

    public void setEvaluations(Map<String, Boolean> evaluations) {
        this.evaluations = evaluations;
    }

    public void addEvaluation(Evaluation evaluation) {
        if (this.evaluations == null) {
            this.evaluations = new HashMap<String, Boolean>();
        }
        this.evaluations.put(evaluation.getSerieId(), evaluation.isLiked());
    }

    public User(String name, String email, String password, List<String> historiqueRecherches, Map<String, Boolean> evaluations) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.historiqueRecherches = historiqueRecherches;
        this.evaluations = evaluations;
    }
}