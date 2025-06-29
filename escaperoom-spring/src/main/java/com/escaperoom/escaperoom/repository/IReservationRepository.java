package com.escaperoom.escaperoom.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.escaperoom.escaperoom.entity.Evenement;
import com.escaperoom.escaperoom.entity.Reservation;
import com.escaperoom.escaperoom.entity.TimeSlot;

@Repository
public interface IReservationRepository extends JpaRepository<Reservation, Long> {

	List<Reservation> findByUserIdUser(int userId);

	public Reservation findByEvenement(Evenement evenement);
	
	public Optional<Reservation> findByTimeSlotAndEvenement(TimeSlot timeSlot, Evenement evenement);
	
	@Query("SELECT t FROM TimeSlot t WHERE t NOT IN (SELECT r.timeSlot FROM Reservation r WHERE r.evenement = :evenement) AND t.date = :selectedDate")
	List<TimeSlot> findTimeSlotsNotReservedForEvenementAndDate(@Param("evenement") Evenement evenement, @Param("selectedDate") LocalDate selectedDate);

}
