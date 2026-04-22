package com.demo.gestionVisa.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

/**
 * Entité représentant le statut d'un passeport
 */
@Entity
@Table(name = "statut_passeport")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatutPasseport {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "id_passeport", nullable = false)
    private Passeport passeport;
    
    @Column(nullable = false)
    private String statut;  // 'actif', 'expire', 'perdu', 'volee'
    
    @Column
    private LocalDate dateChangementStatut;
}
