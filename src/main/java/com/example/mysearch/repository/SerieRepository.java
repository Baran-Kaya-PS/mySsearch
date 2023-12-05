package com.example.mysearch.repository;
import com.example.mysearch.model.Serie;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
public interface SerieRepository extends MongoRepository<Serie, String>{
    List<Serie> findByTitre(String titre);
    List<Serie> findByGenre(String genre);
}
