# 🎯 SPRINT 1 - Module de Demande de Visa

## 📋 Vue d'ensemble du Projet

Ce module implémente le système complet de gestion des demandes de visa pour le projet **Visa Back Office**. Le système gère :

- ✅ Création de demandes de visa
- ✅ Validation des champs obligatoires
- ✅ Gestion des pièces justificatives
- ✅ Attribution automatique du statut
- ✅ Suivi des demandes

---

## 🏗️ Structure du Projet

### Backend - Dossier `gestionVisa`

```
gestionVisa/
├── config/                          # Configurations Spring
│   └── WebConfig.java              # Configuration Web
│
├── controller/                       # Contrôleurs REST
│   ├── DemandeController.java        # Gestion des demandes
│   ├── DemandeurController.java      # Gestion des demandeurs
│   └── VisaTransformableController.java  # Gestion des visas
│
├── dto/                              # Data Transfer Objects
│   ├── DemandeRequestDTO.java        # Demande - Requête
│   ├── DemandeResponseDTO.java       # Demande - Réponse
│   ├── DemandeurDTO.java             # Demandeur
│   ├── VisaTransformableDTO.java     # Visa Transformable
│   └── PieceJustificativeDTO.java    # Pièce Justificative
│
├── enums/                            # Énumérations
│   ├── StatutDemande.java            # Statuts possibles
│   ├── TypeDemande.java              # Types de demande
│   ├── SituationFamiliale.java       # Situations familiales
│   └── TypePieceJustificative.java   # Types de pièces
│
├── model/                            # Entités JPA
│   ├── Demande.java                  # Demande de visa
│   ├── Demandeur.java                # Personne demandant
│   ├── VisaTransformable.java        # Visa initial
│   └── PieceJustificative.java       # Justificatifs
│
├── repository/                       # Accès Base de Données
│   ├── DemandeRepository.java
│   ├── DemandeurRepository.java
│   ├── VisaTransformableRepository.java
│   └── PieceJustificativeRepository.java
│
└── service/                          # Logique Métier
    ├── DemandeService.java           # Service principal
    ├── DemandeurService.java
    ├── VisaTransformableService.java
    └── PieceJustificativeService.java
```

---

## 🔐 Modèle de Données

### Relations

```
┌─────────────┐
│  DEMANDEUR  │
└──────┬──────┘
       │ (1,n)
       │
    ┌──┴──┐
    │     │
    │     └─────────────┐
    │                   │
┌───▼──────────────┐  ┌─▼────────────────┐
│ VISA_TRANSFORMABLE │ DEMANDE (relié aussi)
└────────────────┘  └──┬────────────────┘
                       │ (1,n)
                       │
                    ┌──▼────────────────┐
                    │ PIECE_JUSTIFICATIVE
                    └───────────────────┘
```

### Tables SQL

#### `demandeur`
- Informations d'état civil
- Adresse à Madagascar
- Situation familiale
- Nationalité

#### `visa_transformable`
- Référence unique du visa
- Dates (entrée, expiration)
- Lieu d'entrée
- Lié au demandeur

#### `demande`
- Type (Travailleur/Investisseur)
- Statut automatique
- Flags : pièces obligatoires / dossier complet
- Dates de création/modification/traitement

#### `piece_justificative`
- Type de pièce
- Fichier uploadé
- Flag : soumise ou non
- Lié à la demande

---

## 🎯 Règles Métier Implémentées

### 1️⃣ Validation des Données Obligatoires

**Demandeur :**
- ❌ Nom (obligatoire)
- ❌ Prénom (obligatoire)
- ❌ Date de naissance (obligatoire)
- ❌ Situation familiale (obligatoire)
- ❌ Nationalité (obligatoire)
- ❌ Adresse à Madagascar (obligatoire)

**Visa Transformable :**
- ❌ Référence (obligatoire, unique)
- ❌ Date d'entrée (obligatoire)
- ❌ Lieu d'entrée (obligatoire)
- ❌ Date d'expiration (obligatoire)

**Demande :**
- ❌ Type de demande (obligatoire)

### 2️⃣ Gestion des Pièces Justificatives

#### Pièces Obligatoires
```
PASSEPORT
CERTIFICAT_NAISSANCE
LETTRE_EMBAUCHE
CONTRAT_TRAVAIL
JUSTIFICATIF_DOMICILE
CASIER_JUDICIAIRE
JUSTIFICATIF_MOYENS
ASSURANCE_MALADIE
PHOTO_IDENTITE
```

#### Pièces Facultatives
```
ACTE_MARIAGE
ACTE_DIVORCE
CERTIFICAT_SANTE
RELEVE_COMPTE
PREUVE_INVESTISSEMENT
```

### 3️⃣ Attribution Automatique du Statut

| Condition | Statut | Flag Obligatoires | Flag Complet |
|-----------|--------|-------------------|--------------|
| Pièces obligatoires manquantes | `DOSSIER_CREE` | ❌ false | ❌ false |
| Toutes obligatoires présentes | `SOUMISE` | ✅ true | ❌ false |
| Toutes pièces (ob + fac) présentes | `SOUMISE` | ✅ true | ✅ true |

### 4️⃣ Cycle de Vie d'une Demande

```
DOSSIER_CREE ─(+ pièces)─> SOUMISE ─(traitement)─> EN_COURS
                                            │
                                    ┌───────┴────────┐
                                    ▼                ▼
                                VALIDEE         REJETEE
```

---

## 📡 API REST Disponible

