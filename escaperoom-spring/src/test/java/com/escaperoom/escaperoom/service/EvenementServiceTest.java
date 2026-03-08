package com.escaperoom.escaperoom.service;

import com.escaperoom.escaperoom.entity.Difficulte;
import com.escaperoom.escaperoom.entity.Evenement;
import com.escaperoom.escaperoom.repository.IEvenementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EvenementServiceTest {

	private EvenementService evenementService;
	private IEvenementRepository evenementRepository;

	@BeforeEach
	void setUp() {
		// Création d’un mock du repository
		evenementRepository = mock(IEvenementRepository.class);

		// Initialisation du service avec le mock injecté manuellement
		evenementService = new EvenementService();
		evenementService.evenementRepository = evenementRepository;
	}

	/**
	 * Test de la méthode de sauvegarde avec un objet Evenement.
	 * Vérifie que le repository est bien appelé et que l’objet retourné est celui sauvegardé.
	 */
	@Test
	void testSaveEvenement() {
		Evenement evenement = new Evenement();
		when(evenementRepository.save(evenement)).thenReturn(evenement);

		Evenement saved = evenementService.saveEvenement(evenement);

		assertEquals(evenement, saved);
		verify(evenementRepository, times(1)).save(evenement);
	}

	/**
	 * Test de la méthode de sauvegarde en passant les paramètres un par un.
	 * Vérifie que l'objet est correctement construit et persisté.
	 */
	@Test
	void testSaveEvenement_WithParams() {
		when(evenementRepository.save(any(Evenement.class))).thenAnswer(invocation -> invocation.getArgument(0));

		Evenement saved = evenementService.saveEvenement("Nom", "Desc", "image.png", 60, 5, 100, "normal");

		assertEquals("Nom", saved.getNom());
		assertEquals("Desc", saved.getDescription());
		assertEquals("image.png", saved.getImage());
		assertEquals(60, saved.getDuree());
		assertEquals(5, saved.getNbeJoueurMax());
		assertEquals(100, saved.getPrix());
		assertEquals(Difficulte.NORMAL, saved.getDifficulte()); // Conversion correcte de la chaîne vers l'enum
		verify(evenementRepository).save(any(Evenement.class));
	}

	/**
	 * Test de récupération d’un événement par son identifiant.
	 * Vérifie que le bon événement est retourné et que le repository est bien appelé.
	 */
	@Test
	void testGetEvenementById() {
		Evenement evenement = new Evenement();
		evenement.setIdEvenement(1L);
		when(evenementRepository.findById(1L)).thenReturn(Optional.of(evenement));

		Evenement result = evenementService.getEvenementById(1L);

		assertEquals(1L, result.getIdEvenement());
		verify(evenementRepository).findById(1L);
	}

	/**
	 * Test de récupération de tous les événements.
	 * Vérifie que la liste retournée contient bien les éléments simulés.
	 */
	@Test
	void testGetEvenements() {
		List<Evenement> list = Arrays.asList(new Evenement(), new Evenement());
		when(evenementRepository.findAll()).thenReturn(list);

		List<Evenement> result = evenementService.getEvenements();

		assertEquals(2, result.size());
	}

	/**
	 * Test de suppression d’un événement en passant directement l’objet.
	 * Vérifie que la méthode delete du repository est bien appelée.
	 */
	@Test
	void testDeleteEvenement() {
		Evenement evenement = new Evenement();
		evenementService.deleteEvenement(evenement);

		verify(evenementRepository, times(1)).delete(evenement);
	}

	/**
	 * Test de suppression d’un événement par son identifiant.
	 * Vérifie l’appel au repository.
	 */
	@Test
	void testDeleteEvenementById() {
		evenementService.deleteEvenement(10L);
		verify(evenementRepository, times(1)).deleteById(10L);
	}

	/**
	 * Test de mise à jour d’un événement existant.
	 * Vérifie que les champs sont bien mis à jour et que l'objet est sauvegardé.
	 */
	@Test
	void testUpdateEvenement() {
		Evenement evenement = new Evenement();
		evenement.setIdEvenement(1L);
		evenement.setNom("Old");
		evenement.setDescription("OldDesc");
		evenement.setDifficulte(Difficulte.NORMAL);

		// Simule la présence de l'événement en base
		when(evenementRepository.findById(1L)).thenReturn(Optional.of(evenement));
		// Retourne l'objet passé à save
		when(evenementRepository.save(any(Evenement.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// Appel de la méthode à tester
		Evenement updated = evenementService.updateEvenement(1L, "New", "Desc", "img.png", 90, 6, 120, "intermediaire");

		// Vérifications des modifications
		assertEquals("New", updated.getNom());
		assertEquals("Desc", updated.getDescription());
		assertEquals("img.png", updated.getImage());
		assertEquals(90, updated.getDuree());
		assertEquals(6, updated.getNbeJoueurMax());
		assertEquals(120, updated.getPrix());
		assertEquals(Difficulte.INTERMEDIAIRE, updated.getDifficulte());
		verify(evenementRepository).save(evenement);
	}

	/**
	 * Test de la méthode alternative findById qui retourne un Optional.
	 */
	@Test
	void testFindById() {
		Evenement evenement = new Evenement();
		when(evenementRepository.findById(2L)).thenReturn(Optional.of(evenement));

		Optional<Evenement> result = evenementService.findById(2L);

		assertTrue(result.isPresent());
		verify(evenementRepository).findById(2L);
	}
}
