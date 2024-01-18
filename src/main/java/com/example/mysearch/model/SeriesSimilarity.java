package com.example.mysearch.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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