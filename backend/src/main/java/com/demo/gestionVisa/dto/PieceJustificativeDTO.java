package com.demo.gestionVisa.dto;

import com.demo.gestionVisa.enums.TypePieceJustificative;

/**
 * DTO pour les pièces justificatives
 */
public class PieceJustificativeDTO {
    
    private Long id;
    private TypePieceJustificative typePiece;
    private String nomFichier;
    private String cheminFichier;
    private boolean sousmise;

    // Constructeurs
    public PieceJustificativeDTO() {
    }

    public PieceJustificativeDTO(TypePieceJustificative typePiece, String nomFichier) {
        this.typePiece = typePiece;
        this.nomFichier = nomFichier;
        this.sousmise = false;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "PieceJustificativeDTO{" +
                "id=" + id +
                ", typePiece=" + typePiece +
                ", nomFichier='" + nomFichier + '\'' +
                ", sousmise=" + sousmise +
                '}';
    }
}
