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

@Controller
public class SignupController {
    @Autowired
    private SignupService signupService;

    @GetMapping("/inscription")
    public String inscriptionForm() {
        return "signup"; // retourne la page d'inscription
    }

    @PostMapping("/inscription")
    public ModelAndView inscriptionSubmit(@Valid User user, BindingResult bindingResult, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView();
        if (signupService.userExists(user.getName())) {

            response.setStatus(HttpStatus.BAD_REQUEST.value());
            modelAndView.setViewName("signup");
            modelAndView.addObject("user", user);
            bindingResult.rejectValue("name", "error.user", "Ce nom d'utilisateur existe déjà"); // RejectValue -> erreur n°400
            return modelAndView;
        }
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("signup"); // Utilisez le nom de votre vue d'inscription
            modelAndView.addObject("user", user);
            return modelAndView;
        }
        signupService.enregistrer(user.getName(), user.getEmail(), user.getPassword());
        modelAndView.setViewName("redirect:/login");
        return modelAndView;
    }
}
