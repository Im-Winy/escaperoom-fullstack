package com.escaperoom.escaperoom.security;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service // Déclare cette classe comme un service Spring
public class LoginAttemptService {

	private static final int MAX_ATTEMPT = 5; // Nombre maximal de tentatives autorisées
	private final LoadingCache<String, Integer> attemptsCache;

	public LoginAttemptService() {
		// Initialise un cache qui expire les entrées après 15 minutes
		this.attemptsCache = CacheBuilder.newBuilder().expireAfterWrite(15, TimeUnit.MINUTES)
				.build(new CacheLoader<>() {
					public Integer load(String key) {
						// Par défaut, un utilisateur a 0 tentative
						return 0;
					}
				});
	}

	// Incrémente le nombre de tentatives de connexion échouées pour un utilisateur
	public void loginFailed(String key) {
		int attempts = 0;
		try {
			attempts = attemptsCache.get(key);
		} catch (ExecutionException e) {
			// Ignoré : le cache retournera 0 via le loader par défaut
		}
		attempts++;
		attemptsCache.put(key, attempts);
	}

	// Réinitialise les tentatives après une connexion réussie
	public void loginSucceeded(String key) {
		attemptsCache.invalidate(key);
	}

	// Vérifie si un utilisateur est bloqué (trop de tentatives)
	public boolean isBlocked(String key) {
		try {
			return attemptsCache.get(key) >= MAX_ATTEMPT;
		} catch (ExecutionException e) {
			// Si une erreur survient, on considère que l'utilisateur n'est pas bloqué
			return false;
		}
	}
}
