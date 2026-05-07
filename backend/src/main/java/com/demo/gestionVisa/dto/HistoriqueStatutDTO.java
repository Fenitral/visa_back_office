package com.demo.gestionVisa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoriqueStatutDTO {

    private Long id;
    private String statut;              // libellé du statut (ex: "CREE", "SCANNEE", "APPROUVEE")
    private LocalDateTime dateChangement;
    private String commentaire;
}
