package com.demo.gestionVisa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.List;

/**
 * Request DTO du sprint 1 pour l'enregistrement d'une demande de transformation.
 */
public class DemandeTransformationCreateRequest {

    @JsonProperty("nom")
    private String nom;

    @JsonProperty("prenom")
    private String prenom;

    @JsonProperty("nom_de_jeune_fille")
    private String nomDeJeuneFille;

    @JsonProperty("date_de_naissance")
    private LocalDate dateDeNaissance;

    @JsonProperty("lieu_de_naissance")
    private String lieuDeNaissance;

    @JsonProperty("situation_de_famille")
    private String situationDeFamille;

    @JsonProperty("nationalite")
    private String nationalite;

    @JsonProperty("adresse")
    private String adresse;

    @JsonProperty("email")
    private String email;

    @JsonProperty("telephone")
    private String telephone;

    @JsonProperty("reference")
    private String reference;

    @JsonProperty("date_expiration")
    private LocalDate dateExpiration;

    @JsonProperty("date_arrivee")
    private LocalDate dateArrivee;

    @JsonProperty("lieu_d_arrivee")
    private String lieuDArrivee;

    @JsonProperty("id_type_visa")
    private Integer idTypeVisa;

    @JsonProperty("pieces")
    private List<String> pieces;

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

    public String getNomDeJeuneFille() {
        return nomDeJeuneFille;
    }

    public void setNomDeJeuneFille(String nomDeJeuneFille) {
        this.nomDeJeuneFille = nomDeJeuneFille;
    }

    public LocalDate getDateDeNaissance() {
        return dateDeNaissance;
    }

    public void setDateDeNaissance(LocalDate dateDeNaissance) {
        this.dateDeNaissance = dateDeNaissance;
    }

    public String getLieuDeNaissance() {
        return lieuDeNaissance;
    }

    public void setLieuDeNaissance(String lieuDeNaissance) {
        this.lieuDeNaissance = lieuDeNaissance;
    }

    public String getSituationDeFamille() {
        return situationDeFamille;
    }

    public void setSituationDeFamille(String situationDeFamille) {
        this.situationDeFamille = situationDeFamille;
    }

    public String getNationalite() {
        return nationalite;
    }

    public void setNationalite(String nationalite) {
        this.nationalite = nationalite;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public LocalDate getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(LocalDate dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public LocalDate getDateArrivee() {
        return dateArrivee;
    }

    public void setDateArrivee(LocalDate dateArrivee) {
        this.dateArrivee = dateArrivee;
    }

    public String getLieuDArrivee() {
        return lieuDArrivee;
    }

    public void setLieuDArrivee(String lieuDArrivee) {
        this.lieuDArrivee = lieuDArrivee;
    }

    public Integer getIdTypeVisa() {
        return idTypeVisa;
    }

    public void setIdTypeVisa(Integer idTypeVisa) {
        this.idTypeVisa = idTypeVisa;
    }

    public List<String> getPieces() {
        return pieces;
    }

    public void setPieces(List<String> pieces) {
        this.pieces = pieces;
    }
}
