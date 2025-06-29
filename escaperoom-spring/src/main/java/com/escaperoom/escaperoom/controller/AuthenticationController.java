package com.escaperoom.escaperoom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.escaperoom.escaperoom.constant.SecurityConstant;
import com.escaperoom.escaperoom.constant.UserImplConstant;
import com.escaperoom.escaperoom.entity.User;
import com.escaperoom.escaperoom.exception.EmailExistException;
import com.escaperoom.escaperoom.exception.ExceptionHandling;
import com.escaperoom.escaperoom.exception.UsernameExistException;
import com.escaperoom.escaperoom.service.UserService;
import com.escaperoom.escaperoom.service.impl.JWTServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin("*") // Autorise les requêtes Cross-Origin
public class AuthenticationController extends ExceptionHandling {

	@Autowired
	UserService userService;
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	JWTServiceImpl jwtToken;

	// Endpoint de connexion utilisateur
	@PostMapping("login")
	public ResponseEntity<User> login(@RequestBody User user) {
		// Authentifie l'utilisateur via son nom d'utilisateur et mot de passe
		authenticate(user.getUsername(), user.getPassword());
		
		// Récupère les données de l'utilisateur authentifié
		User loginUser = userService.findUserByUsername(user.getUsername());
		
		// Génère l'en-tête JWT pour la réponse
		HttpHeaders jwtHeaders = getJwtHeader(loginUser);

		// Retourne l'utilisateur avec le token JWT dans les headers
		return new ResponseEntity<>(loginUser, jwtHeaders, HttpStatus.OK);
	}

	// Endpoint d'inscription utilisateur
	@PostMapping("register")
	public ResponseEntity<User> register(@RequestBody User user) 
			throws UsernameExistException, EmailExistException {

		// Enregistre un nouvel utilisateur
		User newUser = userService.register(user.getPrenom(), user.getNom(), user.getUsername(), user.getEmail());
		
		if (newUser != null) {
			return new ResponseEntity<>(newUser, HttpStatus.OK);
		} else {
			// Lève une exception si le nom d'utilisateur existe déjà
			throw new UsernameExistException(UserImplConstant.USERNAME_ALREADY_EXIST);
		}
	}

	// Crée les en-têtes HTTP contenant le JWT
	private HttpHeaders getJwtHeader(User loginUser) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(SecurityConstant.JWT_TOKEN_HEADER, jwtToken.generateToken(loginUser));
		return headers;
	}

	// Méthode interne pour authentifier un utilisateur avec Spring Security
	private void authenticate(String username, String password) {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
	}

}
