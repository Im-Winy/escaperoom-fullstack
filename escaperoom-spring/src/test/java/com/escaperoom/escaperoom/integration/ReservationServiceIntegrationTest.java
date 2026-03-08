package com.escaperoom.escaperoom.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.escaperoom.escaperoom.entity.Evenement;
import com.escaperoom.escaperoom.entity.Reservation;
import com.escaperoom.escaperoom.entity.TimeSlot;
import com.escaperoom.escaperoom.entity.User;
import com.escaperoom.escaperoom.repository.IEvenementRepository;
import com.escaperoom.escaperoom.repository.IReservationRepository;
import com.escaperoom.escaperoom.repository.ITimeSlotRepository;
import com.escaperoom.escaperoom.repository.IUserRepository;
import com.escaperoom.escaperoom.service.ReservationService;

import jakarta.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("test") // Utilisation du profil "test" pour charger la configuration appropriée
@Transactional // Chaque test est transactionnel et rollbacké automatiquement après exécution
class ReservationServiceIntegrationTest {

    @Autowired
    private ReservationService reservationService; // Service à tester

    @Autowired
    private IReservationRepository reservationRepository; // Répository pour gérer les réservations en base

    @Autowired
    private IUserRepository userRepository; // Répository pour récupérer les utilisateurs

    @Autowired
    private IEvenementRepository evenementRepository; // Répository pour récupérer les événements

    @Autowired
    private ITimeSlotRepository timeSlotRepository; // Répository pour récupérer les créneaux horaires

    @Test
    void testReserveIntegration_Success() {
        // Suppression de toutes les réservations pour s'assurer d'un état propre avant le test
        reservationRepository.deleteAll();

        // Récupération d'un utilisateur existant en base (id=1) via les données de test
        User user = userRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Récupération d'un événement existant en base (id=1) via les données de test
        Evenement evenement = evenementRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));

        // Récupération d'un créneau horaire existant en base (id=1) via les données de test
        TimeSlot timeSlot = timeSlotRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Créneau horaire non trouvé"));

        // Appel du service réel pour effectuer la réservation
        Reservation reservation = reservationService.reserve(timeSlot.getIdTimeSlot(), user.getIdUser(),
                evenement.getIdEvenement());

        // Vérification que la réservation a bien un identifiant généré (persistée en base)
        assertNotNull(reservation.getIdReservation());

        // Vérification que la réservation est liée au bon utilisateur
        assertEquals(user.getIdUser(), reservation.getUser().getIdUser());

        // Vérification que la réservation est liée au bon événement
        assertEquals(evenement.getIdEvenement(), reservation.getEvenement().getIdEvenement());

        // Vérification que la réservation correspond bien au créneau horaire choisi
        assertEquals(timeSlot.getIdTimeSlot(), reservation.getTimeSlot().getIdTimeSlot());

        // Vérification du montant hors taxe (80.00 € attendu)
        assertEquals(new BigDecimal("80.00"), reservation.getMontantHT().setScale(2));

        // Vérification du montant de la TVA (20% de 80.00 = 16.00 €)
        assertEquals(new BigDecimal("16.00"), reservation.getMontantTVA().setScale(2));

        // Vérification du montant total (HT + TVA = 96.00 €)
        assertEquals(new BigDecimal("96.00"), reservation.getMontant().setScale(2));
    }
}
