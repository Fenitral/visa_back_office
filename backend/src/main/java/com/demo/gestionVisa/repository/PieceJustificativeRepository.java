package com.demo.gestionVisa.repository;

import com.demo.gestionVisa.model.PieceJustificative;
import com.demo.gestionVisa.model.Demande;
import com.demo.gestionVisa.enums.TypePieceJustificative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour la gestion des pièces justificatives
 */
@Repository
public interface PieceJustificativeRepository extends JpaRepository<PieceJustificative, Long> {
    
    /**
     * Recherche toutes les pièces d'une demande
     */
    List<PieceJustificative> findByDemande(Demande demande);
    
    /**
     * Recherche une pièce spécifique pour une demande
     */
    Optional<PieceJustificative> findByDemandeAndTypePiece(Demande demande, TypePieceJustificative typePiece);
    
    /**
     * Compte les pièces soumises pour une demande
     */
    long countByDemandeAndSousmise(Demande demande, boolean sousmise);
}
