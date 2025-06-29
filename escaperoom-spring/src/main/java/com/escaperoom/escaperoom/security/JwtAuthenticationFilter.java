package com.escaperoom.escaperoom.security;

import java.io.IOException; // Pour gérer les exceptions d'entrée/sortie

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.escaperoom.escaperoom.service.JWTService;
import com.escaperoom.escaperoom.service.UserService;

import org.apache.commons.lang3.StringUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

@Component // Permet à Spring de détecter automatiquement ce filtre
@RequiredArgsConstructor // Génère un constructeur injectant les services JWTService et UserService
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JWTService jwtService; // Service pour gérer les JWT
	private final UserService userService; // Service pour charger les utilisateurs

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		final String authHeader = request.getHeader("Authorization"); // Récupère l'en-tête Authorization
		final String jwt;
		final String username;

		// Vérifie si l'en-tête est vide ou ne commence pas par "Bearer "
		if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, "Bearer ")) {
			filterChain.doFilter(request, response); // Poursuit la chaîne sans authentification
			return;
		}

		jwt = authHeader.substring(7); // Extrait le JWT en retirant "Bearer "
		username = jwtService.extractUserName(jwt); // Extrait le nom d’utilisateur depuis le JWT

		// Vérifie que le token est bien présent, que l'utilisateur n'est pas encore authentifié
		if (StringUtils.isNotEmpty(username) && SecurityContextHolder.getContext().getAuthentication() == null) {

			UserDetails userDetails = userService.loadUserByUsername(username); // Charge l’utilisateur

			// Vérifie si le token est valide pour cet utilisateur
			if (jwtService.isTokenValid(jwt, userDetails)) {

				SecurityContext securityContext = SecurityContextHolder.createEmptyContext(); // Crée un contexte de sécurité vide

				// Crée un token d’authentification contenant l’utilisateur et ses rôles
				UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

				token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // Ajoute des détails sur la requête
				
				// Définit le token dans le contexte																			
				securityContext.setAuthentication(token);
				// Met à jour le contexte de sécurité global
				SecurityContextHolder.setContext(securityContext);
			}
		}

		filterChain.doFilter(request, response); // Continue la chaîne de filtres avec ou sans authentification
	}
}
