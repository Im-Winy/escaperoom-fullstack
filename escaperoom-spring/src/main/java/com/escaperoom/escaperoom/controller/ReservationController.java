package com.escaperoom.escaperoom.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.escaperoom.escaperoom.entity.Reservation;
import com.escaperoom.escaperoom.entity.TimeSlot;
import com.escaperoom.escaperoom.entity.User;
import com.escaperoom.escaperoom.repository.ITimeSlotRepository;
import com.escaperoom.escaperoom.service.ReservationService;

@RestController
@RequestMapping("/api/auth/")
@CrossOrigin("*")
public class ReservationController {

	@Autowired
	ReservationService reservationService;

	@Autowired
	ITimeSlotRepository timeSlotRepository;

	// Réserve un créneau pour un utilisateur donné et un événement donné
	@PostMapping("/reservation/{idUser}/{idEvenement}")
	public ResponseEntity<?> reserve(@PathVariable Long idUser, @PathVariable Long idEvenement,
			@RequestParam Long timeSlotId) {
		try {
			// Appel du service pour effectuer la réservation
			Reservation reservation = reservationService.reserve(timeSlotId, idUser, idEvenement);
			return ResponseEntity.ok(reservation);
		} catch (IllegalStateException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@GetMapping("/evenement/{evenementId}/timeslots")
	public ResponseEntity<List<TimeSlot>> getAvailableTimeSlots(@PathVariable Long evenementId,
			@RequestParam String selectedDate) {

		// Convertir la chaîne en LocalDate
		LocalDate date = LocalDate.parse(selectedDate);

		try {
			List<TimeSlot> timeSlots = reservationService.getAvailableTimeSlotsForEvenement(evenementId, date);
			if (timeSlots.isEmpty()) {
				return ResponseEntity.noContent().build(); // Aucun créneau disponible
			}
			return ResponseEntity.ok(timeSlots); // Renvoie les créneaux horaires disponibles
		} catch (RuntimeException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Événement introuvable
		}
	}

	// Générer les créneaux pour une seule journée
	@PostMapping("/generer-creneaux-journee")
	public ResponseEntity<?> generateSlotsForDay(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

		Object result = reservationService.generateTimeSlotsForDay(date);

		if (result instanceof String) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
		} else {
			return ResponseEntity.ok(result);
		}
	}

	// Récupère toutes les réservations
	@GetMapping("/reservations")
	public List<Reservation> getAllUsers(@Validated @RequestBody(required = false) User user) {
		return reservationService.getReservation();
	}
	
	@GetMapping("/historique-reservations/user/{userId}")
	public List<Reservation> getReservationsByUserId(@PathVariable int userId) {
	    return reservationService.getReservationsByUserId(userId);
	}

	
	// Supprime une réservation
	@DeleteMapping("/{reservationId}/cancel")
    public ResponseEntity<String> cancelReservation(
            @PathVariable Long reservationId,
            @RequestParam Long userId) {
        return reservationService.cancelReservation(reservationId, userId);
    }

}
