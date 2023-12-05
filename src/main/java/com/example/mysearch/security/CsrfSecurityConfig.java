package com.example.mysearch.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class CsrfSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> {
                    csrf.csrfTokenRepository(customCsrfTokenRepository()); // Utiliser un repository personnalisé
                    // Autres personnalisations CSRF...
                });
        // Autres configurations de sécurité...
        return http.build();
    }

    private CsrfTokenRepository customCsrfTokenRepository() {
        // Implémentez votre propre CsrfTokenRepository
        return new HttpSessionCsrfTokenRepository(); // Exemple avec HttpSessionCsrfTokenRepository
    }
}
