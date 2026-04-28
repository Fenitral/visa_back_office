package com.demo.gestionVisa.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "statut_carte_resident")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatutCarteResident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_carte_resident", nullable = false)
    private CarteResident carteResident;


    @Column(name = "date_modif", nullable = false)
    private LocalDate dateModif;
}
