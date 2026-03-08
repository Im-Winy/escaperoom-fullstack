package com.escaperoom.escaperoom.service;

import com.escaperoom.escaperoom.entity.Evenement;
import com.escaperoom.escaperoom.entity.Reservation;
import com.escaperoom.escaperoom.entity.TimeSlot;
import com.escaperoom.escaperoom.entity.User;
import com.escaperoom.escaperoom.repository.IEvenementRepository;
import com.escaperoom.escaperoom.repository.IReservationRepository;
import com.escaperoom.escaperoom.repository.ITimeSlotRepository;
import com.escaperoom.escaperoom.repository.IUserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    private ReservationService reservationService;
    private IReservationRepository reservationRepository;
    private IUserRepository userRepository;
    private ITimeSlotRepository timeSlotRepository;
    private IEvenementRepository evenementRepository;
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        // Création des mocks des dépendances utilisées par ReservationService
        reservationRepository = mock(IReservationRepository.class);
        userRepository = mock(IUserRepository.class);
        timeSlotRepository = mock(ITimeSlotRepository.class);
        evenementRepository = mock(IEvenementRepository.class);
        emailService = mock(EmailService.class);

        // Initialisation du service avec injection manuelle des mocks
        reservationService = new ReservationService();
        reservationService.reservationRepository = reservationRepository;
        reservationService.userRepository = userRepository;
        reservationService.timeSlotRepository = timeSlotRepository;
        reservationService.evenementRepository = evenementRepository;
        reservationService.emailService = emailService;
    }

    /**
     * Test d’un scénario de réservation réussie.
     * Vérifie que tous les champs de la réservation sont correctement remplis
     * et que les montants HT/TVA/TTC sont bien calculés.
     */
    @Test
    void testReserve_Success() {
        Long userId = 1L;
        Long eventId = 2L;
        Long timeSlotId = 3L;

        User user = new User();
        user.setIdUser(userId);
        user.setEmail("test@example.com");

        Evenement event = new Evenement();
        event.setIdEvenement(eventId);
        event.setPrix(40); // Prix TTC

        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setIdTimeSlot(timeSlotId);

        // Simulation des retours de repository
        when(timeSlotRepository.findById(timeSlotId)).thenReturn(Optional.of(timeSlot));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(evenementRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(reservationRepository.findByTimeSlotAndEvenement(timeSlot, event)).thenReturn(Optional.empty());
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Exécution
        Reservation result = reservationService.reserve(timeSlotId, userId, eventId);

        // Vérification des résultats
        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(event, result.getEvenement());
        assertEquals(timeSlot, result.getTimeSlot());

        // Vérifie les montants (HT, TVA, TTC)
        assertEquals(new BigDecimal("40.00"), result.getMontantHT().setScale(2));
        assertEquals(new BigDecimal("8.00"), result.getMontantTVA().setScale(2));
        assertEquals(new BigDecimal("48.00"), result.getMontant().setScale(2));
    }

    /**
     * Test d’échec : le créneau horaire n'existe pas.
     */
    @Test
    void testReserve_TimeSlotNotFound() {
        when(timeSlotRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalStateException.class, () ->
                reservationService.reserve(1L, 2L, 3L));

        assertEquals("Ce créneau n'existe pas.", exception.getMessage());
    }

    /**
     * Test d’échec : l'utilisateur est introuvable.
     */
    @Test
    void testReserve_UserNotFound() {
        TimeSlot timeSlot = new TimeSlot();
        when(timeSlotRepository.findById(anyLong())).thenReturn(Optional.of(timeSlot));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () ->
                reservationService.reserve(1L, 2L, 3L));

        assertEquals("Utilisateur introuvable", exception.getMessage());
    }

    /**
     * Test d’échec : l’événement est introuvable.
     */
    @Test
    void testReserve_EventNotFound() {
        TimeSlot timeSlot = new TimeSlot();
        User user = new User();

        when(timeSlotRepository.findById(anyLong())).thenReturn(Optional.of(timeSlot));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(evenementRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () ->
                reservationService.reserve(1L, 2L, 3L));

        assertEquals("Événement introuvable", exception.getMessage());
    }

    /**
     * Test d’échec : le créneau est déjà réservé pour l’événement.
     */
    @Test
    void testReserve_AlreadyReserved() {
        TimeSlot timeSlot = new TimeSlot();
        User user = new User();
        Evenement event = new Evenement();
        Reservation existing = new Reservation();

        when(timeSlotRepository.findById(anyLong())).thenReturn(Optional.of(timeSlot));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(evenementRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(reservationRepository.findByTimeSlotAndEvenement(timeSlot, event)).thenReturn(Optional.of(existing));

        Exception exception = assertThrows(IllegalStateException.class, () ->
                reservationService.reserve(1L, 2L, 3L));

        assertEquals("Ce créneau est déjà réservé pour cet événement.", exception.getMessage());
    }

    /**
     * Test d’échec : tentative de génération de créneaux horaires sur une date
     * pour laquelle des créneaux existent déjà.
     */
    @Test
    void testGenerateTimeSlotsForDay_AlreadyExists() {
        LocalDate date = LocalDate.of(2025, 5, 20);
        when(timeSlotRepository.findByDate(date)).thenReturn(List.of(new TimeSlot()));

        ResponseEntity<?> response = reservationService.generateTimeSlotsForDay(date);
        assertEquals(409, response.getStatusCodeValue()); // HTTP 409 CONFLICT
    }

    /**
     * Test de succès : génération des créneaux horaires sur une journée vide.
     * On s'attend à recevoir 12 créneaux (de 10h à 22h, 1h chacun).
     */
    @Test
    void testGenerateTimeSlotsForDay_Success() {
        LocalDate date = LocalDate.of(2025, 5, 21);
        when(timeSlotRepository.findByDate(date)).thenReturn(List.of());
        when(timeSlotRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<?> response = reservationService.generateTimeSlotsForDay(date);
        assertEquals(200, response.getStatusCodeValue());

        List<TimeSlot> slots = (List<TimeSlot>) response.getBody();
        assertNotNull(slots);
        assertEquals(12, slots.size()); // de 10h à 22h inclus
    }

    /**
     * Test d’échec : récupération des créneaux disponibles pour un événement inexistant.
     */
    @Test
    void testGetAvailableTimeSlotsForEvenement_EventNotFound() {
        when(evenementRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class,
                () -> reservationService.getAvailableTimeSlotsForEvenement(1L, LocalDate.now()));

        assertEquals("Événement introuvable", exception.getMessage());
    }
}
