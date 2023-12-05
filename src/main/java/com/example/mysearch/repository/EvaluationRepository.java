package com.example.mysearch.repository;
import com.example.mysearch.model.Evaluation;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
public interface EvaluationRepository extends MongoRepository<Evaluation, String>{
    List<Evaluation> findByUtilisateurId(String utilisateurId);
    List<Evaluation> findBySerieId(String serieId);
}
