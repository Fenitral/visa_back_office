# SPRINT 4: Génération et Suivi des Demandes via QR Code 🎯

## ✨ Objectif Sprint

Implémenter un système complet de **génération et suivi de demandes de visa via QR code**.

Chaque demande reçoit un QR code unique contenant une URL. Quand on scanne ce code:
- On accède à une **page publique** qui affiche le statut et l'historique
- Aucune authentification requise - idéal pour partager par SMS/email

---

## 📋 Ce qui a été livré

### ✅ 1. Backend - Endpoints REST API

**Fichiers modifiés/créés:**
- ✅ `DemandeRestController.java` - **NOUVEAU** 
- ✅ `HistoriqueStatutDTO.java` - **NOUVEAU**
- ✅ `DemandeResponseDTO.java` - Modifié (ajout des historiques)
- ✅ `DemandeMapper.java` - Modifié (mapping des historiques)

**Endpoints créés:**
```
GET /api/demandes              → Liste de toutes les demandes (JSON)
GET /api/demandes/{id}         → Détails d'une demande + historique (JSON)
```

**Port:** `http://localhost:8084`

**CORS:** Activé pour permettre les requêtes Vue.js

### ✅ 2. Frontend - Application Vue.js complète

**Localisation:** `d:/.../.../frontend/`

**Technos:**
- Vue 3
- Vite (build tool moderne)
- Axios (API HTTP)
- qrcode.vue (génération de QR codes)

**Structure:**
```
frontend/
├── src/
│   ├── App.vue                         # Composant racine + navigation
│   ├── main.js                         # Point d'entrée
│   └── views/
│       ├── ListeDemandes.vue           # Liste de toutes les demandes
│       ├── DetailsDemande.vue          # Détails + QR code
│       └── FollowUp.vue                # Page publique de suivi
├── index.html                          # HTML principal
├── package.json                        # Dépendances
├── vite.config.js                      # Configuration Vite + proxy API
├── .gitignore                          # Fichiers à ignorer
└── README.md                           # Documentation complète
```

---

## 🚀 Guide de Démarrage

### Prérequis
- ✅ Backend Spring Boot lancé (`npm run dev` ou `mvn spring-boot:run`)
- ✅ Node.js 16+ installé

### Étapes de lancement

#### 1️⃣ Lancer le Backend
```bash
cd backend
mvn spring-boot:run
# ou si déjà compilé:
mvn spring-boot:run
```
✅ Accessible sur: `http://localhost:8084`

#### 2️⃣ Lancer le Frontend
```bash
cd frontend
npm install              # Première fois seulement
npm run dev             # Développement
```
✅ Accessible sur: `http://localhost:5173`

---

## 📱 Comment ça fonctionne

### Phase 1: Consulter les demandes
```
1. Aller à http://localhost:5173
2. Voir la liste de toutes les demandes
3. Cliquer sur une demande
```

### Phase 2: Consulter les détails
```
1. Page de détails s'affiche avec:
   - Infos du demandeur
   - Historique des statuts en timeline
   - Liste des pièces justificatives
2. Le QR CODE est généré automatiquement
```

### Phase 3: Scanner le QR Code
```
1. Avec un téléphone, scanner le QR code
   (ou copier l'URL affichée)
2. Ça ouvre une URL unique: http://localhost:5173/?demande=123
3. La page "FollowUp" s'affiche automatiquement
4. Affichage public du statut et historique
```

### Architecture du QR Code
```
QR Code encodé avec URL:
├── Base: http://localhost:5173 (ou domaine prod)
├── Paramètre: ?demande={ID}
└── Frontend détecte le paramètre et affiche FollowUp.vue
```

---

## 🎨 Parcours Utilisateur Complet

```
DEMANDEUR INITIAL (Bureau)
    ↓
[Créer une demande]
    ↓
Demande reçoit: ID=123
    ↓
Consultation sur http://localhost:5173
    ├── Voir sa demande #123
    ├── Voir les détails
    └── Voir le QR CODE
    ↓
[Scanner le QR Code avec téléphone]
    ↓
URL: http://localhost:5173/?demande=123
    ↓
TIERS (Famille, Ami, Consulat)
    └── Peut consulter le statut en TEMPS RÉEL
        - Sans authentification
        - Historique complet visible
        - État des documents
```

---

## 📊 Statuts des demandes

| Statut | Couleur | Description |
|--------|---------|-------------|
| CREE | 🟡 Jaune | Demande créée |
| SCANNEE | 🔵 Cyan | Documents reçus |
| APPROUVEE | 🟢 Vert | Visa approuvé ✓ |
| REJETEE | 🔴 Rouge | Visa rejeté ✗ |

