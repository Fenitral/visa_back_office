package com.demo.gestionVisa.repository;

import com.demo.gestionVisa.model.Demande;
import com.demo.gestionVisa.model.Demandeur;
import com.demo.gestionVisa.enums.StatutDemande;
import com.demo.gestionVisa.enums.TypeDemande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour la gestion des demandes de visa
 */
@Repository
public interface DemandeRepository extends JpaRepository<Demande, Long> {
    
    /**
     * Recherche toutes les demandes d'un demandeur
     */
    List<Demande> findByDemandeur(Demandeur demandeur);
    
    /**
     * Recherche toutes les demandes par statut
     */
    List<Demande> findByStatut(StatutDemande statut);
    
    /**
     * Recherche toutes les demandes par type
     */
    List<Demande> findByTypeDemande(TypeDemande typeDemande);
    
    /**
     * Compte les demandes d'un demandeur
     */
    long countByDemandeur(Demandeur demandeur);
}
