package com.demo.gestionVisa.dto;

import com.demo.gestionVisa.enums.StatutDemande;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response DTO du sprint 1 pour la creation d'une demande de transformation.
 */
public class DemandeTransformationCreateResponse {

    @JsonProperty("id_demande")
    private Long idDemande;

    @JsonProperty("numero_demande")
    private String numeroDemande;

    @JsonProperty("statut")
    private StatutDemande statut;

    public DemandeTransformationCreateResponse(Long idDemande, String numeroDemande, StatutDemande statut) {
        this.idDemande = idDemande;
        this.numeroDemande = numeroDemande;
        this.statut = statut;
    }

    public Long getIdDemande() {
        return idDemande;
    }

    public String getNumeroDemande() {
        return numeroDemande;
    }

    public StatutDemande getStatut() {
        return statut;
    }
}
