package com.example.mysearch.controller;

import com.example.mysearch.model.Evaluation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.mysearch.service.EvaluationService;
@RestController
@RequestMapping("/api/evaluation")

public class EvaluationControler {
    private final EvaluationService evaluationService;

    public EvaluationControler(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @GetMapping("/all")
    public ResponseEntity<Iterable<Evaluation>> getAllEvaluations() {
        Iterable<Evaluation> evaluations = evaluationService.getAllEvaluations();
        return ResponseEntity.ok(evaluations);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Evaluation> getEvaluationById(@PathVariable Long id) {
        Evaluation evaluation = evaluationService.getEvaluationById(id);
        return evaluation != null ? ResponseEntity.ok(evaluation): ResponseEntity.notFound().build();
    }
    @PostMapping("/create")
    public ResponseEntity<Evaluation> createEvaluation(@Valid @RequestBody Evaluation evaluation) {
        Evaluation savedEvaluation = evaluationService.createEvaluation(evaluation);
        return new ResponseEntity<>(savedEvaluation, HttpStatus.CREATED);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Evaluation> updateEvaluation(@PathVariable Long id, @Valid @RequestBody Evaluation evaluation) {
        Evaluation updatedEvaluation = evaluationService.updateEvaluation(id, evaluation);
        return new ResponseEntity<>(updatedEvaluation, HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Evaluation> deleteEvaluation(@PathVariable Long id) {
        evaluationService.deleteEvaluation(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
