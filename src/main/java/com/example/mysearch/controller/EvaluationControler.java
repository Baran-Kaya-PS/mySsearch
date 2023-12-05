package com.example.mysearch.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.mysearch.service.EvaluationService;
@RestController
@RequestMapping("/api/evaluation")

public class EvaluationControler {
    private final EvaluationService evaluationService;

    public EvaluationControler(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }
}
