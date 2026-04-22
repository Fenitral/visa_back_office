package com.demo.gestionVisa.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entité représentant une situation familiale (table de référence)
 */
@Entity
@Table(name = "situation_familiale")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SituationFamilialeRef {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 50)
    private String libelle;
}
