package com.example.mysearch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/connexion")
    public String getLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(
            @RequestParam String username,
            @RequestParam String password,
            RedirectAttributes redirectAttributes) {

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            return "redirect:/index";
        } catch (AuthenticationException e) {
            redirectAttributes.addFlashAttribute("error", "Invalid username or password");
            return "redirect:/connexion";
        }
    }
}
