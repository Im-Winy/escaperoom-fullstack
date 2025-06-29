package com.escaperoom.escaperoom.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.escaperoom.escaperoom.entity.Difficulte;
import com.escaperoom.escaperoom.entity.Evenement;
import com.escaperoom.escaperoom.service.EvenementService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class EvenementController {

	@Autowired
	EvenementService evenementService;

	// Enregistre un évènement
	@PostMapping("/evenement")
	public ResponseEntity<Evenement> createEvenement(
			@RequestParam("nom") String nom,
			@RequestParam("description") String description, 
			@RequestParam("image") MultipartFile image,
			@RequestParam("duree") int duree, 
			@RequestParam("nbeJoueurMax") int nbeJoueurMax,
			@RequestParam("prix") int prix, 
			@RequestParam("difficulte") Difficulte difficulte) {

	    // Créer un nouvel événement
	    Evenement evenement = new Evenement(nom, description, image.getOriginalFilename(), duree, nbeJoueurMax, prix, difficulte);
	    Evenement savedEvenement = evenementService.saveEvenement(evenement);

	    return ResponseEntity.ok(savedEvenement);
	}

	// Récupère tout les évènements
	@GetMapping("/evenement/list")
	public List<Evenement> getAllEvenements(@Validated @RequestBody(required = false) Evenement evenement) {
		return evenementService.getEvenements();
	}

	// Récupère un évènement
	@GetMapping("/evenement/{id}")
	public Evenement getEvenementById(@PathVariable("id") Long idEvenement) {
		return evenementService.getEvenementById(idEvenement);
	}

	// Met à jour un évènement
	@PutMapping("/update/evenement/{id}")
	public ResponseEntity<Evenement> updateEvenement(
			@PathVariable("id") Long idEvenement,
			@RequestParam("nom") String nom, 
			@RequestParam("description") String description,
			@RequestParam("image") String image, 
			@RequestParam("duree") int duree,
			@RequestParam("nbeJoueurMax") int nbeJoueurMax, 
			@RequestParam("prix") int prix,
			@RequestParam("difficulte") String difficulte) {
		Evenement updatedEvenement = evenementService.updateEvenement(idEvenement, nom, description, image, duree, nbeJoueurMax, prix, difficulte);
		return ResponseEntity.ok(updatedEvenement);
	}

	@DeleteMapping("delete/evenement/{id}")
	public ResponseEntity<Evenement> deleteEvenement(@PathVariable("id") Long id) {

		evenementService.deleteEvenement(id);
		return new ResponseEntity<>(HttpStatus.OK);

	}

}
