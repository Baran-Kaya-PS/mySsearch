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
    

    @GetMapping("/{id}")
    public History getHistoryRecordById(@PathVariable Long id) {
        return historyService.getHistoryRecordById(id);
    }
    

    @PostMapping("/")
    public History createHistoryRecord(@RequestBody History historyRecord) {
        return historyService.createHistoryRecord(historyRecord);
    }
    

    @PutMapping("/{id}")
    public History updateHistoryRecord(@PathVariable Long id, @RequestBody History historyRecord) {
        return historyService.updateHistoryRecord(id, historyRecord);
    }
    

    @DeleteMapping("/{id}")
    public void deleteHistoryRecord(@PathVariable Long id) {
        historyService.deleteHistoryRecord(id);
    }
}
