package com.escaperoom.escaperoom.security;

import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.escaperoom.escaperoom.service.UserService;

public class LoginAttemptAuthenticationProvider implements AuthenticationProvider {

	private final UserService userService;
	private final LoginAttemptService loginAttemptService;
	private final PasswordEncoder passwordEncoder;

	public LoginAttemptAuthenticationProvider(UserService userService, LoginAttemptService loginAttemptService, PasswordEncoder passwordEncoder) {
		this.userService = userService;
		this.loginAttemptService = loginAttemptService;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = authentication.getName(); // Nom d'utilisateur fourni
		String presentedPassword = authentication.getCredentials().toString(); // Mot de passe fourni

		// Vérifie si l'utilisateur est bloqué à cause de trop de tentatives échouées
		if (loginAttemptService.isBlocked(username)) {
			throw new LockedException("Compte bloqué");
		}

		// Charge les détails de l'utilisateur depuis le service
		UserDetails userDetails = userService.loadUserByUsername(username);

		// Vérifie si le mot de passe est incorrect
		if (!passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
			loginAttemptService.loginFailed(username); // Enregistre un échec
			throw new BadCredentialsException("Mot de passe incorrect");
		}

		// Réinitialise les tentatives après succès
		loginAttemptService.loginSucceeded(username);

		// Authentifie l'utilisateur avec ses rôles
		return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		// Spécifie que ce provider supporte UsernamePasswordAuthenticationToken
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}
}
