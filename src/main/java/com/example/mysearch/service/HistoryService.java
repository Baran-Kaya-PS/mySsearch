package com.example.mysearch.service;

import com.example.mysearch.model.History;
import com.example.mysearch.repository.HistoryRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HistoryService {
    private final HistoryRepository historyRepository;

    public HistoryService(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    public History getHistoryByUserId(String userId) {
        return historyRepository.findByUtilisateurId(userId).orElse(new History(userId));
    }

    public void addSearchToHistory(String userId, String keyword) {
        History history = historyRepository.findByUtilisateurId(userId).orElse(new History(userId));
        history.getSearchCount().put(keyword, history.getSearchCount().getOrDefault(keyword, 0) + 1);
        historyRepository.save(history);
    }

    public void addSerieClick(String userId, String serieId) {
        History history = historyRepository.findByUtilisateurId(userId).orElse(new History(userId));
        history.getClickCount().put(serieId, history.getClickCount().getOrDefault(serieId, 0) + 1);
        historyRepository.save(history);
    }

    public void addSerieLike(String userId, String serieName) {
        History history = historyRepository.findByUtilisateurId(userId).orElse(new History(userId));
        if (!history.getSerieLike().contains(serieName)) {
            history.getSerieLike().add(serieName);
        }
        historyRepository.save(history);
    }

    public void addSerieDislike(String userId, String serieName) {
        History history = historyRepository.findByUtilisateurId(userId).orElse(new History(userId));
        if (!history.getSerieDislike().contains(serieName)) {
            history.getSerieDislike().add(serieName);
        }
        historyRepository.save(history);
    }

    public boolean hasUserClickedOnSerie(String id, String serieId) {
        History history = historyRepository.findByUtilisateurId(id).orElse(null);
        if (history == null) {
            return false;
        }

        return history.getClickCount().containsKey(serieId);
    }

    public List<History> getAllHistoryRecords() {
        return historyRepository.findAll();
    }

    public History createHistoryRecord(History historyRecord) {
        return historyRepository.save(historyRecord);
    }

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

    public void deleteHistoryRecord(String id) {
        historyRepository.deleteByUtilisateurId(id);
    }
}