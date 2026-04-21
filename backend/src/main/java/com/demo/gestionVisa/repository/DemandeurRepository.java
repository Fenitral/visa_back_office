package com.demo.gestionVisa.repository;

import com.demo.gestionVisa.model.Demandeur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

/**
 * Repository pour la gestion des demandeurs
 */
@Repository
public interface DemandeurRepository extends JpaRepository<Demandeur, Long> {
    
    /**
     * Recherche un demandeur par nom et prénom
     */
    Optional<Demandeur> findByNomAndPrenom(String nom, String prenom);
    
    /**
     * Recherche tous les demandeurs par nationalité
     */
    List<Demandeur> findByNationalite(String nationalite);
}
