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
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

/**
 * Cette classe représente la configuration de sécurité de l'application.
 * Elle est responsable de la définition des règles de sécurité, de la gestion de l'authentification
 * et de l'autorisation des requêtes HTTP.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    /**
     * Cette méthode configure le filtre de sécurité pour les requêtes HTTP.
     * Elle définit les règles d'autorisation et de gestion de l'authentification.
     * 
     * @param http L'objet HttpSecurity utilisé pour configurer le filtre de sécurité.
     * @return Le SecurityFilterChain configuré.
     * @throws Exception Si une exception survient lors de la configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Désactiver CSRF ici
        http
                //.csrf(csrf -> {
                //    csrf.csrfTokenRepository(customCsrfTokenRepository());
                //})
                .csrf(AbstractHttpConfigurer::disable) // nous avons fais le choix de le désactiver car il nous posait des problèmes nottament lors de la connexion
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/inscription", "/login", "/recherche", "/compte","/","/api/serie/search","/image/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login") // URL de la page de connexion
                        .loginProcessingUrl("/perform_login") // URL de traitement du formulaire de connexion
                        .permitAll() // Accès à tous les utilisateurs
                        .defaultSuccessUrl("/", true) // Redirection après connexion réussie
                        .failureUrl("/login?error=true")
                        .usernameParameter("username")
                        .passwordParameter("password")
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/connexion?logout")
                        .permitAll()
                );

        return http.build();
    }

    /**
     * Cette méthode retourne un objet CsrfTokenRepository personnalisé.
     * Dans cet exemple, elle retourne un HttpSessionCsrfTokenRepository.
     * 
     * @return L'objet CsrfTokenRepository personnalisé.
     */
    private CsrfTokenRepository customCsrfTokenRepository() {
        return new HttpSessionCsrfTokenRepository(); // Exemple avec HttpSessionCsrfTokenRepository
    }

    /**
     * Cette méthode retourne un objet AuthenticationManagerBean.
     * 
     * @param authenticationConfiguration L'objet AuthenticationConfiguration utilisé pour obtenir l'AuthenticationManager.
     * @return L'objet AuthenticationManagerBean.
     * @throws Exception Si une exception survient lors de la configuration.
     */
    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Cette méthode configure l'AuthenticationManager pour l'authentification des utilisateurs.
     * 
     * @param auth L'objet AuthenticationManagerBuilder utilisé pour configurer l'AuthenticationManager.
     * @param userService Le service UserService utilisé pour récupérer les informations des utilisateurs.
     * @param passwordEncoder Le PasswordEncoder utilisé pour encoder les mots de passe.
     * @throws Exception Si une exception survient lors de la configuration.
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth, UserService userService, PasswordEncoder passwordEncoder) throws Exception {
        // Configuration de l'AuthenticationManager pour l'authentification des utilisateurs
        auth
                .userDetailsService(userService)
                .passwordEncoder(passwordEncoder);
    }
}
