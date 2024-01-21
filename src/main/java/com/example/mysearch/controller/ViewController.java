package com.example.mysearch.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Contrôleur pour les vues de l'application.
 */
@Controller
public class ViewController {

    /**
     * Renvoie la vue de la page d'accueil.
     *
     * @return le nom de la vue "index"
     */
    @GetMapping("/api/home")
    public String home() {
        return "index";
    }

    /**
     * Renvoie la vue des recommandations.
     *
     * @return le nom de la vue "recommandation"
     */
    @GetMapping("/api/recommendations")
    public String recommendations() {
        return "recommandation";
    }

    /**
     * Renvoie la vue des séries.
     *
     * @return le nom de la vue "serie"
     */
    @GetMapping("/api/series")
    public String series() {
        return "serie";
    }
}