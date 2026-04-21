-- ============================================
-- SCRIPT DE CRÉATION DES TABLES
-- GESTION DES DEMANDES DE VISA
-- ============================================

-- Table des demandeurs
CREATE TABLE IF NOT EXISTS demandeur (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    nom_jeune_fille VARCHAR(100),
    date_naissance DATE NOT NULL,
    situation_familiale VARCHAR(50) NOT NULL,
    nationalite VARCHAR(100) NOT NULL,
    profession VARCHAR(100),
    adresse_madagascar VARCHAR(255) NOT NULL,
    telephone VARCHAR(20),
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP,
    CONSTRAINT uk_demandeur_nom_prenom UNIQUE (nom, prenom)
);

-- Table des visas transformables
CREATE TABLE IF NOT EXISTS visa_transformable (
    id BIGSERIAL PRIMARY KEY,
    reference VARCHAR(50) NOT NULL UNIQUE,
    demandeur_id BIGINT NOT NULL,
    date_entree DATE NOT NULL,
    lieu_entree VARCHAR(100) NOT NULL,
    date_expiration DATE NOT NULL,
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP,
    CONSTRAINT fk_visa_transformable_demandeur FOREIGN KEY (demandeur_id) REFERENCES demandeur(id) ON DELETE CASCADE
);

-- Table des statuts de demande
CREATE TABLE IF NOT EXISTS statut_demande (
    id SERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    libelle VARCHAR(100) NOT NULL,
    description TEXT
);

-- Insertion des statuts
INSERT INTO statut_demande (code, libelle, description) VALUES
('DOSSIER_CREE', 'Dossier créé', 'Dossier créé - en attente de validation'),
('VALIDEE', 'Validée', 'Demande validée'),
('REJETEE', 'Rejetée', 'Demande rejetée')
ON CONFLICT (code) DO NOTHING;

-- Table des demandes de visa
CREATE TABLE IF NOT EXISTS demande (
    id BIGSERIAL PRIMARY KEY,
    demandeur_id BIGINT NOT NULL,
    visa_transformable_id BIGINT NOT NULL,
    type_demande VARCHAR(50) NOT NULL,
    statut VARCHAR(50) NOT NULL,
    pieces_obligatoires_completes BOOLEAN DEFAULT FALSE,
    dossier_complet BOOLEAN DEFAULT FALSE,
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP,
    date_traitement TIMESTAMP,
    CONSTRAINT fk_demande_demandeur FOREIGN KEY (demandeur_id) REFERENCES demandeur(id) ON DELETE CASCADE,
    CONSTRAINT fk_demande_visa FOREIGN KEY (visa_transformable_id) REFERENCES visa_transformable(id) ON DELETE CASCADE,
    CONSTRAINT ck_type_demande CHECK (type_demande IN ('TRAVAILLEUR', 'INVESTISSEUR')),
    CONSTRAINT ck_statut CHECK (statut IN ('DOSSIER_CREE', 'VALIDEE', 'REJETEE'))
);

-- Table des pièces justificatives
CREATE TABLE IF NOT EXISTS piece_justificative (
    id BIGSERIAL PRIMARY KEY,
    demande_id BIGINT NOT NULL,
    type_piece VARCHAR(100) NOT NULL,
    nom_fichier VARCHAR(255) NOT NULL,
    chemin_fichier VARCHAR(500),
    sousmise BOOLEAN DEFAULT FALSE,
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP,
    CONSTRAINT fk_piece_justificative_demande FOREIGN KEY (demande_id) REFERENCES demande(id) ON DELETE CASCADE
);

-- Créer les indexes pour optimiser les recherches
CREATE INDEX idx_demandeur_nationalite ON demandeur(nationalite);
CREATE INDEX idx_visa_transformable_demandeur_id ON visa_transformable(demandeur_id);
CREATE INDEX idx_visa_transformable_reference ON visa_transformable(reference);
CREATE INDEX idx_demande_demandeur_id ON demande(demandeur_id);
CREATE INDEX idx_demande_visa_transformable_id ON demande(visa_transformable_id);
CREATE INDEX idx_demande_statut ON demande(statut);
CREATE INDEX idx_demande_type_demande ON demande(type_demande);
CREATE INDEX idx_piece_justificative_demande_id ON piece_justificative(demande_id);
CREATE INDEX idx_piece_justificative_sousmise ON piece_justificative(sousmise);

-- ============================================
-- COMMENTAIRES DES TABLES
-- ============================================

COMMENT ON TABLE demandeur IS 'Table des informations des demandeurs de visa';
COMMENT ON TABLE visa_transformable IS 'Table des visas transformables';
COMMENT ON TABLE demande IS 'Table des demandes de visa';
COMMENT ON TABLE piece_justificative IS 'Table des pièces justificatives pour les demandes';
COMMENT ON COLUMN demande.pieces_obligatoires_completes IS 'Indique si toutes les pièces obligatoires sont soumises';
COMMENT ON COLUMN demande.dossier_complet IS 'Indique si toutes les pièces (obligatoires et facultatives) sont soumises';
COMMENT ON COLUMN piece_justificative.sousmise IS 'Indique si la pièce justificative a été soumise';
