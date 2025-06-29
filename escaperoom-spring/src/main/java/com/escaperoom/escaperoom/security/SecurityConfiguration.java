package com.escaperoom.escaperoom.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import java.util.List;

import com.escaperoom.escaperoom.entity.Role;
import com.escaperoom.escaperoom.service.UserService;

@Configuration // Indique que cette classe contient des beans de configuration Spring
@EnableWebSecurity // Active la configuration de sécurité Web de Spring Security
public class SecurityConfiguration {

    // Injection des composants nécessaires à la sécurité
    @Autowired JwtAuthenticationFilter jwtAuthenticationFilter;
    @Autowired UserService userService;
    @Autowired LoginAttemptService loginAttemptService;
    @Autowired JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    // Configuration CORS : permet d'autoriser les requêtes cross-origin
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200", "http://localhost")); // Origine autorisée (frontend Angular)
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Méthodes HTTP autorisées
        configuration.setAllowedHeaders(List.of("*")); // Tous les headers autorisés
        configuration.setExposedHeaders(List.of("Authorization", "Jwt-Token", "Access-Control-Allow-Origin")); // Headers visibles côté client
        configuration.setAllowCredentials(true); // Autorise l'envoi de cookies et d'authentification

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Applique la config à toutes les routes
        return source;
    }

    // Chaîne de filtres de sécurité
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(AbstractHttpConfigurer::disable) // Désactive la protection CSRF (inutile en stateless/API REST)
            .cors(Customizer.withDefaults()) // Active la configuration CORS définie ci-dessus
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(antMatcher("/api/auth/**")).permitAll() // Autorise l'accès aux routes d'authentification
                .requestMatchers(antMatcher("/api/admin")).hasAuthority(Role.ROLE_ADMIN.name()) // Seuls les admins peuvent accéder à /api/admin
                .requestMatchers(antMatcher("/api/user")).hasAuthority(Role.ROLE_USER.name()) // Seuls les utilisateurs peuvent accéder à /api/user
                .anyRequest().authenticated() // Toute autre requête nécessite une authentification
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Pas de session, on utilise JWT
            .authenticationProvider(authenticationProvider()) // Utilisation d'un provider personnalisé
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // Intercepte les requêtes avant l'authentification standard
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)); // Gestion des erreurs d'authentification

        return http.build();
    }

    // Fournisseur d'authentification personnalisé prenant en compte les tentatives de connexion
    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new LoginAttemptAuthenticationProvider(
                userService,
                loginAttemptService,
                passwordEncoder()
        );
    }

    // Encodeur de mots de passe (utilise BCrypt pour le hachage)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean qui expose l'AuthenticationManager utilisé dans le processus d'authentification
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
