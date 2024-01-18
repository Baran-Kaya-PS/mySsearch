package com.example.mysearch.controller;

import com.example.mysearch.model.History;
import com.example.mysearch.service.HistoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public History getHistoryRecordById(@PathVariable String id) {
        return historyService.getHistoryByUserId(id);
    }

    @PostMapping("/")
    public History createHistoryRecord(@RequestBody History historyRecord) {
        return historyService.createHistoryRecord(historyRecord);
    }

    @PutMapping("/{id}")
    public History updateHistoryRecord(@PathVariable String id, @RequestBody History historyRecord) {
        return historyService.updateHistoryRecord(id, historyRecord);
    }

    @DeleteMapping("/{id}")
    public void deleteHistoryRecord(@PathVariable String id) {
        historyService.deleteHistoryRecord(id);
    }
}