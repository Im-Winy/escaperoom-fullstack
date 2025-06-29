package com.escaperoom.escaperoom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.escaperoom.escaperoom.entity.Paiement;
import com.escaperoom.escaperoom.entity.StatutPaiement;

@Repository
public interface IPaiementRepository extends JpaRepository<Paiement, Long> {
	List<Paiement> findByStatut(StatutPaiement statut);
}
