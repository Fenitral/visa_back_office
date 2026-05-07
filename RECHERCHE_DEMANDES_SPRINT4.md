# Sprint 4 - Fonctionnalité de Recherche de Demandes

## 📋 Résumé des modifications

### Backend (Java/Spring)

#### 1. **DemandeService.java** - 2 nouvelles méthodes
- `getDemandeWithRelated(Long id)` : Retourne une demande + toutes les demandes du même demandeur
- `getDemandesByPasseport(String numeroPasSeport)` : Retourne toutes les demandes liées à un passeport

#### 2. **PasseportService.java** - 1 nouvelle méthode
- `findByNumero(String numero)` : Expose la méthode repository publiquement

#### 3. **DemandeRestController.java** - 2 nouveaux endpoints
- `GET /api/demandes/search/id/{id}` : Recherche par ID de demande
  ```json
  Retourne: {
    "principale": { demande details },
    "liees": [ demandes liées ]
  }
  ```
  
- `GET /api/demandes/search/passport/{numeroPasSeport}` : Recherche par numéro de passeport
  ```json
  Retourne: [ demandes ]
  ```

### Frontend (Vue.js)

#### 1. **Recherche.vue** - Nouvelle page de recherche
- Formulaire avec 2 options :
  - Recherche par ID de demande
  - Recherche par numéro de passeport
- Affichage intelligent des résultats selon le type de recherche
- **Logs console détaillés** pour chaque recherche (visible en ouvrant DevTools F12)
- Cartes de demande cliquables pour voir les détails

#### 2. **App.vue** - Modifications
- Ajout du composant Recherche
- Nouveau bouton de navigation "🔍 Rechercher"
- Gestion de la page précédente pour meilleure navigation

---

## 🧪 Comment tester

### 1. **Démarrer le backend**
```bash
cd backend
mvn spring-boot:run
```

### 2. **Démarrer le frontend**
```bash
cd frontend
npm install  # si nécessaire
npm run dev
```

### 3. **Accéder à l'application**
```
http://localhost:5173  (ou selon votre config Vite)
```

### 4. **Tester la recherche**

#### Recherche par ID :
1. Cliquez sur le bouton "🔍 Rechercher" dans la navbar
2. Sélectionnez "Par ID de Demande"
3. Entrez un ID valide (ex: 1, 5, 10)
4. Cliquez sur "Rechercher"
5. **Ouvrez la Console (F12)** pour voir les logs détaillés de la réponse API

**Résultat attendu :**
- La demande principale affichée en haut (avec bordure bleue)
- Les autres demandes du même demandeur en dessous
- Console affichant les données complètes

#### Recherche par Passeport :
1. Cliquez sur le bouton "🔍 Rechercher"
2. Sélectionnez "Par Numéro de Passeport"
3. Entrez un numéro de passeport (ex: PASS-1, PASS-5)
4. Cliquez sur "Rechercher"
5. **Ouvrez la Console (F12)** pour voir les logs détaillés

**Résultat attendu :**
- Toutes les demandes liées à ce passeport affichées
- Console affichant la liste des demandes trouvées

### 5. **Voir les logs API**

Ouvrez la Console du navigateur (F12) et cherchez les lignes :
```
📌 Résultat recherche par ID:
OU
📋 Résultat recherche par Passeport:
```

Cela affichera exactement ce que l'API a retourné.

---

## ✅ Fonctionnalités implémentées

✔️ Formulaire de recherche avec 2 critères (ID ou Passeport)  
✔️ Recherche par ID + affichage demandes liées  
✔️ Recherche par Passeport + affichage toutes demandes  
✔️ Logs console détaillés pour chaque recherche  
✔️ Navigation intuitive (bouton "🔍 Rechercher" dans la navbar)  
✔️ Gestion des erreurs (passeport non trouvé, ID invalide, etc.)  
✔️ Design responsive et cohérent avec l'app  
✔️ API RESTful complète au backend  

---

## 🐛 Débogage

Si une recherche ne fonctionne pas :

1. **Vérifiez la Console** (F12) pour les erreurs d'API
2. **Vérifiez que le backend est en cours d'exécution** : http://localhost:8080/api/demandes
3. **Vérifiez les numéros** : Les passeports commencent généralement par "PASS-"
4. **Vérifiez les IDs** : Les demandes doivent exister dans la base de données

---

## 📊 Architecture globale

```
Frontend (Vue.js)
  ├─ App.vue (navigation)
  ├─ views/
  │  ├─ ListeDemandes.vue (liste toutes demandes)
  │  ├─ DetailsDemande.vue (détails une demande)
  │  ├─ Recherche.vue (🆕 formulaire recherche)
  │  └─ FollowUp.vue (page publique)

Backend (Spring Boot)
  ├─ DemandeRestController
  │  ├─ GET /api/demandes (toutes)
  │  ├─ GET /api/demandes/{id} (détail)
  │  ├─ GET /api/demandes/search/id/{id} (🆕 par ID)
  │  └─ GET /api/demandes/search/passport/{numero} (🆕 par passeport)
  ├─ DemandeService
  │  ├─ getToutesDemandes()
  │  ├─ getDemande(id)
  │  ├─ getDemandeWithRelated(id) (🆕)
  │  └─ getDemandesByPasseport(numero) (🆕)
```

---

## 🎯 Notes importantes

- Les logs console s'affichent à chaque recherche
- La page revient automatiquement à la liste après un retour
- Les cartes de demande sont cliquables pour voir les détails complets
- Le design s'adapte aux appareils mobiles
