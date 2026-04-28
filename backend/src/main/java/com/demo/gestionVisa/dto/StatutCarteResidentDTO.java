package com.demo.gestionVisa.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatutCarteResidentDTO {
    private Long id;
    private Long carteResidentId;
    private String carteResidentNumero;
    private Long statutCRId;
    private String statutCRLibelle;
    private LocalDate dateModif;
}
