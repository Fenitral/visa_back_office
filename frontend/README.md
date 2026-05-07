# 📱 Application Vue.js - Suivi des Demandes de Visa avec QR Code

Application Vue.js + Vite pour le Sprint 4 - Génération et suivi des demandes de visa via QR code.

## 🎯 Fonctionnalités

✅ **Liste des demandes** - Affiche toutes les demandes avec leurs statuts
✅ **Détails de la demande** - Consultation complète des informations
✅ **QR Code** - Génération automatique pour chaque demande
✅ **Page publique de suivi** - Accessible via le QR code scanné
✅ **Historique des statuts** - Timeline complète des changements de statut
✅ **Gestion des pièces** - Affiche les documents fournis et manquants

## 🚀 Installation & Démarrage

### Prérequis
- Node.js 16+ et npm

### Étapes

```bash
# 1. Aller dans le dossier frontend
cd frontend

# 2. Installer les dépendances
npm install

# 3. Démarrer le serveur de développement
npm run dev
```

L'application sera accessible à: `http://localhost:5173`

## 📂 Structure du Projet

```
frontend/
├── src/
│   ├── components/          # Composants réutilisables
│   ├── views/               # Pages principales
│   │   ├── ListeDemandes.vue       # Liste de tous les demandes
│   │   ├── DetailsDemande.vue      # Détails + QR code
│   │   └── FollowUp.vue            # Page publique de suivi
│   ├── App.vue              # Composant racine avec navigation
│   └── main.js              # Point d'entrée
├── public/                  # Fichiers statiques
├── index.html               # HTML principal
├── package.json             # Dépendances
├── vite.config.js          # Configuration Vite
└── README.md               # Ce fichier
```

## 🔌 API Backend

L'application consomme les endpoints suivants:

```
GET  /api/demandes           → Liste de toutes les demandes
GET  /api/demandes/{id}      → Détails d'une demande + historique
```

**URL du backend:** `http://localhost:8084` (configurable dans `vite.config.js`)

## 📋 Pages

### 1️⃣ Liste des Demandes (`ListeDemandes.vue`)
- Affiche une grille de toutes les demandes
- Filtre par statut (CREE, SCANNEE, APPROUVEE, REJETEE)
- Clic sur une demande → Page de détails

### 2️⃣ Détails de la Demande (`DetailsDemande.vue`)
- Informations complètes du demandeur
- **QR Code** - Généré automatiquement
- Historique des statuts en timeline
- Liste des pièces justificatives
- Copie de l'URL du QR code

### 3️⃣ Suivi Public (`FollowUp.vue`)
- Page accessible publiquement via URL paramétrée
- Affiche le statut actuel en gros
- Timeline complète de progression
- État de chaque document
- Message contextuel selon le statut

## 🔗 Comment ça marche

### Génération du QR Code

Chaque demande génère une URL unique:
```
http://localhost:5173/?demande={ID}
```

Cette URL est encodée dans le QR code. Quand on scanne:
1. Le téléphone ouvre l'URL
2. L'app détecte le paramètre `?demande=X`
3. La page `FollowUp.vue` se charge automatiquement
4. Le statut et l'historique s'affichent

### Flow Utilisateur

```
Accueil
  ├── Liste des demandes
  │   └── Clic sur demande
  │       └── Détails + QR Code
  │           └── Scan QR Code
  │               └── Page Publique de Suivi
  └── Paramètre URL (?demande=X)
      └── Page Publique directement
```

## 🛠️ Construction pour la production

```bash
npm run build
```

Les fichiers compilés seront dans le dossier `dist/`

## 📦 Dépendances

- **Vue 3** - Framework frontend
- **Vite** - Build tool
- **Axios** - Client HTTP
- **qrcode.vue** - Génération de QR codes

## 🎨 Styling

L'application utilise CSS scoped pour l'isolation des styles:
- Couleur primaire: `#667eea` (bleu)
- Couleur secondaire: `#764ba2` (violet)
- Statuts: Jaune (CREE), Cyan (SCANNEE), Vert (APPROUVEE), Rouge (REJETEE)

## 🐛 Dépannage

### L'API n'est pas accessible
Vérifier que le backend Spring Boot est démarré sur `http://localhost:8084`

### CORS error
S'assurer que l'endpoint `DemandeRestController.java` a l'annotation `@CrossOrigin`

### QR Code ne s'affiche pas
Vérifier que `qrcode.vue` est correctement installé: `npm install qrcode.vue`

## 📝 Notes

- L'application est complètement indépendante du backend
- Elle peut tourner sur un serveur différent (nginx, Apache, etc.)
- Responsive design pour mobile et desktop
- Pas besoin de router (navigation simple basée sur l'état)

## 🚀 Prochaines étapes (Sprint futur)

- [ ] Authentification utilisateur
- [ ] Génération PDF du QR code
- [ ] Partage par email
- [ ] Notifications en temps réel
- [ ] Dashboard admin

---

**Sprint 4 - 2026** | Système de Suivi Demandes Visa
