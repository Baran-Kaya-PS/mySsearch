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

@Controller
public class AuthController {
    private final ObjectProvider<AuthenticationManager> authenticationManagerProvider;
    @Autowired
    public AuthController(ObjectProvider<AuthenticationManager> authenticationManagerProvider) {
        this.authenticationManagerProvider = authenticationManagerProvider;
    }
    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String getLoginPage() {
        return "login";
    }

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
            // Gérer les erreurs d'authentification
            return "/login";
        }
    }
    @GetMapping("/logout")
    @ResponseBody
    public String logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return "Logged out successfully.";
    }
    @GetMapping("/deleteAccount")
    @ResponseBody
    public String deleteAccount(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication != null){
            String username = authentication.getName();
            userService.removeUserFromDatabase(username);
        }
        return "Account deleted successfully.";
    }
    @ExceptionHandler(value = {BindException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<String> handleBindException(BindException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
