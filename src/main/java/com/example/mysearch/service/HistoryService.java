package com.example.mysearch.service;

import com.example.mysearch.model.History;
import com.example.mysearch.repository.HistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

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
}
