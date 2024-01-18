package com.example.mysearch.service;

import com.example.mysearch.model.History;
import com.example.mysearch.repository.HistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Service
public class HistoryService {
    private final HistoryRepository historyRepository;
    public HistoryService(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    public List<History> getAllHistoryRecords() {
        return historyRepository.findAll();
    }

    public History getHistoryRecordById(Long id) {
        return historyRepository.findById(String.valueOf(id)).orElse(null);
    }

    public History createHistoryRecord(History history) {
        return historyRepository.save(history);
    }


    public History updateHistoryRecord(Long id, History updatedHistory) {
        History existingHistory = historyRepository.findById(String.valueOf(id)).orElse(null);
        if (existingHistory != null) {
            existingHistory.setDate(updatedHistory.getDate());
            existingHistory.setRecherche(updatedHistory.getRecherche());
            existingHistory.setResultats(updatedHistory.getResultats());
            return historyRepository.save(existingHistory);

        }
        return null;
    }

    public boolean deleteHistoryRecord(Long id) {
        History existingHistory = historyRepository.findById(String.valueOf(id)).orElse(null);
        if (existingHistory != null) {
            historyRepository.delete(existingHistory);
            return true;
        }
        return false;
    }
    // addSearchToHistory
    public History addSearchToHistory(String id, String search) {
        History existingHistory = historyRepository.findByUtilisateurId(id).orElse(null);
        if (existingHistory != null) {
            existingHistory.getRecherche().add(search);
            return historyRepository.save(existingHistory);
        }
        History newHistory = new History();
        newHistory.setUtilisateurId(id);
        newHistory.getRecherche().add(search);
        return historyRepository.save(newHistory);
    }
    public History addSerieClick(String id, String serieName) {
        Optional<History> historyOpt = historyRepository.findByUtilisateurId(id);
        History history;
        if (historyOpt.isPresent()) {
            history = historyOpt.get();
        } else {
            history = new History();
            history.setUtilisateurId(id);
        }

        if (history.getSerieClick() == null) {
            history.setSerieClick(new ArrayList<>());
        }

        history.getSerieClick().add(serieName);
        return historyRepository.save(history);
    }

    public History getHistoryByUserId(String userId) {
        return historyRepository.findByUtilisateurId(userId).orElse(null);
    }

    public void addSerieDislike(String userId, String serieId) {
        History history = historyRepository.findByUtilisateurId(userId).orElse(null);
        if (history == null) {
            history = new History();
            history.setUtilisateurId(userId);
            history.setSerieDislike(new ArrayList<>());
        }

        if (history.getSerieDislike() == null) {
            history.setSerieDislike(new ArrayList<>());
        }

        if (!history.getSerieDislike().contains(serieId)) {
            history.getSerieDislike().add(serieId);
            historyRepository.save(history);
        }
    }

    public void addSerieLike(String userId, String serieName) {
        History history = historyRepository.findByUtilisateurId(userId).orElse(null);
        if (history == null) {
            history = new History();
            history.setUtilisateurId(userId);
            history.setSerieLike(new ArrayList<>());
        }

        if (history.getSerieLike() == null) {
            history.setSerieLike(new ArrayList<>());
        }

        if (!history.getSerieLike().contains(serieName)) {
            history.getSerieLike().add(serieName);
            historyRepository.save(history);
        }
    }

    public boolean hasUserClickedOnSerie(String id, String serieId) {
        History history = historyRepository.findByUtilisateurId(id).orElse(null);
        if (history == null) {
            return false;
        }

        if (history.getSerieClick() == null) {
            return false;
        }

        return history.getSerieClick().contains(serieId);
    }
}
