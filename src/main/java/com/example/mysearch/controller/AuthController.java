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
import org.springframework.beans.factory.ObjectProvider;

@Controller
public class AuthController {
    private final ObjectProvider<AuthenticationManager> authenticationManagerProvider;
    @Autowired
    public AuthController(ObjectProvider<AuthenticationManager> authenticationManagerProvider) {
        this.authenticationManagerProvider = authenticationManagerProvider;
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(
            @RequestParam String username,
            @RequestParam String password,
            RedirectAttributes redirectAttributes) {

        try {
            AuthenticationManager authManager = authenticationManagerProvider.getObject();
            authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            return "redirect:/index";
        } catch (AuthenticationException e) {
            redirectAttributes.addFlashAttribute("error", "Invalid username or password");
            return "redirect:/login";
        }
    }
}
