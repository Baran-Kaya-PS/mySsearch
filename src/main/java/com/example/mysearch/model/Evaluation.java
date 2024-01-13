package com.example.mysearch.model;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Evaluation {
    private String id;
    private String utilisateurId;
    private String serieId;
    private boolean like;

    public Evaluation() {}

    public Evaluation(String id, String utilisateurId, String serieId, boolean like) {
        this.id = id;
        this.utilisateurId = utilisateurId;
        this.serieId = serieId;
        this.like = like;
    }

    public String toString() {
        return "Evaluation{" +
                "id='" + id + '\'' +
                ", utilisateurId='" + utilisateurId + '\'' +
                ", serieId='" + serieId + '\'' +
                ", like=" + like +
                '}';
    }

    public String toCSV() {
        return id + "," + utilisateurId + "," + serieId + "," + like;
    }

    public Boolean isLiked() {
        return like;
    }
}
