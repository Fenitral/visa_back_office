package com.demo.gestionVisa.enums;

/**
 * Énumération des types de demande de visa
 */
public enum TypeDemande {
    TRAVAILLEUR("Visa Travailleur"),
    INVESTISSEUR("Visa Investisseur");

    private final String label;

    TypeDemande(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
