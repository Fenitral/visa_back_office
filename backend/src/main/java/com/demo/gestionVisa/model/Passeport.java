package com.demo.gestionVisa.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

/**
 * Entité représentant un passeport
 */
@Entity
@Table(name = "passeport")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Passeport {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "id_demandeur", nullable = false)
    private Demandeur demandeur;
    
    @Column(nullable = false, unique = true, length = 50)
    private String numeroPasseport;
    
    @Column(nullable = false)
    private LocalDate dateDelivrance;
    
    @Column(nullable = false)
    private LocalDate dateExpiration;
    
    @Column(length = 100)
    private String paysDelivrance;
    
    @OneToMany(mappedBy = "passeport", cascade = CascadeType.ALL)
    private List<StatutPasseport> statuts;
}
