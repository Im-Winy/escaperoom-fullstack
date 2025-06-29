package com.escaperoom.escaperoom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.escaperoom.escaperoom.entity.Evenement;

@Repository
public interface IEvenementRepository extends JpaRepository<Evenement,Long>{

}