### Demandeurs
```
POST   /api/demandeurs                  # Créer
GET    /api/demandeurs                  # Lister tous
GET    /api/demandeurs/{id}             # Récupérer un
PUT    /api/demandeurs/{id}             # Modifier
DELETE /api/demandeurs/{id}             # Supprimer
```

### Visas Transformables
```
POST   /api/visas-transformables               # Créer
GET    /api/visas-transformables               # Lister tous
GET    /api/visas-transformables/{id}          # Récupérer un
GET    /api/visas-transformables/reference/{ref}  # Par référence
PUT    /api/visas-transformables/{id}          # Modifier
DELETE /api/visas-transformables/{id}          # Supprimer
```

### Demandes de Visa ⭐
```
POST   /api/demandes/enregistrer               # Créer + validation automatique
GET    /api/demandes                           # Lister toutes
GET    /api/demandes/{id}                      # Récupérer une
GET    /api/demandes/statut/{statut}           # Filtrer par statut
POST   /api/demandes/{demandeId}/pieces        # Ajouter pièce
PUT    /api/demandes/pieces/{pieceId}/soumettre  # Soumettre pièce
GET    /api/demandes/{demandeId}/pieces        # Lister pièces
PUT    /api/demandes/{id}/statut               # Changer statut
DELETE /api/demandes/{id}                      # Supprimer
```

---

## 🚀 Démarrage Rapide

### 1. Prérequis
```bash
- Java 17+
- Maven 3.6+
- PostgreSQL 12+
```

### 2. Configuration PostgreSQL
```bash
# Créer la base et l'utilisateur
psql -U postgres
CREATE DATABASE visa_db;
CREATE USER visa_user WITH PASSWORD 'visa_password';
GRANT ALL PRIVILEGES ON DATABASE visa_db TO visa_user;
```

### 3. Configurer l'Application
Éditer `backend/src/main/resources/application.yml` :
```yaml
datasource:
  url: jdbc:postgresql://localhost:5432/visa_db
  username: visa_user
  password: visa_password
```

### 4. Démarrer
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

L'application démarre sur `http://localhost:8080`

---

## 🧪 Exemple d'Utilisation

### Étape 1 : Créer une Demande

```bash
curl -X POST http://localhost:8080/api/demandes/enregistrer \
  -H "Content-Type: application/json" \
  -d '{
    "demandeur": {
      "nom": "Dupont",
      "prenom": "Jean",
      "dateNaissance": "1990-05-15",
      "situationFamiliale": "MARIE",
      "nationalite": "Française",
      "adresseMadagascar": "123 Rue de la Paix, Antananarivo"
    },
    "visaTransformable": {
      "reference": "VISA-001",
      "demandeurId": 1,
      "dateEntree": "2024-01-15",
      "lieuEntree": "Ivato",
      "dateExpiration": "2025-01-15"
    },
    "typeDemande": "TRAVAILLEUR",
    "piecesJustificatives": []
  }'
```

**Résultat :** 
- ✅ Demande créée avec `statut = DOSSIER_CREE`
- ℹ️ Message : "Dossier créé. Pièces obligatoires manquantes."

### Étape 2 : Ajouter des Pièces

```bash
curl -X POST http://localhost:8080/api/demandes/1/pieces \
  -H "Content-Type: application/json" \
  -d '{
    "typePiece": "PASSEPORT",
    "nomFichier": "passeport.pdf",
    "sousmise": true
  }'
```

### Étape 3 : Statut Automatiquement Mis à Jour

Une fois **toutes les pièces obligatoires** ajoutées :
```bash
curl -X GET http://localhost:8080/api/demandes/1
```

**Résultat :**
```json
{
  "id": 1,
  "statut": "SOUMISE",
  "piecesObligatoiresCompletes": true,
  "dossierComplet": false,
  "message": "Toutes les pièces obligatoires sont complètes"
}
```

---

## 📊 Schéma de la Base de Données

```sql
-- Voir backend/src/main/resources/db/migration/V1__CreateVisaDemandTables.sql
```

**Fichier de migration Flyway :** Crée automatiquement la structure au démarrage

---

## 🔍 Points Clés de l'Implémentation

### ✅ Validation Côté Serveur
Tous les champs obligatoires sont validés avant l'enregistrement.

### ✅ Logique Métier Centralisée
La classe `DemandeService` contient :
- Validation des données
- Calcul du statut
- Vérification des pièces

### ✅ Statut Automatique
Le statut est calculé à la création et revérifié après chaque modification.

### ✅ Transactions
Les opérations critiques sont transactionnelles (atomicité garantie).

### ✅ Indexes Optimisés
Créés sur les colonnes fréquemment recherchées.

### ✅ Réponses Cohérentes
Tous les endpoints retournent :
```json
{
  "success": true/false,
  "message": "...",
  "data": {...}
}
```

---

## 📚 Documentation Complète

- **API Documentation** : `VISA_API_DOCUMENTATION.md`
- **Quick Start** : `QUICK_START.md`
- **Javadoc** : Dans le code source

---

## 🤝 Contribution

1. Suivre la structure existante
2. Ajouter les annotations Spring appropriées
3. Documenter les méthodes publiques
4. Valider avec les tests existants

---

## 📞 Support

Pour toute question sur ce module :
- Consultez la documentation
- Vérifiez les logs
- Revérifiez la configuration PostgreSQL

---

**Status du Sprint 1 :** ✅ COMPLÉTÉ  
**Date de Création :** 2024  
**Responsable du Module :** Équipe Backend  
