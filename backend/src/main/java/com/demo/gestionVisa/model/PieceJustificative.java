package com.demo.gestionVisa.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.demo.gestionVisa.enums.TypePieceJustificative;

/**
 * Entité représentant une pièce justificative associée à une demande
 */
@Entity
@Table(name = "piece_justificative")
public class PieceJustificative {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "demande_id", nullable = false)
    private Demande demande;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TypePieceJustificative typePiece;
    
    @Column(nullable = false)
    private String nomFichier;
    
    @Column
    private String cheminFichier;
    
    @Column(nullable = false)
    private boolean sousmise;
    
    @Column(nullable = false)
    private LocalDateTime dateCreation;
    
    @Column
    private LocalDateTime dateModification;

    // Constructeurs
    public PieceJustificative() {
        this.dateCreation = LocalDateTime.now();
        this.sousmise = false;
    }

    public PieceJustificative(Demande demande, TypePieceJustificative typePiece, 
                             String nomFichier) {
        this.demande = demande;
        this.typePiece = typePiece;
        this.nomFichier = nomFichier;
        this.dateCreation = LocalDateTime.now();
        this.sousmise = false;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Demande getDemande() {
        return demande;
    }

    public void setDemande(Demande demande) {
        this.demande = demande;
    }

    public TypePieceJustificative getTypePiece() {
        return typePiece;
    }

    public void setTypePiece(TypePieceJustificative typePiece) {
        this.typePiece = typePiece;
    }

    public String getNomFichier() {
        return nomFichier;
    }

    public void setNomFichier(String nomFichier) {
        this.nomFichier = nomFichier;
    }

    public String getCheminFichier() {
        return cheminFichier;
    }

    public void setCheminFichier(String cheminFichier) {
        this.cheminFichier = cheminFichier;
    }

    public boolean isSousmise() {
        return sousmise;
    }

    public void setSousmise(boolean sousmise) {
        this.sousmise = sousmise;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDateModification() {
        return dateModification;
    }

    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }
}
