# Documentation API - Gestion des Demandes de Visa

## 📋 Vue d'ensemble

Cette API permet de gérer les demandes de visa avec les règles métier suivantes :
- Validation des champs obligatoires
- Vérification des pièces justificatives
- Attribution automatique du statut
- Gestion des demandes (création, modification, consultation)

## 🏗️ Architecture

### Structure du Projet
```
gestionVisa/
├── config/          # Configurations Spring
├── controller/      # Contrôleurs REST
├── dto/            # Objets de transfert de données
├── enums/          # Énumérations
├── model/          # Entités JPA
├── repository/     # Accès à la base de données
└── service/        # Logique métier
```

## 🔑 Énumérations

### TypeDemande
- `TRAVAILLEUR` - Visa Travailleur
- `INVESTISSEUR` - Visa Investisseur

### StatutDemande
- `DOSSIER_CREE` - Dossier créé (pièces obligatoires manquantes)
- `SOUMISE` - Demande soumise (pièces obligatoires complètes)
- `EN_COURS` - En cours de traitement
- `VALIDEE` - Demande validée
- `REJETEE` - Demande rejetée

### SituationFamiliale
- `CELIBATAIRE` - Célibataire
- `MARIE` - Marié(e)
- `DIVORCE` - Divorcé(e)
- `VEUF` - Veuf(ve)
- `UNION_LIBRE` - Union libre
- `PACS` - Pacsé(e)

### TypePieceJustificative
Les pièces justificatives sont divisées en :

**Obligatoires :**
- `PASSEPORT`
- `CERTIFICAT_NAISSANCE`
- `LETTRE_EMBAUCHE`
- `CONTRAT_TRAVAIL`
- `JUSTIFICATIF_DOMICILE`
- `CASIER_JUDICIAIRE`
- `JUSTIFICATIF_MOYENS`
- `ASSURANCE_MALADIE`
- `PHOTO_IDENTITE`

**Facultatives :**
- `ACTE_MARIAGE`
- `ACTE_DIVORCE`
- `CERTIFICAT_SANTE`
- `RELEVE_COMPTE`
- `PREUVE_INVESTISSEMENT`

## 📡 Endpoints

### 1. Demandeurs

#### Créer un demandeur
```
POST /api/demandeurs
Content-Type: application/json

{
  "nom": "Dupont",
  "prenom": "Jean",
  "nomJeuneFille": "Martin",
  "dateNaissance": "1990-05-15",
  "situationFamiliale": "MARIE",
  "nationalite": "Française",
  "profession": "Ingénieur",
  "adresseMadagascar": "123 Rue de la Paix, Antananarivo",
  "telephone": "+261381234567"
}
```

#### Récupérer un demandeur
```
GET /api/demandeurs/{id}
```

#### Récupérer tous les demandeurs
```
GET /api/demandeurs
```

#### Mettre à jour un demandeur
```
PUT /api/demandeurs/{id}
Content-Type: application/json

{
  "nom": "Dupont",
  "prenom": "Jean",
  ...
}
```

#### Supprimer un demandeur
```
DELETE /api/demandeurs/{id}
```

---

### 2. Visas Transformables

#### Créer un visa transformable
```
POST /api/visas-transformables
Content-Type: application/json

{
  "reference": "VISA-2024-001",
  "demandeurId": 1,
  "dateEntree": "2024-01-15",
  "lieuEntree": "Ivato",
  "dateExpiration": "2025-01-15"
}
```

#### Récupérer un visa par ID
```
GET /api/visas-transformables/{id}
```

#### Récupérer un visa par référence
```
GET /api/visas-transformables/reference/{reference}
```

#### Récupérer tous les visas
```
GET /api/visas-transformables
```

#### Mettre à jour un visa
```
PUT /api/visas-transformables/{id}
Content-Type: application/json

{
  "reference": "VISA-2024-001",
  "demandeurId": 1,
  "dateEntree": "2024-01-15",
  "lieuEntree": "Ivato",
  "dateExpiration": "2025-01-15"
}
```

#### Supprimer un visa
```
DELETE /api/visas-transformables/{id}
```

---

### 3. Demandes de Visa

#### Créer une demande de visa
```
POST /api/demandes/enregistrer
Content-Type: application/json

{
  "demandeur": {
    "nom": "Dupont",
    "prenom": "Jean",
    "dateNaissance": "1990-05-15",
    "situationFamiliale": "MARIE",
    "nationalite": "Française",
    "adresseMadagascar": "123 Rue de la Paix, Antananarivo",
    "telephone": "+261381234567",
    "profession": "Ingénieur"
  },
  "visaTransformable": {
    "reference": "VISA-2024-001",
    "demandeurId": 1,
    "dateEntree": "2024-01-15",
    "lieuEntree": "Ivato",
    "dateExpiration": "2025-01-15"
  },
  "typeDemande": "TRAVAILLEUR",
  "piecesJustificatives": [
    {
      "typePiece": "PASSEPORT",
      "nomFichier": "passeport.pdf",
      "sousmise": true
    },
    {
      "typePiece": "CERTIFICAT_NAISSANCE",
      "nomFichier": "naissance.pdf",
      "sousmise": true
    }
  ]
}
```

#### Récupérer une demande
```
GET /api/demandes/{id}
```

