package com.demo.gestionVisa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour rechercher un demandeur par un terme libre.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchDemandeurDTO {
    private String query;
    private String nom;
}
