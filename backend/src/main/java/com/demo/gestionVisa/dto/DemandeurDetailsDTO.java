package com.demo.gestionVisa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour retourner les détails d'un demandeur et son statut
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DemandeurDetailsDTO {
    private DemandeurDTO demandeur;
    private boolean hasAntecedents;  // true si le demandeur a des demandes antérieures
    private Long latestDemandId;      // ID de la dernière demande si elle existe
}
