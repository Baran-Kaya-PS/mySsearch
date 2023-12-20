package com.example.mysearch.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;
import static org.springframework.security.web.util.matcher.RegexRequestMatcher.regexMatcher;
import static org.springframework.http.HttpMethod.POST;
@Configuration
@EnableWebSecurity
@EnableWebMvc
public class SecurityConfig {
            @Bean
            SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/inscription", "/connexion").permitAll() // Accès public à l'inscription et à la connexion
                .requestMatchers("/accueil", "/recherche", "/compte").hasRole("USER") // Accès utilisateur à l'accueil, recherche, et compte
                .requestMatchers("/admin/**").hasRole("ADMIN") // Accès administrateur aux routes admin
                .anyRequest().authenticated() // Toutes les autres requêtes nécessitent une authentification
            )
            .httpBasic(Customizer.withDefaults()); // Exemple d'utilisation de l'authentification HTTP de base

        return http.build();
    }

            @Bean
            public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
            }
        }