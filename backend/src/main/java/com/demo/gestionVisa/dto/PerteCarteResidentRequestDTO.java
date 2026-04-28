package com.demo.gestionVisa.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerteCarteResidentRequestDTO {
    private Long carteResidentId;  // ID of the carte resident that was lost
    private Long statutCRId;  // ID of the status (e.g., "Perdue")
    private LocalDate dateModif;  // Date of the loss
    private String commentaire;  // Optional comment
}
