package com.example.mysearch.controller;

import com.example.mysearch.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.ObjectProvider;

import java.net.BindException;
import java.net.http.HttpRequest;

/**
 * Cette classe est le contrôleur pour l'authentification des utilisateurs.
 * Elle gère les requêtes liées à l'authentification, la déconnexion et la suppression de compte.
 */
@Controller
public class AuthController {
    private final ObjectProvider<AuthenticationManager> authenticationManagerProvider;

    /**
     * Construit un nouveau AuthController avec le fournisseur de gestionnaire d'authentification spécifié.
     * 
     * @param authenticationManagerProvider le fournisseur pour le gestionnaire d'authentification
     */
    @Autowired
    public AuthController(ObjectProvider<AuthenticationManager> authenticationManagerProvider) {
        this.authenticationManagerProvider = authenticationManagerProvider;
    }

    @Autowired
    private UserService userService;

    /**
     * Retourne la page de connexion.
     * 
     * @return la page de connexion
     */
    @GetMapping("/login")
    public String getLoginPage() {
        return "login";
    }

    /**
     * Gère la requête de connexion.
     * 
     * @param username le nom d'utilisateur
     * @param password le mot de passe
     * @param request  la requête HTTP servlet
     * @return la page de redirection
     */
    @PostMapping("/login")
    public String handleLogin(@RequestParam String username, @RequestParam String password, HttpServletRequest request) {
        try {
            AuthenticationManager authManager = authenticationManagerProvider.getObject();
            Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

            return "redirect:/";
        } catch (AuthenticationException e) {
            return "/login";
        }
    }

    /**
     * Déconnecte l'utilisateur.
     * 
     * @param request        la requête HTTP servlet
     * @param response       la réponse HTTP servlet
     * @param authentication l'objet d'authentification
     * @return un message de succès
     */
    @GetMapping("/logout")
    @ResponseBody
    public String logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return "Déconnecté avec succès.";
    }

    /**
     * Supprime le compte utilisateur.
     * 
     * @param request        la requête HTTP servlet
     * @param response       la réponse HTTP servlet
     * @param authentication l'objet d'authentification
     * @return un message de succès
     */
    @GetMapping("/deleteAccount")
    @ResponseBody
    public String deleteAccount(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication != null) {
            String username = authentication.getName();
            userService.removeUserFromDatabase(username);
        }
        return "Compte supprimé avec succès.";
    }

    /**
     * Gère l'exception de liaison.
     * 
     * @param ex l'exception de liaison
     * @return une réponse avec un message d'erreur
     */
    @ExceptionHandler(value = { BindException.class, MethodArgumentNotValidException.class })
    public ResponseEntity<String> handleBindException(BindException ex) {
        // Gérer l'exception de liaison
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
