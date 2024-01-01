package com.example.mysearch.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.mysearch.service.HistoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;
import com.example.mysearch.service.HistoryService;
import com.example.mysearch.model.History;

@RestController
@RequestMapping("/api/history")
public class HistoryControler {
    private final HistoryService historyService;

    public HistoryControler(HistoryService historyService) {
        this.historyService = historyService;
    }
    @GetMapping("/")
    public List<History> getAllHistoryRecords() {
        return historyService.getAllHistoryRecords();
    }
    
    // Add a method to get a specific history record by ID
    @GetMapping("/{id}")
    public History getHistoryRecordById(@PathVariable Long id) {
        return historyService.getHistoryRecordById(id);
    }
    
    // Add a method to create a new history record
    @PostMapping("/")
    public History createHistoryRecord(@RequestBody History historyRecord) {
        return historyService.createHistoryRecord(historyRecord);
    }
    
    // Add a method to update an existing history record
    @PutMapping("/{id}")
    public History updateHistoryRecord(@PathVariable Long id, @RequestBody History historyRecord) {
        return historyService.updateHistoryRecord(id, historyRecord);
    }
    
    // Add a method to delete a history record
    @DeleteMapping("/{id}")
    public void deleteHistoryRecord(@PathVariable Long id) {
        historyService.deleteHistoryRecord(id);
    }
}
