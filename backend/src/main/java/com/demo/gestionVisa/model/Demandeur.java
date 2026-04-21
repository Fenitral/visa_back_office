package com.demo.gestionVisa.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entité représentant un demandeur de visa
 */
@Entity
@Table(name = "demandeur")
public class Demandeur {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nom;
    
    @Column(nullable = false)
    private String prenom;
    
    @Column(name = "nom_jeune_fille")
    private String nomJeuneFille;
    
    @Column(nullable = false)
    private LocalDate dateNaissance;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private com.demo.gestionVisa.enums.SituationFamiliale situationFamiliale;
    
    @Column(nullable = false)
    private String nationalite;
    
    @Column
    private String profession;
    
    @Column(nullable = false, name = "adresse_madagascar")
    private String adresseMadagascar;
    
    @Column
    private String telephone;
    
    @Column(nullable = false)
    private LocalDateTime dateCreation;
    
    @Column
    private LocalDateTime dateModification;

    // Constructeurs
    public Demandeur() {
        this.dateCreation = LocalDateTime.now();
    }

    public Demandeur(String nom, String prenom, LocalDate dateNaissance, 
                     com.demo.gestionVisa.enums.SituationFamiliale situationFamiliale, 
                     String nationalite, String adresseMadagascar) {
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.situationFamiliale = situationFamiliale;
        this.nationalite = nationalite;
        this.adresseMadagascar = adresseMadagascar;
        this.dateCreation = LocalDateTime.now();
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNomJeuneFille() {
        return nomJeuneFille;
    }

    public void setNomJeuneFille(String nomJeuneFille) {
        this.nomJeuneFille = nomJeuneFille;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public com.demo.gestionVisa.enums.SituationFamiliale getSituationFamiliale() {
        return situationFamiliale;
    }

    public void setSituationFamiliale(com.demo.gestionVisa.enums.SituationFamiliale situationFamiliale) {
        this.situationFamiliale = situationFamiliale;
    }

    public String getNationalite() {
        return nationalite;
    }

    public void setNationalite(String nationalite) {
        this.nationalite = nationalite;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getAdresseMadagascar() {
        return adresseMadagascar;
    }

    public void setAdresseMadagascar(String adresseMadagascar) {
        this.adresseMadagascar = adresseMadagascar;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
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
