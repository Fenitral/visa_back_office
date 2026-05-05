-- Add TRANSFERT_VISA type demande
INSERT INTO type_demande (libelle)
VALUES ('TRANSFERT_VISA')
ON CONFLICT (libelle) DO NOTHING;
