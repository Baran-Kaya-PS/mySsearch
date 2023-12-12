package com.example.mysearch.model;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Document(collection = "series")
public class Serie {
    @Id
    private String id;

    @Field("title")
    private String titre;
    @Field("tfidf_vectors")
    private Map<String, Double> vecteursTFIDF;

    public Serie() {

    }

    public Serie(String id, String titre,TreeMap<String, Double> vecteursTFIDF) {
        this.id = id;
        this.titre = titre;
        this.vecteursTFIDF = vecteursTFIDF;
    }

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

    public void setVecteursTFIDF(TreeMap<String, Double> vecteursTFIDF) {
        this.vecteursTFIDF = vecteursTFIDF;
    }

    @Override
    public String toString() {
        return "Serie{" +
                "id='" + id + '\'' +
                ", titre='" + titre + '\'' +
                ", vecteursTFIDF='" + vecteursTFIDF + '\'' +
                '}';
    }
}
