package com.escaperoom.escaperoom.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "PAIEMENTS")
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPaiement;

    private Long amount;

    // Ne contient que les 4 derniers chiffres
    private String paymentToken;

    private String transactionId;

    @Enumerated(EnumType.STRING)
    private StatutPaiement statut;

    private LocalDateTime datePaiement;

    @OneToOne(mappedBy = "paiement")
    @JsonIgnore
    private Reservation reservation;

    public Paiement() {}

    public Paiement(Long idPaiement, Long amount, String paymentToken, String transactionId,
                    StatutPaiement statut, LocalDateTime datePaiement, Reservation reservation) {
        this.idPaiement = idPaiement;
        this.amount = amount;
        this.paymentToken = paymentToken;
        this.transactionId = transactionId;
        this.statut = statut;
        this.datePaiement = datePaiement;
        this.reservation = reservation;
    }

    // Getters & setters

    public Long getIdPaiement() {
        return idPaiement;
    }

    public void setIdPaiement(Long idPaiement) {
        this.idPaiement = idPaiement;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getPaymentToken() {
        return paymentToken;
    }

    public void setPaymentToken(String paymentToken) {
        this.paymentToken = paymentToken;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public StatutPaiement getStatut() {
        return statut;
    }

    public void setStatut(StatutPaiement statut) {
        this.statut = statut;
    }

    public LocalDateTime getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(LocalDateTime datePaiement) {
        this.datePaiement = datePaiement;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }
}
