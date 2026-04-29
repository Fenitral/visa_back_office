package com.demo.gestionVisa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO pour afficher l'état complet du scan d'une demande
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScanDemandeDTO {

    private Long idDemande;
    private String numeroDemande;
    private String nomDemandeur;
    private String statutActuel;
    private List<ScanPieceDTO> pieces;
    
    /**
     * @return true si tous les pièces obligatoires ont été scannées
     */
    public boolean sontToutesPiecesObligatoiresScannees() {
        if (pieces == null || pieces.isEmpty()) {
            return false;
        }
        return pieces.stream()
                .filter(ScanPieceDTO::getObligatoire)
                .allMatch(ScanPieceDTO::getScannee);
    }
}
