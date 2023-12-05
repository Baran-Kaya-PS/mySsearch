package com.example.mysearch.model;

import org.springframework.data.mongodb.core.mapping.Document;

public class Evaluation {
    private String id;
    private String utilisateurId;
    private String serieId;
    private boolean like;

    public Evaluation(){}
    public Evaluation(String id, String utilisateurId, String serieId, boolean like) {
        this.serieId = serieId;
        this.like = like;
    }
    public String getSerieId() {
        return serieId;
    }

    public void setSerieId(String serieId) {
        this.serieId = serieId;
    }

    public boolean getLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
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
