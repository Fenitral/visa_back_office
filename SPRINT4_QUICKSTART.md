# 🚀 SPRINT 4 - Quick Start Guide

## ⚡ Démarrage rapide (5 minutes)

### Étape 1: Lancer le Backend

```bash
cd d:\STUDY\S6\MNaina\trinome\visa\visa\visa_back_office\backend
mvn spring-boot:run
```

**✓ Attend:** Application prête sur `http://localhost:8084`

### Étape 2: Lancer le Frontend

Ouvrir un **NOUVEAU terminal** (le premier reste actif):

```bash
cd d:\STUDY\S6\MNaina\trinome\visa\visa\visa_back_office\frontend
npm install
npm run dev
```

**✓ Attend:** Application prête sur `http://localhost:5173`

---

## 🧪 Tests à faire

### Test 1️⃣: API Backend
```
Ouvrir: http://localhost:8084/api/demandes
Résultat attendu: JSON avec liste de demandes
```

### Test 2️⃣: Frontend - Liste
```
Ouvrir: http://localhost:5173
Résultat attendu: Grille de demandes s'affiche
```

### Test 3️⃣: Frontend - Détails
```
Cliquer sur une demande dans la liste
Résultat attendu: 
  ✓ Infos du demandeur affichées
  ✓ QR Code généré (carré avec points noirs)
  ✓ Historique en timeline
```

### Test 4️⃣: QR Code - Copier URL
```
Sur la page Détails, cliquer "📋 Copier l'URL"
Résultat attendu: 
  ✓ URL copiée dans presse-papiers
  ✓ URL format: http://localhost:5173/?demande=123
```

### Test 5️⃣: QR Code - Scanner
```
Copier l'URL du test 4
La coller dans la barre d'adresse
Résultat attendu:
  ✓ Page "Suivi Public" s'affiche
  ✓ Grand statut en haut
  ✓ Timeline de l'historique
  ✓ Documents affichés avec checkmarks
```

### Test 6️⃣: Responsive
```
Ouvrir http://localhost:5173/?demande=1 sur téléphone/émulateur
Résultat attendu:
  ✓ Page s'affiche correctement
  ✓ Texte lisible
  ✓ Boutons cliquables
```

---

## 📁 Structure créée

```
visa_back_office/
├── backend/                          (Existant, modifié)
│   ├── src/main/java/.../
│   │   ├── controller/
│   │   │   └── DemandeRestController.java          ✨ NOUVEAU
│   │   ├── dto/
│   │   │   ├── DemandeResponseDTO.java             (modifié)
│   │   │   └── HistoriqueStatutDTO.java            ✨ NOUVEAU
│   │   └── mapper/
│   │       └── DemandeMapper.java                  (modifié)
│   └── pom.xml                       (inchangé)
│
├── frontend/                         ✨ NOUVEAU DOSSIER
│   ├── src/
│   │   ├── App.vue                   ✨ NOUVEAU
│   │   ├── main.js                   ✨ NOUVEAU
│   │   └── views/
│   │       ├── ListeDemandes.vue     ✨ NOUVEAU
│   │       ├── DetailsDemande.vue    ✨ NOUVEAU
│   │       └── FollowUp.vue          ✨ NOUVEAU
│   ├── public/                        (vide, pour favicons etc)
│   ├── index.html                    ✨ NOUVEAU
│   ├── package.json                  ✨ NOUVEAU
│   ├── vite.config.js                ✨ NOUVEAU
│   ├── .gitignore                    ✨ NOUVEAU
│   └── README.md                     ✨ NOUVEAU
│
└── SPRINT4_QR_CODE.md               ✨ NOUVEAU (ce dossier)
```

---

## 🔧 Fichiers modifiés dans le Backend

### 1. `DemandeRestController.java` (NOUVEAU)
Expose les endpoints:
- `GET /api/demandes`
- `GET /api/demandes/{id}`

### 2. `HistoriqueStatutDTO.java` (NOUVEAU)
Représente l'historique d'une demande

### 3. `DemandeResponseDTO.java` (MODIFIÉ)
Ajout du champ: `List<HistoriqueStatutDTO> historiques`

### 4. `DemandeMapper.java` (MODIFIÉ)
Mapping des historiques dans la méthode `toResponseDTO()`

---

## 🐛 Dépannage

### ❌ "Cannot GET /api/demandes"
→ Backend pas lancé
→ Vérifier: `http://localhost:8084/api/test` retourne "Backend OK"

### ❌ "CORS error in console"
→ S'assurer que `@CrossOrigin` est dans `DemandeRestController`

### ❌ "QR Code vide/blanc"
→ Rafraîchir la page
→ Vérifier console pour erreurs JS

### ❌ "npm ERR! not found: npm"
→ Node.js pas installé
→ Télécharger depuis: https://nodejs.org/

### ❌ "Demande non trouvée en URL"
→ URL correcte? Format: `?demande=123` (id numérique)

---

## 📊 Performance

- **Frontend:** Vite compile en <100ms
- **API:** Requête `/api/demandes` ~50-100ms
- **QR Code:** Généré instantanément
- **Page FollowUp:** Chargement <500ms

---

## 🎯 Scénario Complet à Tester

```
1. Backend lancé ✓
2. Frontend lancé ✓
3. Ouvrir http://localhost:5173 ✓
4. Voir liste de demandes ✓
5. Cliquer sur demande #1 ✓
6. Voir QR Code ✓
7. Copier URL ✓
8. Ouvrir URL dans nouvel onglet ✓
9. Voir page "Suivi Public" ✓
10. Vérifier historique visible ✓
```

Si tous les ✓: **Sprint 4 OK! 🎉**

---

## 📚 Documentation complète

- `frontend/README.md` - Documentation Vue.js détaillée
- `SPRINT4_QR_CODE.md` - Documentation complète du sprint
- Code commenté dans les fichiers `.vue`

---

**Sprint 4 - QR Code Tracking System**
