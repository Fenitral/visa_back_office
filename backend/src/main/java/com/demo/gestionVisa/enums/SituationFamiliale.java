package com.demo.gestionVisa.enums;

/**
 * Énumération des situations familiales
 */
public enum SituationFamiliale {
    CELIBATAIRE("Célibataire"),
    MARIE("Marié(e)"),
    DIVORCE("Divorcé(e)"),
    VEUF("Veuf(ve)"),
    UNION_LIBRE("Union libre"),
    PACS("Pacsé(e)");

    private final String label;

    SituationFamiliale(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
