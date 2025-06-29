package com.escaperoom.escaperoom.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="CRENEAU_HORAIRE")
public class TimeSlot {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CRENEAU_HORAIRE")
    private Long idTimeSlot;

    @Column(name = "DATE")
    private LocalDate date;
    
    @Column(name = "HEURE_DEBUT")
    private LocalTime startTime;
    
    @Column(name = "HEURE_FIN")
    private LocalTime endTime;

	public TimeSlot() {
		super();
	}

	public TimeSlot(Long idTimeSlot, LocalDate date, LocalTime startTime, LocalTime endTime) {
		super();
		this.idTimeSlot = idTimeSlot;
		this.date = date;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public Long getIdTimeSlot() {
		return idTimeSlot;
	}

	public void setIdTimeSlot(Long idTimeSlot) {
		this.idTimeSlot = idTimeSlot;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}

	public LocalTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
	}
    
}