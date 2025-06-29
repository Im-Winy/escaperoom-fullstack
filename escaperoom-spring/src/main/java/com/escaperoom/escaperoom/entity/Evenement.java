package com.escaperoom.escaperoom.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "EVENEMENTS")
public class Evenement {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_EVENEMENT")
	private Long idEvenement;
	
	@Column(name = "NOM")
	private String nom;
	
	@Column(name = "DESCRIPTION")
	private String description;
	
	@Column(name = "IMAGE")
	private String image;
	
	@Column(name = "DUREE")
	private int duree;
	
	@Column(name = "NBE_JOUEUR_MAX")
	private int nbeJoueurMax;
	
	@Column(name = "PRIX")
	private int prix;
	
	@Column(name = "DIFFICULTE")
	@Enumerated(EnumType.STRING)
	private Difficulte difficulte;
	
	@OneToMany(mappedBy = "evenement", cascade = CascadeType.ALL)
	@JsonIgnore
    private List<Reservation> reservation = new ArrayList<>();

	public Evenement() {
		super();
	}

	// Constructeur avec tous les paramètres
	public Evenement(Long idEvenement, String nom, String description, String image, int duree, int nbeJoueurMax,
			int prix, Difficulte difficulte, List<Reservation> reservation) {
		super();
		this.idEvenement = idEvenement;
		this.nom = nom;
		this.description = description;
		this.image = image;
		this.duree = duree;
		this.nbeJoueurMax = nbeJoueurMax;
		this.prix = prix;
		this.difficulte = difficulte;
		this.reservation = reservation;
	}
	
	// Constructeur (sans ID et réservations)
	public Evenement(String nom, String description, String image, int duree, int nbeJoueurMax,
			int prix, Difficulte difficulte) {
		super();
		this.nom = nom;
		this.description = description;
		this.image = image;
		this.duree = duree;
		this.nbeJoueurMax = nbeJoueurMax;
		this.prix = prix;
		this.difficulte = difficulte;
	}

	public Long getIdEvenement() {
		return idEvenement;
	}

	public void setIdEvenement(Long idEvenement) {
		this.idEvenement = idEvenement;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public int getDuree() {
		return duree;
	}

	public void setDuree(int duree) {
		this.duree = duree;
	}

	public int getNbeJoueurMax() {
		return nbeJoueurMax;
	}

	public void setNbeJoueurMax(int nbeJoueurMax) {
		this.nbeJoueurMax = nbeJoueurMax;
	}

	public int getPrix() {
		return prix;
	}

	public void setPrix(int prix) {
		this.prix = prix;
	}

	public Difficulte getDifficulte() {
		return difficulte;
	}

	public void setDifficulte(Difficulte difficulte) {
		this.difficulte = difficulte;
	}

	public List<Reservation> getReservation() {
		return reservation;
	}

	public void setReservation(List<Reservation> reservation) {
		this.reservation = reservation;
	}

}
