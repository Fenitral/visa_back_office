package com.demo.gestionVisa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour afficher l'état du scan d'une pièce justificative
 * Utilisé pour l'interface de scan des pièces
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScanPieceDTO {

    private Long idPiece;
    private Long idDemandePiece; // ID du lien demande-piece
    private String nomPiece;
    private Boolean obligatoire;
    private Boolean fourni; // true si la pièce a été fournie/complétée
    private Boolean scannee; // true si cheminFichier != null
    private String nomFichier;
    private LocalDateTime dateUpload;
}
