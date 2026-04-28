CREATE TABLE statutCR(
   Id_visa_statut COUNTER,
   libelle VARCHAR(50) NOT NULL,
   PRIMARY KEY(Id_visa_statut)
);

CREATE TABLE statutP(
   Id_passport_statut COUNTER,
   libelle VARCHAR(50) NOT NULL,
   PRIMARY KEY(Id_passport_statut)
);

CREATE TABLE statut_passport(
   Id_passport INT,
   Id_passport_statut INT,
   date_modif DATE,
   PRIMARY KEY(Id_passport, Id_passport_statut),
   FOREIGN KEY(Id_passport) REFERENCES passport(Id_passport),
   FOREIGN KEY(Id_passport_statut) REFERENCES statutP(Id_passport_statut)
);

CREATE TABLE statut_carteResident(
   Id_carte_resident INT,
   Id_visa_statut INT,
   date_modif DATE,
   PRIMARY KEY(Id_carte_resident, Id_visa_statut),
   FOREIGN KEY(Id_carte_resident) REFERENCES carte_resident(Id_carte_resident),
   FOREIGN KEY(Id_visa_statut) REFERENCES statutCR(Id_visa_statut)
);

<!-- transforme cette script en un sql valable pour postgres stp  -->

CREATE TABLE statut_carteResident(
   Id_carte_resident INT,
   Id_visa_statut INT,
   date_modif DATE,
   PRIMARY KEY(Id_carte_resident, Id_visa_statut),
   FOREIGN KEY(Id_carte_resident) REFERENCES carte_resident(Id_carte_resident),
   FOREIGN KEY(Id_visa_statut) REFERENCES statutCR(Id_visa_statut)
);

nous allons commencer par le perte de carte de resident d'abord : 
donc, on va creer une table comme ceci 

CREATE TABLE statut_carteResident(
   Id_carte_resident INT,
   Id_visa_statut INT,
   date_modif DATE,
   PRIMARY KEY(Id_carte_resident, Id_visa_statut),
   FOREIGN KEY(Id_carte_resident) REFERENCES carte_resident(Id_carte_resident),
   FOREIGN KEY(Id_visa_statut) REFERENCES statutCR(Id_visa_statut)
);

mais ce sera entre carte de resident et type_demande : 
puis, creer moi le back-end : 
afin de realiser ceci (l'image que j'ai collee.)
