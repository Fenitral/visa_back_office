package com.demo.gestionVisa.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarteResidentSearchDTO {
    private String nom;  // Search by name (demandeur name)
    private String numeroCarte;  // Search by carte resident number
}
