package com.example.mysearch.controller;

import com.example.mysearch.model.User;
import com.example.mysearch.service.SignupService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.net.URI;

/**
 * Cette classe est le contrôleur responsable de la gestion des inscriptions des utilisateurs.
 */
@Controller
public class SignupController {
    @Autowired
    private SignupService signupService;

    /**
     * Affiche le formulaire d'inscription.
     *
     * @return le nom de la page d'inscription
     */
    @GetMapping("/inscription")
    public String inscriptionForm() {
        return "signup"; // retourne la page d'inscription
    }

    /**
     * Traite la soumission du formulaire d'inscription.
     *
     * @param user           l'utilisateur à inscrire
     * @param bindingResult  le résultat de la validation des champs du formulaire
     * @param response       la réponse HTTP
     * @return le modèle et la vue correspondant à la redirection après l'inscription
     */
    @PostMapping("/inscription")
    public ModelAndView inscriptionSubmit(@Valid User user, BindingResult bindingResult, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView();

        if (signupService.userExists(user.getName())) {
            bindingResult.rejectValue("name", "error.user", "Ce nom d'utilisateur existe déjà");
            response.setStatus(HttpStatus.BAD_REQUEST.value()); // Définit le statut HTTP ici.
            modelAndView.addObject("user", user);
            modelAndView.setViewName("signup");
            return modelAndView;
        }

        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("signup");
            modelAndView.addObject("user", user);
            return modelAndView;
        }

        signupService.enregistrer(user.getName(), user.getEmail(), user.getPassword());
        modelAndView.setViewName("/login");
        return modelAndView;
    }
}
