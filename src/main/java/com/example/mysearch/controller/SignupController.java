package com.example.mysearch.controller;

import com.example.mysearch.service.SignupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
@Controller
public class SignupController {
    @Autowired
    private SignupService signupService;

    @GetMapping("/inscription")
    public String inscriptionForm() {
        return "signup"; // retourne la page d'inscription
    }

    @PostMapping("/inscription")
    public String inscriptionSubmit(@RequestParam String username, @RequestParam String email, @RequestParam String password) {
        signupService.enregistrer(username, email, password);
        return "redirect:/connexion"; // redirige vers la page de connexion après l'inscription
    }
}
