package com.example.mysearch.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

public class Evaluation {
    private String id;
    private String utilisateurId;
    @Setter
    @Getter
    private String serieId;
    @Setter
    private boolean like;

    public Evaluation(){}
    public Evaluation(String id, String utilisateurId, String serieId, boolean like) {
        this.serieId = serieId;
        this.like = like;
    }

    public boolean getLike() {
        return like;
    }

    public String toString(){
        return "Evaluation{" +
                "id='" + id + '\'' +
                ", utilisateurId='" + utilisateurId + '\'' +
                ", serieId='" + serieId + '\'' +
                ", note=" + like +
                '}';
    }
    public String toCSV(){
        return id + "," + utilisateurId + "," + serieId + "," + like;
    }
}
