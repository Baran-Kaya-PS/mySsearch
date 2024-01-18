package com.example.mysearch.repository;

import com.example.mysearch.model.SeriesSimilarity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SeriesSimilarityRepository extends MongoRepository<SeriesSimilarity, String> {
}
