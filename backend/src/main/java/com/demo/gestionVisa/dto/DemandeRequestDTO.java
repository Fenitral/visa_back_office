package com.demo.gestionVisa.dto;

import com.demo.gestionVisa.enums.TypeDemande;
import com.demo.gestionVisa.enums.StatutDemande;
import java.util.List;

/**
 * DTO pour la création d'une nouvelle demande de visa
 */
public class DemandeRequestDTO {
    
    private DemandeurDTO demandeur;
    private VisaTransformableDTO visaTransformable;
    private TypeDemande typeDemande;
    private List<PieceJustificativeDTO> piecesJustificatives;

    // Constructeurs
    public DemandeRequestDTO() {
    }

    // Getters et Setters
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

    public List<PieceJustificativeDTO> getPiecesJustificatives() {
        return piecesJustificatives;
    }

    public void setPiecesJustificatives(List<PieceJustificativeDTO> piecesJustificatives) {
        this.piecesJustificatives = piecesJustificatives;
    }
}
