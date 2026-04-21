package com.demo.gestionVisa.repository;

import com.demo.gestionVisa.model.VisaTransformable;
import com.demo.gestionVisa.model.Demandeur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

/**
 * Repository pour la gestion des visas transformables
 */
@Repository
public interface VisaTransformableRepository extends JpaRepository<VisaTransformable, Long> {
    
    /**
     * Recherche un visa par sa référence
     */
    Optional<VisaTransformable> findByReference(String reference);
    
    /**
     * Recherche tous les visas d'un demandeur
     */
    List<VisaTransformable> findByDemandeur(Demandeur demandeur);
}
