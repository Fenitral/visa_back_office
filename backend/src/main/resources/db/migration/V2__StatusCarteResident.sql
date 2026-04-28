-- ============================================
-- MIGRATION V2: Statut Carte Résident
-- GESTION DES PERTES DE CARTE DE RÉSIDENT
-- ============================================



-- Table de relation entre carte_resident et statut_cr
CREATE TABLE IF NOT EXISTS statut_carte_resident (
    id BIGSERIAL PRIMARY KEY,
    id_carte_resident BIGINT NOT NULL,
    id_statut_cr BIGINT NOT NULL,
    date_modif DATE NOT NULL DEFAULT CURRENT_DATE,
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_statut_carte_resident_carte FOREIGN KEY (id_carte_resident) REFERENCES carte_resident(id) ON DELETE CASCADE,
    CONSTRAINT fk_statut_carte_resident_statut FOREIGN KEY (id_statut_cr) REFERENCES statut_cr(id) ON DELETE RESTRICT,
    CONSTRAINT uk_statut_carte_resident UNIQUE (id_carte_resident, id_statut_cr)
);

-- Indexes pour optimiser les recherches
CREATE INDEX idx_statut_carte_resident_carte_id ON statut_carte_resident(id_carte_resident);
CREATE INDEX idx_statut_carte_resident_statut_id ON statut_carte_resident(id_statut_cr);
CREATE INDEX idx_statut_carte_resident_date ON statut_carte_resident(date_modif);

-- Commentaires
COMMENT ON TABLE statut_cr IS 'Table des statuts possibles d''une carte de résident';
COMMENT ON TABLE statut_carte_resident IS 'Table d''historique des changements de statut des cartes de résident';
COMMENT ON COLUMN statut_carte_resident.date_modif IS 'Date du changement de statut';
COMMENT ON COLUMN statut_carte_resident.date_creation IS 'Date d''enregistrement du changement';
