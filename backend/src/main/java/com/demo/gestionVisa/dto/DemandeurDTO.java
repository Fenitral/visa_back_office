package com.demo.gestionVisa.dto;

import java.time.LocalDate;
import com.demo.gestionVisa.enums.SituationFamiliale;

/**
 * DTO pour les informations d'un demandeur
 */
public class DemandeurDTO {
    
    private Long id;
    private String nom;
    private String prenom;
    private String nomJeuneFille;
    private LocalDate dateNaissance;
    private SituationFamiliale situationFamiliale;
    private String nationalite;
    private String profession;
    private String adresseMadagascar;
    private String telephone;

    // Constructeurs
    public DemandeurDTO() {
    }

    public DemandeurDTO(String nom, String prenom, LocalDate dateNaissance,
                       SituationFamiliale situationFamiliale, String nationalite,
                       String adresseMadagascar) {
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.situationFamiliale = situationFamiliale;
        this.nationalite = nationalite;
        this.adresseMadagascar = adresseMadagascar;
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

    public SituationFamiliale getSituationFamiliale() {
        return situationFamiliale;
    }

    public void setSituationFamiliale(SituationFamiliale situationFamiliale) {
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

    @Override
    public String toString() {
        return "DemandeurDTO{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", dateNaissance=" + dateNaissance +
                ", nationalite='" + nationalite + '\'' +
                '}';
    }
}
