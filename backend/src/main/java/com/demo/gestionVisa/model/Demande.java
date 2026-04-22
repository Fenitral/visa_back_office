package com.demo.gestionVisa.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.demo.gestionVisa.enums.StatutDemande;

/**
 * Entité représentant une demande de visa
 */
@Entity
@Table(name = "demande")
public class Demande {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "demandeur_id", nullable = false)
    private Demandeur demandeur;
    
    @ManyToOne
    @JoinColumn(name = "visa_transformable_id", nullable = false)
    private VisaTransformable visaTransformable;
    
    @ManyToOne
    @JoinColumn(name = "id_type_visa")
    private TypeVisa typeVisa;
    
    @ManyToOne
    @JoinColumn(name = "id_type_demande", nullable = false)
    private TypeDemandeRef typeDemande;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatutDemande statut;
    
    @Column(nullable = false, name = "pieces_obligatoires_completes")
    private boolean piecesObligatoiresCompletes;
    
    @Column(name = "dossier_complet")
    private boolean dossierComplet;
    
    @Column(nullable = false)
    private LocalDateTime dateCreation;
    
    @Column
    private LocalDateTime dateModification;
    
    @Column
    private LocalDateTime dateTraitement;

    // Constructeurs
    public Demande() {
        this.dateCreation = LocalDateTime.now();
    }

    public Demande(Demandeur demandeur, VisaTransformable visaTransformable, 
                   TypeVisa typeVisa, TypeDemandeRef typeDemande) {
        this.demandeur = demandeur;
        this.visaTransformable = visaTransformable;
        this.typeVisa = typeVisa;
        this.typeDemande = typeDemande;
        this.dateCreation = LocalDateTime.now();
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Demandeur getDemandeur() {
        return demandeur;
    }

    public void setDemandeur(Demandeur demandeur) {
        this.demandeur = demandeur;
    }

    public VisaTransformable getVisaTransformable() {
        return visaTransformable;
    }

    public void setVisaTransformable(VisaTransformable visaTransformable) {
        this.visaTransformable = visaTransformable;
    }

    public TypeDemandeRef getTypeDemande() {
        return typeDemande;
    }

    public void setTypeDemande(TypeDemandeRef typeDemande) {
        this.typeDemande = typeDemande;
    }

    public TypeVisa getTypeVisa() {
        return typeVisa;
    }

    public void setTypeVisa(TypeVisa typeVisa) {
        this.typeVisa = typeVisa;
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

    public LocalDateTime getDateTraitement() {
        return dateTraitement;
    }

    public void setDateTraitement(LocalDateTime dateTraitement) {
        this.dateTraitement = dateTraitement;
    }
}
