# 🏗️ SPRINT 4 - Architecture Complète

## 📐 Architecture Générale

```
┌─────────────────────────────────────────────────────────────────────┐
│                     CLIENTS                                          │
│  ┌──────────────────┐  ┌──────────────────┐  ┌───────────────────┐  │
│  │  Web Browser     │  │  Mobile Phone    │  │  Admin Dashboard  │  │
│  │  (Demandes)      │  │  (Scan QR Code)  │  │  (Backend Portal) │  │
│  └────────┬─────────┘  └────────┬─────────┘  └─────────┬─────────┘  │
└───────────┼──────────────────────┼──────────────────────┼────────────┘
            │                      │                      │
            ├─────────────────────┼──────────────────────┤
            │                     │                      │
   ┌────────▼────────┐  ┌────────▼──────┐    ┌──────────▼────────┐
   │ Frontend Vue.js │  │ Page Publique │    │  MVC Templates    │
   │ (Port 5173)     │  │  FollowUp.vue │    │  (Existant)       │
   │                 │  │ (?demande=X)  │    │  (Port 8084)      │
   └────────┬────────┘  └────────┬──────┘    └──────────┬────────┘
            │                     │                      │
            ├─────────────────────┼──────────────────────┤
            │                     │                      │
            │             ┌───────▼─────────┐           │
            │             │ Nginx/Apache    │           │
            │             │ Load Balancer   │           │
            │             └───────┬─────────┘           │
            │                     │                      │
            └─────────────────────┼──────────────────────┘
                                  │
                ┌─────────────────▼─────────────────┐
                │  Backend Spring Boot (8084)       │
                │  ┌──────────────────────────────┐ │
                │  │ DemandeRestController         │ │
                │  │ ├── GET /api/demandes        │ │
                │  │ └── GET /api/demandes/{id}   │ │
                │  ├──────────────────────────────┤ │
                │  │ DemandeService               │ │
                │  │ ├── getToutesDemandes()      │ │
                │  │ ├── getDemande(id)           │ │
                │  │ └── creerHistoriqueStatut()  │ │
                │  ├──────────────────────────────┤ │
                │  │ Repository Layer             │ │
                │  │ ├── DemandeRepository        │ │
                │  │ ├── HistoriqueStatutRepository│ │
                │  │ └── DemandePieceRepository   │ │
                │  └──────────────────────────────┘ │
                └─────────────────┬─────────────────┘
                                  │
                  ┌───────────────▼────────────────┐
                  │   Database (MySQL/PostgreSQL)  │
                  │   ├── demande                  │
                  │   ├── historique_statut        │
                  │   ├── demande_piece            │
                  │   ├── piece                    │
                  │   ├── type_visa                │
                  │   └── statut_demande           │
                  └────────────────────────────────┘
```

---

## 🔄 Flux de Données

### Flux 1: Liste des demandes

```
User ouvre http://localhost:5173
    ↓
App.vue → ListeDemandes.vue
    ↓
Axios: GET /api/demandes
    ↓
Backend: DemandeRestController.listerDemandes()
    ↓
DemandeService.getToutesDemandes()
    ↓
DemandeRepository.findAll()
    ↓
Database: SELECT * FROM demande
    ↓
Mapper: Demande → DemandeResponseDTO
    ↓
JSON Response [{ id: 1, nom: "Dupont", ... }]
    ↓
Frontend: render demandes-grid
    ↓
User voit liste avec cartes cliquables
```

### Flux 2: Détails + QR Code

```
User clique sur demande #123
    ↓
App.vue → DetailsDemande.vue (demandeId=123)
    ↓
Axios: GET /api/demandes/123
    ↓
Backend: DemandeRestController.obtenirDemande(123)
    ↓
DemandeService.getDemande(123)
    ↓
DemandeRepository.findById(123)
    ↓
Database: SELECT * FROM demande WHERE id=123
         INNER JOIN historique_statut
         INNER JOIN demande_piece
    ↓
Mapper: Demande → DemandeResponseDTO (avec historiques)
    ↓
JSON Response {
  id: 123,
  nom: "Dupont",
  historiques: [
    { statut: "CREE", date: "2026-05-06", ... },
    { statut: "SCANNEE", date: "2026-05-07", ... }
  ],
  pieces: [...]
}
    ↓
Frontend: render détails
    ↓
qrcode-vue généère: http://localhost:5173/?demande=123
    ↓
User voit tout (infos + QR code + historique)
```

### Flux 3: Scan QR → Suivi Public

```
User scanne QR code avec téléphone
    ↓
Browser ouvre: http://localhost:5173/?demande=123
    ↓
App.vue détecte paramètre ?demande=123
    ↓
App.vue → FollowUp.vue (demandeId=123)
    ↓
Axios: GET /api/demandes/123
    ↓
(Même flux que Flux 2)
    ↓
Frontend: render page FollowUp publique
    ↓
User voit:
  - Statut BIG
  - Timeline historique
  - Documents avec checkmarks
  - Message contextuel
```

---

## 📦 Composants & Responsabilités

### Backend

#### `DemandeRestController`
- **Rôle:** Exposer les endpoints REST
- **Méthodes:**
  - `GET /api/demandes` → liste
  - `GET /api/demandes/{id}` → détails

#### `DemandeService`
- **Rôle:** Logique métier
- **Méthodes:**
  - `getToutesDemandes()` - retourne toutes
  - `getDemande(id)` - retourne une

