package com.escaperoom.escaperoom.security;

import java.io.IOException;
import java.io.OutputStream;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.escaperoom.escaperoom.constant.SecurityConstant;
import com.escaperoom.escaperoom.entity.HttpResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component // Composant Spring pour gérer les accès non autorisés
public class JwtAuthenticationEntryPoint extends Http403ForbiddenEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
			throws IOException {

		// Crée une réponse personnalisée 403 avec un message clair
		HttpResponse httpResponse = new HttpResponse(
			HttpStatus.FORBIDDEN.value(),
			HttpStatus.FORBIDDEN,
			HttpStatus.FORBIDDEN.getReasonPhrase().toUpperCase(),
			SecurityConstant.FORBIDDEN_MESSAGE
		);

		// Configure la réponse HTTP en JSON
		response.setContentType(APPLICATION_JSON_VALUE);
		response.setStatus(FORBIDDEN.value());

		// Écrit la réponse dans le corps HTTP
		OutputStream outputStream = response.getOutputStream();
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(outputStream, httpResponse);
		outputStream.flush();
	}
}
