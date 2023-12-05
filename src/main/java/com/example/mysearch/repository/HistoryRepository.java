package com.example.mysearch.repository;
import com.example.mysearch.model.History;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
public interface HistoryRepository extends MongoRepository<History, String>{
    List<History> findByUtilisateurId(String utilisateurId);
    List<History> findBySerieId(String serieId);
    List<History> findByRecherche(String recherche);
}
