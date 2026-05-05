-- Add APPROUVEE status for demande validation without antecedents
INSERT INTO statut_demande (libelle)
VALUES ('APPROUVEE')
ON CONFLICT (libelle) DO NOTHING;
