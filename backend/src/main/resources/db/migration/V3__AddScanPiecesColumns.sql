-- ============================================
-- MIGRATION V3: AJOUT DES COLONNES DE SCAN
-- SPRINT 3: Scan des pièces justificatives
-- ============================================

-- Ajouter les colonnes pour supporter le scan/upload des pièces justificatives
ALTER TABLE demande_piece
ADD COLUMN IF NOT EXISTS chemin_fichier VARCHAR(500),
ADD COLUMN IF NOT EXISTS nom_fichier VARCHAR(255),
ADD COLUMN IF NOT EXISTS date_upload TIMESTAMP;

-- Ajouter un nouveau statut "SCANNEE" pour les demandes dont toutes les pièces ont été scannées
INSERT INTO statut_demande (libelle)
VALUES ('SCANNEE')
ON CONFLICT (libelle) DO NOTHING;
