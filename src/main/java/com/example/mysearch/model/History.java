package com.example.mysearch.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "historique")
public class History {
    @Id
    private String id;
    private String utilisateurId;
    @DateTimeFormat(pattern = "yyyy-MM-dd-HH-mm")
    private List<String> date;
    private List<String> recherche;
    private List<String> resultats;

    public History() {
        this.date = new ArrayList<>();
        this.recherche = new ArrayList<>();
        this.resultats = new ArrayList<>();
    }

    public History(String id, String utilisateurId, List<String> date, List<String> recherche, List<String> resultats) {
        this.id = id;
        this.utilisateurId = utilisateurId;
        this.date = date;
        this.recherche = recherche;
        this.resultats = resultats;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUtilisateurId() {
        return utilisateurId;
    }

    public void setUtilisateurId(String utilisateurId) {
        this.utilisateurId = utilisateurId;
    }

    public List<String> getDate() {
        return date;
    }

    public void setDate(List<String> date) {
        this.date = date;
    }

    public List<String> getRecherche() {
        return recherche;
    }

    public void setRecherche(List<String> recherche) {
        this.recherche = recherche;
    }

    public List<String> getResultats() {
        return resultats;
    }

    public void setResultats(List<String> resultats) {
        this.resultats = resultats;
    }

    @Override
    public String toString() {
        return "History{" +
                "id='" + id + '\'' +
                ", utilisateurId='" + utilisateurId + '\'' +
                ", date=" + date +
                ", recherche=" + recherche +
                ", resultats=" + resultats +
                '}';
    }

    public String toCSV() {
        return id + ';' + utilisateurId + ';' + date + ';' + recherche + ';' + resultats;
    }
}