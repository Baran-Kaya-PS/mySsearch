package com.example.mysearch.repository;

import com.example.mysearch.model.SeriesSimilarity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SeriesSimilarityRepository extends MongoRepository<SeriesSimilarity, String> {
    List<SeriesSimilarity> findBySeries1Id(String seriesId);
    List<SeriesSimilarity> findBySeries2Id(String seriesId);

    List<SeriesSimilarity> findBySeries1IdOrSeries2Id(java.lang.String seriesId, java.lang.String seriesId1);
}
