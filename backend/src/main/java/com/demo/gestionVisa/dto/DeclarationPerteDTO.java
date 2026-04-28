package com.demo.gestionVisa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la déclaration de perte de passeport et/ou carte résident.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeclarationPerteDTO {
    
    // Identification du demandeur concerné
    private Long idDemandeur;
    
    // Types de perte déclarés
    private Boolean pertePasseport;      // true = perte du passeport
    private Boolean perteCarteResident;  // true = perte de la carte résident
    
    // Raison/Commentaire de la déclaration (optionnel)
    private String motif;
}
