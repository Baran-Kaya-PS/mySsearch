package com.example.mysearch.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.util.HashMap;
import java.util.Map;

@Document(collection = "series")
public class Serie {
    @Id
    private String id;

    @Field("title")
    private String titre;

    @Field("tfidf_vectors")
    private Map<String, Double> vecteursTFIDF = new HashMap<>();

    // Constructeurs, getters et setters

    public Serie() {
        // Constructeur par défaut nécessaire pour Spring Data
    }

    public Serie(String id, String titre, Map<String, Double> vecteursTFIDF) {
        this.id = id;
        this.titre = titre;
        this.vecteursTFIDF = vecteursTFIDF;
    }

    // Getters et Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public Map<String, Double> getVecteursTFIDF() {
        return vecteursTFIDF;
    }

    public void setVecteursTFIDF(Map<String, Double> vecteursTFIDF) {
        this.vecteursTFIDF = vecteursTFIDF;
    }

    // Méthode toString pour un affichage facile des informations de la série

    @Override
    public String toString() {
        return "Serie{" +
                "id='" + id + '\'' +
                ", titre='" + titre + '\'' +
                ", vecteursTFIDF=" + vecteursTFIDF +
                '}';
    }

    public Map<String, Double> getTfidfVectors() {
        return this.vecteursTFIDF;
    }
}
