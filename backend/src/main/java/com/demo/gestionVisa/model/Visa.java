package com.demo.gestionVisa.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

/**
 * Entité représentant un visa
 */
@Entity
@Table(name = "visa")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Visa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "id_demande", nullable = false)
    private Demande demande;
    
    @Column(length = 50)
    private String reference;
    
    @Column(nullable = false)
    private LocalDate dateDebut;
    
    @Column(nullable = false)
    private LocalDate dateFin;
    
    @ManyToOne
    @JoinColumn(name = "id_passeport", nullable = false)
    private Passeport passeport;
}
