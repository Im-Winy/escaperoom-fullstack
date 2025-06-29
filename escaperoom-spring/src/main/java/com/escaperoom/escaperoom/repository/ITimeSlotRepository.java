package com.escaperoom.escaperoom.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.escaperoom.escaperoom.entity.TimeSlot;

@Repository
public interface ITimeSlotRepository extends JpaRepository<TimeSlot, Long> {
    
	List<TimeSlot> findByDate(LocalDate date);

}
