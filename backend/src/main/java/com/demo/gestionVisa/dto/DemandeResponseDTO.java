package com.demo.gestionVisa.dto;

import com.demo.gestionVisa.enums.TypeDemande;
import com.demo.gestionVisa.enums.StatutDemande;
import java.time.LocalDateTime;

/**
 * DTO pour la réponse d'une demande de visa
 */
public class DemandeResponseDTO {
    
    private Long id;
    private DemandeurDTO demandeur;
    private VisaTransformableDTO visaTransformable;
    private TypeDemande typeDemande;
    private StatutDemande statut;
    private boolean piecesObligatoiresCompletes;
    private boolean dossierComplet;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private String message;

    // Constructeurs
    public DemandeResponseDTO() {
    }

    public DemandeResponseDTO(Long id, DemandeurDTO demandeur, 
                             VisaTransformableDTO visaTransformable,
                             TypeDemande typeDemande, StatutDemande statut,
                             boolean piecesObligatoiresCompletes, boolean dossierComplet) {
        this.id = id;
        this.demandeur = demandeur;
        this.visaTransformable = visaTransformable;
        this.typeDemande = typeDemande;
        this.statut = statut;
        this.piecesObligatoiresCompletes = piecesObligatoiresCompletes;
        this.dossierComplet = dossierComplet;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DemandeurDTO getDemandeur() {
        return demandeur;
    }

    public void setDemandeur(DemandeurDTO demandeur) {
        this.demandeur = demandeur;
    }

    public VisaTransformableDTO getVisaTransformable() {
        return visaTransformable;
    }

    public void setVisaTransformable(VisaTransformableDTO visaTransformable) {
        this.visaTransformable = visaTransformable;
    }

    public TypeDemande getTypeDemande() {
        return typeDemande;
    }

    public void setTypeDemande(TypeDemande typeDemande) {
        this.typeDemande = typeDemande;
    }

    public StatutDemande getStatut() {
        return statut;
    }

    public void setStatut(StatutDemande statut) {
        this.statut = statut;
    }

    public boolean isPiecesObligatoiresCompletes() {
        return piecesObligatoiresCompletes;
    }

    public void setPiecesObligatoiresCompletes(boolean piecesObligatoiresCompletes) {
        this.piecesObligatoiresCompletes = piecesObligatoiresCompletes;
    }

    public boolean isDossierComplet() {
        return dossierComplet;
    }

    public void setDossierComplet(boolean dossierComplet) {
        this.dossierComplet = dossierComplet;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
