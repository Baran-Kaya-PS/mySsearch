package com.example.mysearch.controller;

import com.example.mysearch.model.History;
import com.example.mysearch.model.Series;
import com.example.mysearch.model.User;
import com.example.mysearch.service.HistoryService;
import com.example.mysearch.service.SerieService;
import com.example.mysearch.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur pour gérer les requêtes liées à l'historique.
 */
@Controller
@RequestMapping("/api/history")
public class HistoryControler {
    private final HistoryService historyService;
    @Autowired
    private UserService userService;

    @Autowired
    private SerieService serieService;
    
    /**
     * Constructeur de la classe HistoryControler.
     * @param historyService le service d'historique
     */
    public HistoryControler(HistoryService historyService) {
        this.historyService = historyService;
    }

    /**
     * Récupère tous les enregistrements d'historique.
     * @return la liste des enregistrements d'historique
     */
    @GetMapping("/")
    public List<History> getAllHistoryRecords() {
        return historyService.getAllHistoryRecords();
    }

    /**
     * Récupère un enregistrement d'historique par son identifiant.
     * @param id l'identifiant de l'enregistrement d'historique
     * @return l'enregistrement d'historique correspondant à l'identifiant
     */
    @GetMapping("/{id}")
    public History getHistoryRecordById(@PathVariable String id) {
        return historyService.getHistoryByUserId(id);
    }

    /**
     * Crée un nouvel enregistrement d'historique.
     * @param historyRecord l'enregistrement d'historique à créer
     * @return l'enregistrement d'historique créé
     */
    @PostMapping("/")
    public History createHistoryRecord(@RequestBody History historyRecord) {
        return historyService.createHistoryRecord(historyRecord);
    }

    /**
     * Met à jour un enregistrement d'historique existant.
     * @param id l'identifiant de l'enregistrement d'historique à mettre à jour
     * @param historyRecord l'enregistrement d'historique mis à jour
     * @return l'enregistrement d'historique mis à jour
     */
    @PutMapping("/{id}")
    public History updateHistoryRecord(@PathVariable String id, @RequestBody History historyRecord) {
        return historyService.updateHistoryRecord(id, historyRecord);
    }

    /**
     * Supprime un enregistrement d'historique.
     * @param id l'identifiant de l'enregistrement d'historique à supprimer
     */
    @DeleteMapping("/{id}")
    public void deleteHistoryRecord(@PathVariable String id) {
        historyService.deleteHistoryRecord(id);
    }

    /**
     * Récupère l'historique de l'utilisateur actuel.
     * @param model le modèle pour la vue
     * @return la vue "history"
     */
    @GetMapping("/user")
    public String getCurrentUserHistory(Model model) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        } else {
            username = principal.toString();
        }

        User user = userService.getUserByUsername(username);
        String userId = user.getId();

        History history = historyService.getUserHistory(userId);
        Map<String, String> seriesNames = new HashMap<>();
        for (String seriesId : history.getClickCount().keySet()) {
            Series series = serieService.getSerieById(seriesId);
            seriesNames.put(seriesId, series.getTitre());
        }
        model.addAttribute("history", history);
        model.addAttribute("seriesNames", seriesNames);
        return "history";
    }
}