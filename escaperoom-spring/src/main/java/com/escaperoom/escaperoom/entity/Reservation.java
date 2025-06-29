package com.escaperoom.escaperoom.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="RESERVATIONS")
public class Reservation {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="ID_RESERVATION")
	private Long idReservation;

	@Column(name="NUMERO_RESERVATION")
	private String numeroReservation;
	
	@Column(name="DATE_RESERVATION")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
	private LocalDateTime dateReservation;
	
    @Column(name = "MONTANT")
    private BigDecimal montant;
    
    @Column(name = "MONTANT_HT")
    private BigDecimal montantHT;
    
    @Column(name = "MONTANT_TVA")
    private BigDecimal montantTVA;
	
	@ManyToOne
	@JsonIgnoreProperties("reservations")
    @JoinColumn(name = "ID_CRENEAU_HORAIRE")
    private TimeSlot timeSlot;
	
	@ManyToOne
	@JsonBackReference(value = "user-reservation")
	@JoinColumn(name="ID_USER")
	private User user;
	
	@ManyToOne
	@JsonIgnoreProperties("reservations")
	@JoinColumn(name="ID_EVENEMENT")
	private Evenement evenement;
	
	@OneToOne
	@JsonIgnoreProperties("reservations")
	@JoinColumn(name="ID_PAIEMENT")
	private Paiement paiement;

	public Reservation() {
		super();
	}

	public Reservation(Long idReservation, String numeroReservation, LocalDateTime dateReservation, BigDecimal montant, BigDecimal montantHT, BigDecimal montantTVA, User user, Evenement evenement, TimeSlot timeSlot, Paiement paiement) {
		super();
		this.idReservation = idReservation;
		this.numeroReservation = numeroReservation;
		this.dateReservation = dateReservation;
		this.montant = montant;
		this.montantHT = montantHT;
		this.montantTVA = montantTVA;
		this.user = user;
		this.evenement = evenement;
		this.timeSlot = timeSlot;
		this.paiement = paiement;
	}

	public Long getIdReservation() {
		return idReservation;
	}

	public void setIdReservation(Long idReservation) {
		this.idReservation = idReservation;
	}

	public String getNumeroReservation() {
		return numeroReservation;
	}

	public void setNumeroReservation(String numeroReservation) {
		this.numeroReservation = numeroReservation;
	}

	public LocalDateTime getDateReservation() {
		return dateReservation;
	}

	public void setDateReservation(LocalDateTime dateReservation) {
		this.dateReservation = dateReservation;
	}

	public BigDecimal getMontant() {
		return montant;
	}

	public void setMontant(BigDecimal montant) {
		this.montant = montant;
	}

	public BigDecimal getMontantHT() {
		return montantHT;
	}

	public void setMontantHT(BigDecimal montantHT) {
		this.montantHT = montantHT;
	}

	public BigDecimal getMontantTVA() {
		return montantTVA;
	}

	public void setMontantTVA(BigDecimal montantTVA) {
		this.montantTVA = montantTVA;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Evenement getEvenement() {
		return evenement;
	}

	public void setEvenement(Evenement evenement) {
		this.evenement = evenement;
	}

	public TimeSlot getTimeSlot() {
		return timeSlot;
	}

	public void setTimeSlot(TimeSlot timeSlot) {
		this.timeSlot = timeSlot;
	}

	public Paiement getPaiement() {
		return paiement;
	}

	public void setPaiement(Paiement paiement) {
		this.paiement = paiement;
	}	
	
}
