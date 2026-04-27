package com.demo.gestionVisa.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "type_visa")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TypeVisa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String libelle;

    @OneToMany(mappedBy = "typeVisa")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Demande> demandes = new HashSet<>();
}
