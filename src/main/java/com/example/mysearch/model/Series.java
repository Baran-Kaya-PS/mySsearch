package com.example.mysearch.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.util.HashMap;
import java.util.Map;

@Document(collection = "series")
public class Series {
    @Setter
    @Getter
    @Id
    private String id;

    @Setter
    @Getter
    @Field("title")
    private String titre;

    @Setter
    @Getter
    @Field("tfidf_vectors")
    private Map<String, Double> vecteursTFIDF = new HashMap<>();



    public Series() {
    }

    public Series(String id, String titre, Map<String, Double> vecteursTFIDF) {
        this.id = id;
        this.titre = titre;
        this.vecteursTFIDF = vecteursTFIDF;
    }


    private String imageName;


    @Override
    public String toString() {
        return "Serie{" +
                "id='" + id + '\'' +
                ", titre='" + titre + '\'' +
                ", vecteursTFIDF=" + vecteursTFIDF +
                '}';
    }
}
