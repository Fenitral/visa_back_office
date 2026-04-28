package com.demo.gestionVisa.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "statut_cr")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatutCR {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String libelle;

    @OneToMany(mappedBy = "statutCR")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<StatutCarteResident> statutsCarteResident = new HashSet<>();
}
