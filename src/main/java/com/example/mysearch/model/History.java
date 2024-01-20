package com.example.mysearch.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.Data;

import java.util.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "historique")
public class History {
    @Id
    private String id;
    private String utilisateurId;
    @DateTimeFormat(pattern = "yyyy-MM-dd-HH-mm")
    private List<String> date;
    private Map<String, Integer> searchCount = new HashMap<>();
    private Map<String, Integer> clickCount = new HashMap<>();
    private String serieId;
    private List<String> serieDislike = new ArrayList<>();
    private List<String> serieLike = new ArrayList<>();
    public History(String utilisateurId) {
        this.utilisateurId = utilisateurId;
    }
}