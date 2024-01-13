package com.example.mysearch.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
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

    public User(String name, String email, String password, List<String> historiqueRecherches, List<Evaluation> viewedSeriesPostSearch, Map<String, Boolean> evaluations) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.historiqueRecherches = historiqueRecherches;
        this.viewedSeriesPostSearch = viewedSeriesPostSearch;
        this.evaluations = evaluations;
    }

    public void addEvaluation(Evaluation evaluation) {
        if (this.evaluations == null) {
            this.evaluations = new HashMap<String, Boolean>();
        }
        this.evaluations.put(evaluation.getSerieId(), evaluation.isLiked());
    }
}