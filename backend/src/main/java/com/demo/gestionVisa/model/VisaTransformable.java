package com.demo.gestionVisa.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entité représentant un visa transformable
 */
@Entity
@Table(name = "visa_transformable")
public class VisaTransformable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String reference;
    
    @ManyToOne
    @JoinColumn(name = "demandeur_id", nullable = false)
    private Demandeur demandeur;
    
    @Column(nullable = false, name = "date_entree")
    private LocalDate dateEntree;
    
    @Column(nullable = false, name = "lieu_entree")
    private String lieuEntree;
    
    @Column(nullable = false, name = "date_expiration")
    private LocalDate dateExpiration;
    
    @Column(nullable = false)
    private LocalDateTime dateCreation;
    
    @Column
    private LocalDateTime dateModification;

    // Constructeurs
    public VisaTransformable() {
        this.dateCreation = LocalDateTime.now();
    }

    public VisaTransformable(String reference, Demandeur demandeur, 
                            LocalDate dateEntree, String lieuEntree, 
                            LocalDate dateExpiration) {
        this.reference = reference;
        this.demandeur = demandeur;
        this.dateEntree = dateEntree;
        this.lieuEntree = lieuEntree;
        this.dateExpiration = dateExpiration;
        this.dateCreation = LocalDateTime.now();
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

    public Demandeur getDemandeur() {
        return demandeur;
    }

    public void setDemandeur(Demandeur demandeur) {
        this.demandeur = demandeur;
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
