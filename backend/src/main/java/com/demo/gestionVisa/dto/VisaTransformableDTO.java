package com.demo.gestionVisa.dto;

import java.time.LocalDate;

/**
 * DTO pour les informations du visa transformable
 */
public class VisaTransformableDTO {
    
    private Long id;
    private String reference;
    private Long demandeurId;
    private LocalDate dateEntree;
    private String lieuEntree;
    private LocalDate dateExpiration;

    // Constructeurs
    public VisaTransformableDTO() {
    }

    public VisaTransformableDTO(String reference, Long demandeurId, LocalDate dateEntree,
                               String lieuEntree, LocalDate dateExpiration) {
        this.reference = reference;
        this.demandeurId = demandeurId;
        this.dateEntree = dateEntree;
        this.lieuEntree = lieuEntree;
        this.dateExpiration = dateExpiration;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Long getDemandeurId() {
        return demandeurId;
    }

    public void setDemandeurId(Long demandeurId) {
        this.demandeurId = demandeurId;
    }

    public LocalDate getDateEntree() {
        return dateEntree;
    }

    public void setDateEntree(LocalDate dateEntree) {
        this.dateEntree = dateEntree;
    }

    public String getLieuEntree() {
        return lieuEntree;
    }

    public void setLieuEntree(String lieuEntree) {
        this.lieuEntree = lieuEntree;
    }

    public LocalDate getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(LocalDate dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    @Override
    public String toString() {
        return "VisaTransformableDTO{" +
                "id=" + id +
                ", reference='" + reference + '\'' +
                ", dateEntree=" + dateEntree +
                ", dateExpiration=" + dateExpiration +
                '}';
    }
}
