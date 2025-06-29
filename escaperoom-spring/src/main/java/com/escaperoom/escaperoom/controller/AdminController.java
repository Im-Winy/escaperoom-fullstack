package com.escaperoom.escaperoom.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.escaperoom.escaperoom.entity.HttpResponse;
import com.escaperoom.escaperoom.entity.User;
import com.escaperoom.escaperoom.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin("*") // Autorise les requêtes Cross-Origin
public class AdminController {
	
	@Autowired
	UserService userService;
	
	// Endpoint de test pour vérifier l'accès admin
	@GetMapping
	public ResponseEntity<String> sayHello(){
		return ResponseEntity.ok("Hello Admin");
	}
	
	// Récupère la liste de tous les utilisateurs
	@GetMapping("list")
	public ResponseEntity<List<User>> getAllUsers() {
		List<User> user = userService.getUsers();
		return new ResponseEntity<>(user, HttpStatus.OK);
	}
	
	// Ajoute un nouvel utilisateur
	@PostMapping("add")
	public ResponseEntity<User> addNewUser(@RequestParam("prenom") String prenom, 
			@RequestParam("nom") String nom,
			@RequestParam("username") String username, 
			@RequestParam("password") String password,
			@RequestParam("email") String email, 
			@RequestParam("role") String role,
			@RequestParam("active") String active, 
			@RequestParam("isNotLocked") String isNotLocked) {
		User newUser = userService.addNewUser(prenom, nom, username, password, email, role,
				Boolean.parseBoolean(active), Boolean.parseBoolean(isNotLocked));
		return new ResponseEntity<>(newUser, HttpStatus.OK);
	}
	
	// Met à jour les informations d’un utilisateur existant
	@PutMapping("update/{id}")
	public ResponseEntity<User> updateUser(@PathVariable("id") long idUser, 
			@RequestPart("prenom") String prenom,
			@RequestPart("nom") String nom, 
			@RequestPart("username") String username,
			@RequestPart("email") String email, 
			@RequestPart("role") String role) {
		User updatedUser = userService.updateUser(idUser, prenom, nom, username, email, role);
		return ResponseEntity.ok(updatedUser);
	}
	
	// Supprime un utilisateur par son ID
	@DeleteMapping("delete/{id}")
	public ResponseEntity<HttpResponse> deleteUser(@PathVariable("id") long id) {
		userService.deleteUser(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
