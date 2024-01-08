package com.example.mysearch.security;

import com.example.mysearch.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> {
                    csrf.csrfTokenRepository(customCsrfTokenRepository());
                })
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/inscription", "/login", "/accueil", "/recherche", "/compte","/").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login") // URL de la page de connexion
                        .loginProcessingUrl("/login") // URL de traitement du formulaire de connexion
                        .permitAll() // Accès à tous les utilisateurs
                        .defaultSuccessUrl("/accueil", true) // Redirection après connexion réussie
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/connexion?logout")
                        .permitAll()
                );
        return http.build();
    }

    private CsrfTokenRepository customCsrfTokenRepository() {
        return new HttpSessionCsrfTokenRepository(); // Exemple avec HttpSessionCsrfTokenRepository
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth, UserService userService, PasswordEncoder passwordEncoder) throws Exception {
        auth
                .userDetailsService(userService)
                .passwordEncoder(passwordEncoder);
    }
}
