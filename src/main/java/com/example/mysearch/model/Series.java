package com.example.mysearch.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Document(collection = "series")
public class Series {
    @Id
    private String id;

    @Field("title")
    private String titre;

    @Field("tfidf_vectors")
    private Map<String, Double> vecteursTFIDF = new HashMap<>();

    private String imageName;
    private int likes;
    private int dislikes;
    private int views;
    private Map<String, List<String>> similarSeriesCache = new HashMap<>();

    public Series() {
    }

    public Series(String id, String titre, Map<String, Double> vecteursTFIDF) {
        this.id = id;
        this.titre = titre;
        this.vecteursTFIDF = vecteursTFIDF;
    }

    @Override
    public String toString() {
        return "Serie{" +
                "id='" + id + '\'' +
                ", titre='" + titre + '\'' +
                ", vecteursTFIDF=" + vecteursTFIDF +
                ", imageName='" + imageName + '\'' +
                ", likes=" + likes +
                ", dislikes=" + dislikes +
                ", views=" + views +
                ", similarSeriesCache=" + similarSeriesCache +
                '}';
    }
}