#### `DemandeResponseDTO`
- **Rôle:** Transfert de données
- **Champs:**
  - id, dateDemande, nomDemandeur, ...
  - **historiques** ← Nouveau

#### `DemandeMapper`
- **Rôle:** Conversion Demande → DTO
- **Mappings:**
  - entité.historiques → dto.historiques ← Nouveau

---

### Frontend

#### `App.vue`
- **Rôle:** Composant racine, navigation
- **État:**
  - currentPage (list/details/followup)
  - selectedDemandeId

#### `ListeDemandes.vue`
- **Rôle:** Afficher toutes les demandes
- **Données:** Array<Demande> depuis API
- **Actions:** Clic → ouvre details

#### `DetailsDemande.vue`
- **Rôle:** Afficher détails + QR code
- **Données:** Demande complète avec historiques
- **Générations:** QR code automatique
- **Actions:** Copie URL, retour

#### `FollowUp.vue`
- **Rôle:** Afficher statut public
- **Données:** Même que DetailsDemande (sans édition)
- **Responsive:** Optimisé mobile
- **Access:** Public, pas de session

---

## 🗄️ Schéma Database

```
┌─────────────────────────────────────────────────────────────┐
│ Table: demande                                              │
├─────────────────────────────────────────────────────────────┤
│ id (PK)                    │ BIGINT                         │
│ date_demande               │ DATETIME                       │
│ id_demandeur (FK)          │ BIGINT                         │
│ id_type_visa (FK)          │ BIGINT                         │
│ id_type_demande (FK)       │ BIGINT                         │
│ id_statut_demande (FK)     │ BIGINT                         │
│ reference_visa             │ VARCHAR(50)                    │
│ id_visa_transformable (FK) │ BIGINT                         │
└─────────────────────────────────────────────────────────────┘
                            ↑
                            │ 1:N
                            │
┌─────────────────────────────────────────────────────────────┐
│ Table: historique_statut               ← NOUVEAU MAPPING    │
├─────────────────────────────────────────────────────────────┤
│ id (PK)                    │ BIGINT                         │
│ id_demande (FK)            │ BIGINT                         │
│ id_statut_demande (FK)     │ BIGINT                         │
│ date_changement            │ DATETIME                       │
│ commentaire                │ TEXT                           │
└─────────────────────────────────────────────────────────────┘
                            │
                            └─ SELECT * FROM historique_statut 
                               WHERE id_demande = ?
                               ORDER BY date_changement ASC
```

---

## 🔌 API Response Format

### GET /api/demandes

```json
HTTP 200 OK
Content-Type: application/json

[
  {
    "id": 1,
    "dateDemande": "2026-05-06T10:30:00",
    "nomDemandeur": "Dupont",
    "prenomDemandeur": "Jean",
    "numeroPasSeport": "AB123456",
    "referenceVisa": "VT-2026-001",
    "typeVisa": "Travailleur",
    "typeDemande": "NOUVELLE",
    "statutDemande": "CREE",
    "pieces": [
      {
        "idPiece": 1,
        "nomPiece": "Passeport",
        "obligatoire": true,
        "fourni": false
      }
    ],
    "historiques": [
      {
        "id": 1,
        "statut": "CREE",
        "dateChangement": "2026-05-06T10:30:00",
        "commentaire": "Création de la demande"
      }
    ]
  }
]
```

### GET /api/demandes/{id}

```json
HTTP 200 OK
Content-Type: application/json

{
  "id": 1,
  "dateDemande": "2026-05-06T10:30:00",
  ...
}
```

---

## 🔐 Sécurité par Couche

### Frontend
- HTTPS enforced
- No tokens stored (public page)
- CSRF protection (par défaut Vite)

### Backend
- CORS configuré
- Input validation (Spring Validation)
- SQL injection prevention (JPA)
- No sensitive data in logs

### Database
- Connexion sécurisée (SSL)
- Backups réguliers
- Principes du moindre privilège

---

## ⚡ Performance

### Caching
```
Frontend:
- Cache busting via Vite hash
- LocalStorage pour state (future)

Backend:
- Redis cache (future)
- Database connection pooling
```

### Compression
```
Frontend:
- Gzip/Brotli (Nginx)
- Tree-shaking (Vite)
- Minification CSS/JS

Backend:
- Spring compression (gzip)
```

---

## 🧪 Tests

### Frontend (À implémenter)
- Unit tests: Jest + Vue Test Utils
- E2E tests: Cypress ou Playwright

### Backend (À implémenter)
- Unit tests: JUnit + Mockito
- Integration tests: TestContainers

---

## 📊 Monitoring

### Frontend Metrics
- Page Load Time
- API Response Time
- Error Rates
- QR Code Generation Performance

### Backend Metrics
- API Response Time per endpoint
- Database Query Performance
- Memory/CPU Usage
- Error Logs

---

## 🚀 Scalabilité Future

```
Étape 1: Actuellement
├── Un serveur Frontend (Nginx)
├── Un serveur Backend (Spring Boot)
└── Une BD centralisée

Étape 2: Scale Horizontal
├── Multiple Frontend Servers + Load Balancer
├── Multiple Backend Instances + Load Balancer
└── Database avec Replication

Étape 3: Microservices (Future)
├── Demande Service
├── QR Code Service
├── Notification Service
├── User Service
└── API Gateway

Étape 4: Cloud Native
├── Kubernetes
├── Containerization (Docker)
├── CDN Global
├── Cloud Database (AWS RDS, etc)
```

---

**Sprint 4 - Architecture Documentée et Scalable ✅**
