package com.example.mysearch.service;

import com.example.mysearch.model.History;
import com.example.mysearch.repository.HistoryRepository;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service de gestion de l'historique.
 */
@Service
public class HistoryService {
    private final HistoryRepository historyRepository;

    /**
     * Constructeur de la classe HistoryService.
     *
     * @param historyRepository le repository d'historique
     */
    public HistoryService(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    /**
     * Récupère l'historique d'un utilisateur en fonction de son identifiant.
     *
     * @param userId l'identifiant de l'utilisateur
     * @return l'historique de l'utilisateur, ou un nouvel historique si aucun n'est trouvé
     */
    public History getHistoryByUserId(String userId) {
        return historyRepository.findByUtilisateurId(userId).orElse(new History(userId));
    }

    /**
     * Ajoute une recherche à l'historique d'un utilisateur.
     *
     * @param userId  l'identifiant de l'utilisateur
     * @param keyword le mot-clé de la recherche
     */
    public void addSearchToHistory(String userId, String keyword) {
        History history = historyRepository.findByUtilisateurId(userId).orElse(new History(userId));
        history.getSearchCount().put(keyword, history.getSearchCount().getOrDefault(keyword, 0) + 1);
        historyRepository.save(history);
    }

    /**
     * Ajoute un clic sur une série à l'historique d'un utilisateur.
     *
     * @param userId   l'identifiant de l'utilisateur
     * @param serieId  l'identifiant de la série
     */
    public void addSerieClick(String userId, String serieId) {
        History history = historyRepository.findByUtilisateurId(userId).orElse(new History(userId));
        history.getClickCount().put(serieId, history.getClickCount().getOrDefault(serieId, 0) + 1);
        historyRepository.save(history);
    }

    /**
     * Ajoute une série aimée à l'historique d'un utilisateur.
     *
     * @param userId     l'identifiant de l'utilisateur
     * @param serieName  le nom de la série
     */
    public void addSerieLike(String userId, String serieName) {
        History history = historyRepository.findByUtilisateurId(userId).orElse(new History(userId));
        if (!history.getSerieLike().contains(serieName)) {
            history.getSerieLike().add(serieName);
        }
        historyRepository.save(history);
    }

    /**
     * Ajoute une série détestée à l'historique d'un utilisateur.
     *
     * @param userId     l'identifiant de l'utilisateur
     * @param serieName  le nom de la série
     */
    public void addSerieDislike(String userId, String serieName) {
        History history = historyRepository.findByUtilisateurId(userId).orElse(new History(userId));
        if (!history.getSerieDislike().contains(serieName)) {
            history.getSerieDislike().add(serieName);
        }
        historyRepository.save(history);
    }

    /**
     * Vérifie si un utilisateur a cliqué sur une série.
     *
     * @param id       l'identifiant de l'utilisateur
     * @param serieId  l'identifiant de la série
     * @return true si l'utilisateur a cliqué sur la série, sinon false
     */
    public boolean hasUserClickedOnSerie(String id, String serieId) {
        History history = historyRepository.findByUtilisateurId(id).orElse(null);
        if (history == null) {
            return false;
        }

        return history.getClickCount().containsKey(serieId);
    }

    /**
     * Récupère tous les enregistrements d'historique.
     *
     * @return la liste des enregistrements d'historique
     */
    public List<History> getAllHistoryRecords() {
        return historyRepository.findAll();
    }

    /**
     * Crée un nouvel enregistrement d'historique.
     *
     * @param historyRecord l'enregistrement d'historique à créer
     * @return l'enregistrement d'historique créé
     */
    public History createHistoryRecord(History historyRecord) {
        return historyRepository.save(historyRecord);
    }

    /**
     * Met à jour un enregistrement d'historique existant.
     *
     * @param id             l'identifiant de l'utilisateur
     * @param historyRecord  l'enregistrement d'historique mis à jour
     * @return l'enregistrement d'historique mis à jour, ou null si aucun enregistrement n'est trouvé
     */
    public History updateHistoryRecord(String id, History historyRecord) {
        History historyToUpdate = historyRepository.findByUtilisateurId(id).orElse(null);
        if (historyToUpdate != null) {
            historyToUpdate.setUtilisateurId(historyRecord.getUtilisateurId());
            historyToUpdate.setSearchCount(historyRecord.getSearchCount());
            historyToUpdate.setClickCount(historyRecord.getClickCount());
            historyToUpdate.setSerieLike(historyRecord.getSerieLike());
            historyToUpdate.setSerieDislike(historyRecord.getSerieDislike());
            return historyRepository.save(historyToUpdate);
        }
        return null;
    }

    /**
     * Récupère les séries regardées par un utilisateur.
     *
     * @param id l'identifiant de l'utilisateur
     * @return la liste des séries regardées par l'utilisateur
     */
    public List<String> getWatchedSeries(String id) {
        History history = historyRepository.findByUtilisateurId(id).orElse(null);
        if (history == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(history.getClickCount().keySet());
    }

    /**
     * Récupère l'historique d'un utilisateur.
     *
     * @param id l'identifiant de l'utilisateur
     * @return l'historique de l'utilisateur, ou null si aucun historique n'est trouvé
     */
    public History getUserHistory(String id) {
        return historyRepository.findByUtilisateurId(id).orElse(null);
    }

    /**
     * Supprime un enregistrement d'historique.
     *
     * @param id l'identifiant de l'utilisateur
     */
    public void deleteHistoryRecord(String id) {
        historyRepository.deleteByUtilisateurId(id);
    }
}