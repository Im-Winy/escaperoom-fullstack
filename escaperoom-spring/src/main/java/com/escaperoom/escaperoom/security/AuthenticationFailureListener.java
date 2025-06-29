package com.escaperoom.escaperoom.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

@Component // Composant Spring écoutant les événements d'authentification échoué
public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        // Récupère le nom d'utilisateur ayant échoué à se connecter
        String username = (String) event.getAuthentication().getPrincipal();

        // Incrémente le compteur de tentatives échouées
        loginAttemptService.loginFailed(username);
    }
}
