package com.example.mysearch.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Représente la similarité entre deux séries.
 * Utilisée pour le calcul de similarité entre deux séries.
 * Les séries les plus similaires par séries ont été stockées en cache dans la collection series.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "series_similarities")
public class SeriesSimilarity {
    @Id
    private String id;
    private String series1Id;
    private String series2Id;
    private double similarity;
}