package com.example.mysearch.controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.mysearch.service.HistoryService;

@RestController
@RequestMapping("/api/history")
public class HistoryControler {
    private final HistoryService historyService;

    public HistoryControler(HistoryService historyService) {
        this.historyService = historyService;
    }
}
