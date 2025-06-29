package com.escaperoom.escaperoom.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component // Composant Spring écoutant les événements d'authentification réussie
public class AuthenticationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        // Récupère le nom d'utilisateur après une connexion réussie
        String username = ((UserDetails) event.getAuthentication().getPrincipal()).getUsername();

        // Réinitialise le compteur de tentatives échouées
        loginAttemptService.loginSucceeded(username);
    }
}
