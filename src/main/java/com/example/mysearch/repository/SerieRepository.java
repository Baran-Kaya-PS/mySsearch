package com.example.mysearch.repository;
import com.example.mysearch.model.Series;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
public interface SerieRepository extends MongoRepository<Series, String>{
    List<Series> findByTitre(String titre);

    boolean existsByTitre(String name);
}
