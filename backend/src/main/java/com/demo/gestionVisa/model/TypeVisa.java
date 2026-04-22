package com.demo.gestionVisa.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Entité représentant un type de visa
 */
@Entity
@Table(name = "type_visa")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypeVisa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 50)
    private String libelle;
    
    @ManyToMany
    @JoinTable(
        name = "piece_type_visa",
        joinColumns = @JoinColumn(name = "id_type_visa"),
        inverseJoinColumns = @JoinColumn(name = "id_piece_justificatives")
    )
    private List<PieceJustificative> piecesJustificatives;
}
