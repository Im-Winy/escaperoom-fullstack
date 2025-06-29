package com.escaperoom.escaperoom.controller;

import com.escaperoom.escaperoom.entity.Paiement;
import com.escaperoom.escaperoom.entity.PaymentRequest;
import com.escaperoom.escaperoom.entity.StatutPaiement;
import com.escaperoom.escaperoom.repository.IPaiementRepository;
import com.escaperoom.escaperoom.service.PaiementService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class PaiementController {

    @Autowired
    private PaiementService paiementService;

    @Autowired
    private IPaiementRepository paiementRepository;

    // Processus de paiement
    @PostMapping("/payment")
    public ResponseEntity<Paiement> processPayment(
    		@RequestParam Long reservationId, 
            @RequestBody PaymentRequest paymentRequest) {
        
        // Traiter le paiement et lier avec la réservation
        Paiement paiement = paiementService.processPayment(paymentRequest, reservationId);

        // Retourner le paiement avec un statut HTTP 200
        return new ResponseEntity<>(paiement, HttpStatus.OK);
    }

    // Récupérer tous les paiements
    @GetMapping
    public ResponseEntity<List<Paiement>> getAllPayments() {
        return ResponseEntity.ok(paiementRepository.findAll());
    }

    // Filtrer par statut (APPROUVE / REFUSE)
    @GetMapping("/status/{statut}")
    public ResponseEntity<List<Paiement>> getPaymentsByStatus(@PathVariable String statut) {
        try {
            StatutPaiement statutEnum = StatutPaiement.valueOf(statut.toUpperCase());
            List<Paiement> paiements = paiementRepository.findByStatut(statutEnum);
            return ResponseEntity.ok(paiements);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build(); // statut invalide
        }
    }
}
