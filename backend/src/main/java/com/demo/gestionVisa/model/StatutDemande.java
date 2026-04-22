package com.demo.gestionVisa.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

/**
 * Entité représentant le statut d'une demande
 */
@Entity
@Table(name = "statut_demande")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatutDemande {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "id_demande", nullable = false)
    private Demande demande;
    
    @Column(nullable = false, length = 50)
    private String statut;  // 'brouillon', 'soumise', 'en_cours', 'validee', 'rejetee'
    
    @Column
    private LocalDate dateChangementStatut;
}
