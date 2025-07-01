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
@ActiveProfiles("test")
@Transactional
class ReservationServiceIntegrationTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private IReservationRepository reservationRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IEvenementRepository evenementRepository;

    @Autowired
    private ITimeSlotRepository timeSlotRepository;

    @Test
    void testReserveIntegration_Success() {
        // Vider les anciennes réservations
        reservationRepository.deleteAll();

        // Récupération des données déjà présentes en base via le script SQL
        User user = userRepository.findById(1L).orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        Evenement evenement = evenementRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));
        TimeSlot timeSlot = timeSlotRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Créneau horaire non trouvé"));

        // Appel du service réel
        Reservation reservation = reservationService.reserve(timeSlot.getIdTimeSlot(), user.getIdUser(),
                evenement.getIdEvenement());

        // Assertions
        assertNotNull(reservation.getIdReservation());
        assertEquals(user.getIdUser(), reservation.getUser().getIdUser());
        assertEquals(evenement.getIdEvenement(), reservation.getEvenement().getIdEvenement());
        assertEquals(timeSlot.getIdTimeSlot(), reservation.getTimeSlot().getIdTimeSlot());
        assertEquals(new BigDecimal("80.00"), reservation.getMontantHT().setScale(2));
        assertEquals(new BigDecimal("16.00"), reservation.getMontantTVA().setScale(2)); // TVA à 20%
        assertEquals(new BigDecimal("96.00"), reservation.getMontant().setScale(2));

    }
}
