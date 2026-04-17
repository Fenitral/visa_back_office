CREATE TABLE Nationalite (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(50) NOT NULL
);

CREATE TABLE Situation_familiale (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(50) NOT NULL
);

CREATE TABLE type_visa (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(50) NOT NULL  -- 'investisseur', 'travailleur'
);

-- @@@@@@@@@@@@@@@
CREATE TABLE piece_justificatives (
    id_piece_justificatives SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    nombre INT NOT NULL
);

CREATE TABLE piece_typeVisa (
    id_piece_justificatives INT NOT NULL,
    id_type_visa INT NOT NULL,
    PRIMARY KEY (id_piece_justificatives, id_type_visa),
    FOREIGN KEY (id_piece_justificatives) REFERENCES piece_justificatives(id_piece_justificatives),
    FOREIGN KEY (id_type_visa) REFERENCES type_visa(id)
);

-- @@@@@@@@@@@@@@@@@@@@@@

CREATE TABLE Demandeur (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    date_naissance DATE NOT NULL,
    lieu_naissance VARCHAR(100) NOT NULL,
    telephone VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    adresse TEXT NOT NULL,
    
    id_situation_familiale INT NOT NULL,
    id_nationalite INT NOT NULL,
    
    FOREIGN KEY (id_situation_familiale) REFERENCES Situation_familiale(id),
    FOREIGN KEY (id_nationalite) REFERENCES Nationalite(id)
);

CREATE TABLE Passeport (
    id SERIAL PRIMARY KEY,
    id_demandeur INT NOT NULL,
    numero_passeport VARCHAR(50) NOT NULL UNIQUE,
    date_delivrance DATE NOT NULL,
    date_expiration DATE NOT NULL,
    pays_delivrance VARCHAR(100),
    FOREIGN KEY (id_demandeur) REFERENCES Demandeur(id)
);

CREATE TABLE Statut_passeport (
    id SERIAL PRIMARY KEY,
    id_passeport INT NOT NULL,
    statut INT NOT NULL,-- 'actif', 'expire', 'perdu', 'volee'
    date_changement_statut DATE,
    FOREIGN KEY (id_passeport) REFERENCES Passeport(id)
);

CREATE TABLE Visa_transformable (
    id SERIAL PRIMARY KEY,
    id_demandeur INT NOT NULL,
    id_passeport INT NOT NULL,
    numero_reference VARCHAR(50) NOT NULL UNIQUE,
    FOREIGN KEY (id_demandeur) REFERENCES Demandeur(id),
    FOREIGN KEY (id_passeport) REFERENCES Passeport(id)
);

CREATE TABLE Type_demande (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(50) NOT NULL  
);

CREATE TABLE Demande (
    id SERIAL PRIMARY KEY,
    id_visa_transformable INT NOT NULL,
    date_demande DATE NOT NULL,
    id_statut INT NOT NULL,
    id_demandeur INT NOT NULL,
    id_type_visa INT NOT NULL,
    id_type_demande INT NOT NULL,
    date_traitement DATE,
    FOREIGN KEY (id_type_demande) REFERENCES Type_demande(id),
    FOREIGN KEY (id_demandeur) REFERENCES Demandeur(id),
    FOREIGN KEY (id_type_visa) REFERENCES type_visa(id),
    FOREIGN KEY (id_visa_transformable) REFERENCES Visa_transformable(id)
);

CREATE TABLE Visa (
    id SERIAL PRIMARY KEY,
    id_demande INT NOT NULL,
    reference VARCHAR(50),
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    id_passeport INT NOT NULL,
    FOREIGN KEY (id_passeport) REFERENCES Passeport(id),
    FOREIGN KEY (id_demande) REFERENCES Demande(id)
);

CREATE TABLE carte_resident (
    id SERIAL PRIMARY KEY,
    id_demande INT NOT NULL,
    reference VARCHAR(50),
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    id_passeport INT NOT NULL,
    FOREIGN KEY (id_demande) REFERENCES Demande(id),
    FOREIGN KEY (id_passeport) REFERENCES Passeport(id)
);

CREATE TABLE Statut_demande (
    id SERIAL PRIMARY KEY,
    id_demande INT NOT NULL,
    statut INT NOT NULL,-- 'brouillon', 'soumise', 'en_cours', 'validee', 'rejetee'
    date_changement_statut DATE,
    FOREIGN KEY (id_demande) REFERENCES Demande(id)
);