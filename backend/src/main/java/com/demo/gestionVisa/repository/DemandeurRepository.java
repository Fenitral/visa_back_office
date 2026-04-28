package com.demo.gestionVisa.repository;

import com.demo.gestionVisa.model.Demandeur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

@Repository
public interface DemandeurRepository extends JpaRepository<Demandeur, Long> {
    Optional<Demandeur> findByNomAndDateNaissance(String nom, LocalDate dateNaissance);
    List<Demandeur> findByNomContainingIgnoreCase(String nom);
}