#### Récupérer toutes les demandes
```
GET /api/demandes
```

#### Récupérer les demandes par statut
```
GET /api/demandes/statut/{statut}

Exemples de valeurs pour {statut}:
- DOSSIER_CREE
- SOUMISE
- EN_COURS
- VALIDEE
- REJETEE
```

#### Ajouter une pièce justificative
```
POST /api/demandes/{demandeId}/pieces
Content-Type: application/json

{
  "typePiece": "PASSEPORT",
  "nomFichier": "passeport_scan.pdf",
  "cheminFichier": "/uploads/documents/123456/passeport.pdf",
  "sousmise": false
}
```

#### Soumettre une pièce justificative
```
PUT /api/demandes/pieces/{pieceId}/soumettre
```

#### Récupérer les pièces d'une demande
```
GET /api/demandes/{demandeId}/pieces
```

#### Mettre à jour le statut d'une demande
```
PUT /api/demandes/{id}/statut
Content-Type: application/json

{
  "statut": "EN_COURS"
}
```

#### Supprimer une demande
```
DELETE /api/demandes/{id}
```

---

## 📊 Règles Métier

### Attribution du Statut

#### Cas 1 : Pièces Obligatoires Manquantes
- **Condition** : Une ou plusieurs pièces obligatoires ne sont pas soumises
- **Statut** : `DOSSIER_CREE`
- **Action** : La demande est enregistrée mais ne peut pas être traitée

#### Cas 2 : Pièces Obligatoires Complètes
- **Condition** : Toutes les pièces obligatoires sont soumises
- **Statut** : `SOUMISE`
- **Action** : La demande peut être traitée par le ministère

#### Cas 3 : Dossier Complet
- **Condition** : Toutes les pièces (obligatoires ET facultatives) sont soumises
- **Statut** : `SOUMISE` (avec `dossierComplet = true`)
- **Action** : Traitement prioritaire

---

## 🔄 Flux de Traitement

```
1. Création du demandeur
   └─> Récupération ou création

2. Récupération du visa transformable
   └─> Vérification de l'existence

3. Création de la demande
   └─> Association demandeur + visa

4. Création des pièces justificatives
   └─> Enregistrement dans la base

5. Calcul du statut
   ├─> Vérification pièces obligatoires
   └─> Attribution du statut automatique

6. Enregistrement de la demande
   └─> Retour du résultat
```

---

## 🎯 Exemples de Flux Complets

### Exemple 1 : Création d'une demande avec dossier incomplet

```
POST /api/demandes/enregistrer

Réponse :
{
  "success": true,
  "message": "Demande enregistrée avec succès",
  "demandeId": 1,
  "statut": "DOSSIER_CREE",
  "messageDetail": "Dossier créé. Pièces obligatoires manquantes. Veuillez compléter votre dossier."
}
```

### Exemple 2 : Ajout de pièces justificatives

```
POST /api/demandes/1/pieces
{
  "typePiece": "PASSEPORT",
  "nomFichier": "passeport.pdf",
  "sousmise": true
}

Réponse :
{
  "success": true,
  "message": "Pièce justificative ajoutée avec succès",
  "piece": { ... }
}

// Le statut est automatiquement revérifié
// Si toutes les pièces obligatoires sont complètes → statut passe à SOUMISE
```

### Exemple 3 : Mise à jour du statut

```
PUT /api/demandes/1/statut
{
  "statut": "EN_COURS"
}

Réponse :
{
  "success": true,
  "message": "Statut mis à jour avec succès",
  "demande": {
    "id": 1,
    "statut": "EN_COURS",
    ...
  }
}
```

---

## 🔐 Gestion des Erreurs

Tous les endpoints retournent une réponse structurée :

**Succès (200, 201) :**
```json
{
  "success": true,
  "message": "Description du succès",
  "data": { ... }
}
```

**Erreur (400, 404, 500) :**
```json
{
  "success": false,
  "message": "Description de l'erreur",
  "error": "Détail technique"
}
```

---

## 📝 Configurations Requises

### application.yml
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/visa_db
    username: postgres
    password: password
    driver-class-name: org.postgresql.Driver
  
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect

  flyway:
    enabled: true
    locations: classpath:db/migration
```

### Dépendances Maven
```xml
<!-- PostgreSQL Driver -->
<dependency>
  <groupId>org.postgresql</groupId>
  <artifactId>postgresql</artifactId>
  <version>42.7.0</version>
</dependency>

<!-- Flyway for migrations -->
<dependency>
  <groupId>org.flywaydb</groupId>
  <artifactId>flyway-core</artifactId>
</dependency>
```

---

## 🧪 Tests

Les endpoints peuvent être testés avec :
- `curl`
- Postman
- Insomnia
- Thunder Client

---

## 📌 Notes Importantes

1. **Validation** : Tous les champs obligatoires sont validés côté serveur
2. **Statut Automatique** : Le statut est calculé automatiquement selon les pièces
3. **Révérification** : Après chaque ajout de pièce, le statut est automatiquement révérifié
4. **Transactions** : Les opérations critiques sont transactionnelles
5. **Dates** : Toujours en format ISO 8601 (YYYY-MM-DD)
6. **Timestamps** : Générés automatiquement par la base de données

---

## 📞 Support

Pour toute question ou problème, contactez l'équipe de développement du projet.