---

## 📄 Données retournées par l'API

### GET /api/demandes
```json
[
  {
    "id": 123,
    "dateDemande": "2026-05-06T10:30:00",
    "nomDemandeur": "Dupont",
    "prenomDemandeur": "Jean",
    "numeroPasSeport": "AB123456",
    "referenceVisa": "VT-2026-001",
    "typeVisa": "Travailleur",
    "typeDemande": "NOUVELLE",
    "statutDemande": "SCANNEE",
    "pieces": [...],
    "historiques": [...]
  }
]
```

### GET /api/demandes/{id}
```json
{
  "id": 123,
  "dateDemande": "2026-05-06T10:30:00",
  "nomDemandeur": "Dupont",
  "prenomDemandeur": "Jean",
  "numeroPasSeport": "AB123456",
  "referenceVisa": "VT-2026-001",
  "typeVisa": "Travailleur",
  "typeDemande": "NOUVELLE",
  "statutDemande": "SCANNEE",
  "pieces": [
    {
      "idPiece": 1,
      "nomPiece": "Passeport",
      "obligatoire": true,
      "fourni": true
    }
  ],
  "historiques": [
    {
      "id": 1,
      "statut": "CREE",
      "dateChangement": "2026-05-06T10:30:00",
      "commentaire": "Création de la demande"
    },
    {
      "id": 2,
      "statut": "SCANNEE",
      "dateChangement": "2026-05-06T11:00:00",
      "commentaire": "Tous les documents reçus"
    }
  ]
}
```

---

## 🔒 Sécurité

⚠️ **Important:** La page FollowUp est PUBLIC (pas d'authentification)
- ✅ OK pour afficher le statut et historique
- ✅ OK pour afficher les pièces
- ❌ PAS d'info sensible révélée
- 💡 ID est simplement un numéro - pas un secret

**Pour la production:** Ajouter une clé de sécurité (ex: token dans le QR code)

---

## 🛠️ Architecture Technique

```
┌─────────────────────────────────────────────────────┐
│          Frontend Vue.js (Port 5173)                │
│  ┌─────────────────────────────────────────────┐    │
│  │ App.vue (Navigation + Routing)              │    │
│  │ ├── ListeDemandes.vue (Grid de demandes)   │    │
│  │ ├── DetailsDemande.vue (Détails + QR)      │    │
│  │ └── FollowUp.vue (Suivi public)             │    │
│  └─────────────────────────────────────────────┘    │
│            ↓ (Axios + Proxy)                        │
│  ┌─────────────────────────────────────────────┐    │
│  │ Backend Spring Boot (Port 8084)             │    │
│  │ ├── DemandeRestController                   │    │
│  │ │   ├── GET /api/demandes                   │    │
│  │ │   └── GET /api/demandes/{id}              │    │
│  │ ├── DemandeService (métier existant)        │    │
│  │ └── DB (Historiques, Demandes, Pièces)      │    │
│  └─────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────┘
```

---

## 📦 Dépendances Frontend

```json
{
  "dependencies": {
    "vue": "^3.4.0",           // Framework Vue
    "axios": "^1.6.0",          // Client HTTP
    "qrcode.vue": "^3.4.0"      // QR code generator
  },
  "devDependencies": {
    "@vitejs/plugin-vue": "^5.0.0",
    "vite": "^5.0.0"
  }
}
```

---

## ✅ Checklist de Déploiement

- [ ] Backend compilé et lancé (`mvn clean compile` OK)
- [ ] Frontend dépendances installées (`npm install` OK)
- [ ] Backend accessible sur `http://localhost:8084`
- [ ] Frontend accessible sur `http://localhost:5173`
- [ ] Endpoints API testés (`/api/demandes` retourne JSON)
- [ ] QR codes générés correctement
- [ ] Page FollowUp accessible via paramètre URL
- [ ] CORS activé dans le backend

---

## 🎯 Prochaines étapes (Future)

- [ ] Déploiement production (Docker, Kubernetes)
- [ ] Domaine personnalisé dans les QR codes
- [ ] Notification par SMS quand statut change
- [ ] Export PDF du QR code
- [ ] Authentification admin
- [ ] Dashboard statistiques
- [ ] Multilingue (EN, FR, AR...)

---

## 📞 Support

Pour toute question:
1. Consulter le README.md dans `frontend/`
2. Vérifier les logs du backend: `mvn clean compile`
3. Vérifier les logs du frontend: `npm run dev`

---

**Sprint 4 - Mai 2026** | Système Complet de Suivi via QR Code
