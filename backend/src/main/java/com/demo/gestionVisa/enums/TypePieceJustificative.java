package com.demo.gestionVisa.enums;

/**
 * Énumération des types de pièces justificatives requises
 */
public enum TypePieceJustificative {
    PASSEPORT("Passeport", true),
    CERTIFICAT_NAISSANCE("Certificat de naissance", true),
    LETTRE_EMBAUCHE("Lettre d'embauche", true),
    CONTRAT_TRAVAIL("Contrat de travail", true),
    JUSTIFICATIF_DOMICILE("Justificatif de domicile", true),
    ACTE_MARIAGE("Acte de mariage", false),
    ACTE_DIVORCE("Acte de divorce", false),
    CERTIFICAT_SANTE("Certificat médical", false),
    CASIER_JUDICIAIRE("Extrait de casier judiciaire", true),
    JUSTIFICATIF_MOYENS("Justificatif de moyens financiers", true),
    ASSURANCE_MALADIE("Attestation assurance maladie", true),
    PHOTO_IDENTITE("Photographie d'identité", true),
    RELEVE_COMPTE("Relevé de compte bancaire", false),
    PREUVE_INVESTISSEMENT("Preuve de l'investissement", false);

    private final String label;
    private final boolean obligatoire;

    TypePieceJustificative(String label, boolean obligatoire) {
        this.label = label;
        this.obligatoire = obligatoire;
    }

    public String getLabel() {
        return label;
    }

    public boolean isObligatoire() {
        return obligatoire;
    }
}
