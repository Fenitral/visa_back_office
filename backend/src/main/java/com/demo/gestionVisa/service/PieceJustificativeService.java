package com.demo.gestionVisa.service;

import com.demo.gestionVisa.model.PieceJustificative;
import com.demo.gestionVisa.model.Demande;
import com.demo.gestionVisa.repository.PieceJustificativeRepository;
import com.demo.gestionVisa.dto.PieceJustificativeDTO;
import com.demo.gestionVisa.enums.TypePieceJustificative;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

/**
 * Service pour la gestion des pièces justificatives
 */
@Service
public class PieceJustificativeService {
    
    @Autowired
    private PieceJustificativeRepository pieceJustificativeRepository;
    
    /**
     * Créer une nouvelle pièce justificative
     */
    public PieceJustificative creerPieceJustificative(Demande demande, PieceJustificativeDTO pieceDTO) {
        PieceJustificative piece = new PieceJustificative();
        piece.setDemande(demande);
        piece.setTypePiece(pieceDTO.getTypePiece());
        piece.setNomFichier(pieceDTO.getNomFichier());
        piece.setCheminFichier(pieceDTO.getCheminFichier());
        piece.setSousmise(pieceDTO.isSousmise());
        
        return pieceJustificativeRepository.save(piece);
    }
    
    /**
     * Récupérer toutes les pièces d'une demande
     */
    public List<PieceJustificative> getPiecesByDemande(Demande demande) {
        return pieceJustificativeRepository.findByDemande(demande);
    }
    
    /**
     * Récupérer une pièce spécifique
     */
    public Optional<PieceJustificative> getPieceByDemandeAndType(Demande demande, TypePieceJustificative type) {
        return pieceJustificativeRepository.findByDemandeAndTypePiece(demande, type);
    }
    
    /**
     * Vérifier si toutes les pièces obligatoires sont soumises
     */
    public boolean verifierPiecesObligatoires(Demande demande) {
        List<PieceJustificative> pieces = getPiecesByDemande(demande);
        
        for (TypePieceJustificative typePiece : TypePieceJustificative.values()) {
            if (typePiece.isObligatoire()) {
                boolean found = pieces.stream()
                    .anyMatch(p -> p.getTypePiece() == typePiece && p.isSousmise());
                
                if (!found) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /**
     * Vérifier si toutes les pièces (obligatoires ET facultatives) sont soumises
     */
    public boolean verifierDossierComplet(Demande demande) {
        List<PieceJustificative> pieces = getPiecesByDemande(demande);
        
        for (TypePieceJustificative typePiece : TypePieceJustificative.values()) {
            boolean found = pieces.stream()
                .anyMatch(p -> p.getTypePiece() == typePiece && p.isSousmise());
            
            if (!found) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Soumettre une pièce justificative
     */
    public PieceJustificative soumettePieceJustificative(Long pieceId) {
        Optional<PieceJustificative> pieceOptional = pieceJustificativeRepository.findById(pieceId);
        
        if (pieceOptional.isPresent()) {
            PieceJustificative piece = pieceOptional.get();
            piece.setSousmise(true);
            piece.setDateModification(LocalDateTime.now());
            
            return pieceJustificativeRepository.save(piece);
        }
        
        return null;
    }
    
    /**
     * Retirer une pièce justificative
     */
    public PieceJustificative retirerPieceJustificative(Long pieceId) {
        Optional<PieceJustificative> pieceOptional = pieceJustificativeRepository.findById(pieceId);
        
        if (pieceOptional.isPresent()) {
            PieceJustificative piece = pieceOptional.get();
            piece.setSousmise(false);
            piece.setDateModification(LocalDateTime.now());
            
            return pieceJustificativeRepository.save(piece);
        }
        
        return null;
    }
    
    /**
     * Récupérer une pièce par ID
     */
    public Optional<PieceJustificative> getPieceById(Long id) {
        return pieceJustificativeRepository.findById(id);
    }
    
    /**
     * Supprimer une pièce
     */
    public void deletePiece(Long id) {
        pieceJustificativeRepository.deleteById(id);
    }
    
    /**
     * Compter les pièces soumises pour une demande
     */
    public long countPiecesSousmises(Demande demande) {
        return pieceJustificativeRepository.countByDemandeAndSousmise(demande, true);
    }
}
