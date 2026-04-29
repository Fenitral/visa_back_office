package com.demo.gestionVisa.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "demande_piece")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemandePiece {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_demande", nullable = false)
    private Demande demande;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_piece", nullable = false)
    private Piece piece;

    @Column(nullable = false)
    private Boolean fourni = false;

    // SPRINT 3: Scan des pièces justificatives
    @Column(name = "chemin_fichier", length = 500)
    private String cheminFichier;

    @Column(name = "nom_fichier", length = 255)
    private String nomFichier;

    @Column(name = "date_upload")
    private LocalDateTime dateUpload;
}
