package com.escaperoom.escaperoom.entity;

public class PaymentRequest {

    private Long montant;

    private String numeroCarteBancaire;

    private String dateExppiration;

    private String cvv;

    // Getters & setters

    public Long getMontant() {
        return montant;
    }

    public void setMontant(Long montant) {
        this.montant = montant;
    }

    public String getNumeroCarteBancaire() {
        return numeroCarteBancaire;
    }

    public void setNumeroCarteBancaire(String numeroCarteBancaire) {
        this.numeroCarteBancaire = numeroCarteBancaire;
    }

    public String getDateExppiration() {
        return dateExppiration;
    }

    public void setDateExppiration(String dateExppiration) {
        this.dateExppiration = dateExppiration;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }
}
