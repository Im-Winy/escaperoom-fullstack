package com.escaperoom.escaperoom.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.escaperoom.escaperoom.entity.Evenement;
import com.escaperoom.escaperoom.entity.Paiement;
import com.escaperoom.escaperoom.entity.PaymentRequest;
import com.escaperoom.escaperoom.entity.Reservation;
import com.escaperoom.escaperoom.entity.StatutPaiement;
import com.escaperoom.escaperoom.entity.TimeSlot;
import com.escaperoom.escaperoom.entity.User;
import com.escaperoom.escaperoom.repository.IPaiementRepository;
import com.escaperoom.escaperoom.repository.IReservationRepository;

@Service
public class PaiementService {

	@Autowired
	IPaiementRepository paiementRepository;

	@Autowired
	IReservationRepository reservationRepository;

	@Autowired
	EmailService emailService;

	public Paiement processPayment(PaymentRequest request, Long reservationId) {

	    // 1. Recherche de la réservation
	    Reservation reservation = reservationRepository.findById(reservationId)
	            .orElseThrow(() -> new RuntimeException("Réservation introuvable"));

	    // 2. Vérification du montant
	    if (request.getMontant() == null) {
	        throw new IllegalArgumentException("Le montant du paiement est manquant.");
	    }

	    BigDecimal montantAttendu = reservation.getMontant();
	    BigDecimal montantRecu = BigDecimal.valueOf(request.getMontant());

	    if (montantRecu.compareTo(montantAttendu) != 0) {
	        throw new IllegalArgumentException("Le montant ne correspond pas au montant attendu : " + montantAttendu);
	    }
	    
	    if (!request.getDateExppiration().matches("^(0[1-9]|1[0-2])/\\d{2}$")) {
	        throw new IllegalArgumentException("Format de date d'expiration invalide. Attendu : MM/AA");
	    }


	    // 3. Simulation d'approbation
	    boolean approved = !request.getNumeroCarteBancaire().equalsIgnoreCase("fail");

	    // 4. Création du paiement
	    Paiement paiement = new Paiement();
	    paiement.setAmount(request.getMontant());

	    // Masquage du numéro de carte (4 derniers chiffres)
	    String numeroCarte = request.getNumeroCarteBancaire();
	    String maskedCard = "XXXX-XXXX-XXXX-" + numeroCarte.substring(numeroCarte.length() - 4);
	    paiement.setPaymentToken(maskedCard);

	    paiement.setTransactionId(UUID.randomUUID().toString());
	    paiement.setStatut(approved ? StatutPaiement.APPROUVE : StatutPaiement.REFUSE);
	    paiement.setDatePaiement(LocalDateTime.now());

	    // 5. Sauvegarde
	    Paiement savedPaiement = paiementRepository.save(paiement);

	    // 6. Association à la réservation si approuvé
	    if (approved) {
	        reservation.setPaiement(savedPaiement);
	        reservationRepository.save(reservation);
	    }

	    // 7. Email de confirmation
	    Evenement evenement = reservation.getEvenement();
	    User utilisateur = reservation.getUser();
	    TimeSlot timeSlot = reservation.getTimeSlot();

	    String formattedDate = timeSlot.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
	    String formattedTime = timeSlot.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm"));

	    emailService.sendReservationConfirmation(
	            utilisateur.getEmail(),
	            utilisateur.getUsername(),
	            evenement.getNom(),
	            formattedDate,
	            formattedTime
	    );

	    // 8. Retour
	    return savedPaiement;
	}


}
