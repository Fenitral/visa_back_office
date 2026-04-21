package com.demo.gestionVisa.enums;

/**
 * Énumération des statuts possibles pour une demande de visa
 */
public enum StatutDemande {
    DOSSIER_CREE("Dossier créé"),
    VALIDEE("Validée"),
    REJETEE("Rejetée");

    private final String label;

    StatutDemande(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
