# SPRINT 3: Scan des Pièces Justificatives 📄

## ✨ Quoi de neuf?

Le Sprint 3 ajoute une **fonctionnalité complète de scan (upload) des pièces justificatives** pour les demandes de visa.

### 🎯 Fonctionnalités

✅ **Upload de fichiers** - Un fichier par pièce justificative  
✅ **Affichage du scan** - Visualiser quelles pièces sont scannées  
✅ **Persistance** - Les uploads sont sauvegardés en base de données  
✅ **Upload fragmenté** - Peut être fait en plusieurs fois (pause/reprendre)  
✅ **Validation** - Vérification que tous les obligatoires sont scannés  
✅ **Changement statut** - La demande passe de "CREE" à "SCANNEE"  

---

## 🚀 Comment utiliser?

### 1. Créer une demande
```
URL: http://localhost:8084/demandes/nouvelle
Remplir le formulaire complètement
Cliquer "ENREGISTRER"
```

### 2. Accéder à la page de scan
```
Depuis la confirmation: Cliquer "Scanner les pièces justificatives"
OU
Depuis les détails: Dans la section Actions, cliquer "Scanner les pièces"
```

### 3. Uploader les pièces
```
- Cliquer ou glisser-déposer un fichier dans chaque zone
- Formats acceptés: PDF, JPG, PNG, JPEG (max 10 MB)
- Les fichiers sont automatiquement sauvegardés
```

### 4. Valider
```
Une fois tous les obligatoires uploadés:
- Cliquer le bouton vert "Valider le scan"
- Confirmer dans la modale
- Le statut de la demande devient "SCANNEE"
```

---

## 📊 Exemple visuel

```
Demande créée (CREE)
      ↓
   Scanner les pièces
      ↓
┌─────────────────────────────────┐
│ Scan des pièces justificatives  │
│                                 │
│ ☐ Passeport (obligatoire)      │ ← Click/Drag-drop
│ ☐ Visa (obligatoire)           │ ← Click/Drag-drop
│ ☐ Certificat (optionnel)       │ ← Click/Drag-drop
│                                 │
│ 2/3 pièces scannées ▓▓░        │
│                                 │
│ [Valider le scan] ✓ (activé)   │
└─────────────────────────────────┘
      ↓
Cliquer "Valider"
      ↓
Demande scannée (SCANNEE)
```

---

## 📁 Fichiers principaux

### Code
- **ScanPiecesController.java** - Routes REST (5 endpoints)
- **ScanPiecesService.java** - Logique des uploads
- **scan-pieces.html** - Interface utilisateur

### Documentation
- **[SPRINT3_INDEX.md](SPRINT3_INDEX.md)** - Index complet
- **[SPRINT3_RESUME_MODIFICATIONS.md](SPRINT3_RESUME_MODIFICATIONS.md)** - Modifications techniques
- **[SPRINT3_SCAN_DOCUMENTATION.md](SPRINT3_SCAN_DOCUMENTATION.md)** - Documentation détaillée
- **[SPRINT3_TESTS_CHECKLIST.md](SPRINT3_TESTS_CHECKLIST.md)** - Guide de test
- **[SPRINT3_API_REST.md](SPRINT3_API_REST.md)** - Documentation API

---

## 🔧 Installation / Déploiement

### Compiler
```bash
cd backend
mvn clean package
```

### Démarrer
```bash
mvn spring-boot:run
```

### Accéder
```bash
http://localhost:8084
```

### Vérifier
```bash
# Base de données
psql visa_db
SELECT column_name FROM information_schema.columns 
WHERE table_name = 'demande_piece';

# Fichiers uploadés
ls uploads/demande_1/
```

---

## 🧪 Tests rapides

### Test 1: Création et scan
```bash
1. Créer une demande via /demandes/nouvelle
2. Cliquer "Scanner les pièces"
3. Uploader un fichier
4. Valider
5. Vérifier statut = SCANNEE
```

### Test 2: Upload fragmenté
```bash
1. Uploader pièce 1
2. Rafraîchir la page
3. Uploader pièce 2
4. Valider → Tout doit fonctionner
```

### Test 3: Erreurs
```bash
1. Essayer d'uploader fichier > 10 MB → Erreur
2. Essayer fichier .txt → Erreur
3. Valider sans tout uploader → Erreur
```

Pour plus de tests, voir: **[SPRINT3_TESTS_CHECKLIST.md](SPRINT3_TESTS_CHECKLIST.md)**

---

## 📱 Interface

### Page de scan

```
┌─────────────────────────────────────────────────────┐
│ 📄 Scan des Pièces Justificatives                   │
│ Demande #1 - DUPONT Jean                           │
│ ✓ Statut: CREE                                      │
│ Progress: 2/4 pièces ▓▓░░                          │
├─────────────────────────────────────────────────────┤
│                                                      │
│ Pièce 1: Passeport (OBLIGATOIRE) ✓ ✓              │
│  📤 Zone d'upload [✓ passeport.pdf uploadé]        │
│  [Supprimer]                                        │
│                                                      │
│ Pièce 2: Visa (OBLIGATOIRE) ✗                     │
│  📤 Zone d'upload [Drag-drop ici]                  │
│                                                      │
│ Pièce 3: Autres docs (OPTIONNEL) ✗                │
│  📤 Zone d'upload [Drag-drop ici]                  │
│                                                      │
├─────────────────────────────────────────────────────┤
│ RÉSUMÉ                                              │
│ Obligatoires: 1/2 ✓                                │
│ Optionnelles: 0/1                                   │
│ [Valider] ✗ (désactivé - attend pièces)          │
│ [Retour]                                            │
└─────────────────────────────────────────────────────┘
```

---

## 🔌 API REST

### Endpoints

| Méthode | Route | Description |
|---------|-------|-------------|
| GET | `/demandes/{id}/scan` | Page HTML de scan |
| GET | `/demandes/{id}/scan/pieces` | État en JSON |
| POST | `/demandes/{id}/scan/upload` | Upload fichier |
| DELETE | `/demandes/{id}/scan/fichier/{id}` | Supprimer fichier |
| POST | `/demandes/{id}/scan/valider` | Valider et changer statut |

### Exemple d'upload
```bash
curl -X POST http://localhost:8084/demandes/1/scan/upload \
  -F "idPiece=1" \
  -F "file=@passeport.pdf"

# Réponse:
# {"message": "Fichier uploadé avec succès", "success": true}
```

Pour plus d'exemples, voir: **[SPRINT3_API_REST.md](SPRINT3_API_REST.md)**

---

## ⚙️ Configuration

### application.properties

```properties
# Répertoire d'upload
upload.directory=uploads

# Limite de taille
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

### Structure des fichiers
```
project-root/
└── uploads/
    ├── demande_1/
    │   ├── uuid1.pdf
    │   └── uuid2.jpg
    └── demande_2/
        └── uuid3.png
```

---

## 🗄️ Base de données

### Colonnes ajoutées à `demande_piece`
```sql
ALTER TABLE demande_piece
ADD COLUMN chemin_fichier VARCHAR(500),
ADD COLUMN nom_fichier VARCHAR(255),
ADD COLUMN date_upload TIMESTAMP;
```

### Nouveau statut
```sql
INSERT INTO statut_demande (libelle)
VALUES ('SCANNEE');
```

---

## 🐛 Dépannage

### Q: Le bouton "Scanner les pièces" n'apparaît pas?
**A:** Rafraîchir le navigateur (Ctrl+F5) et recompiler l'application

### Q: Erreur 404 sur /scan?
**A:** Vérifier que ScanPiecesController.java existe et est compilé

### Q: Les fichiers disparaissent après refresh?
**A:** Vérifier que la migration V3 s'est exécutée en BD

### Q: Upload échoue?
**A:** Vérifier les logs; vérifier taille < 10 MB; vérifier format (pdf/jpg/png)

Pour plus d'aide, voir: **[SPRINT3_TESTS_CHECKLIST.md](SPRINT3_TESTS_CHECKLIST.md)**

---

## 📚 Documentation complète

Pour la documentation complète:
- **[SPRINT3_INDEX.md](SPRINT3_INDEX.md)** - Navigation complète
- **[SPRINT3_SCAN_DOCUMENTATION.md](SPRINT3_SCAN_DOCUMENTATION.md)** - Détails techniques
- **[SPRINT3_API_REST.md](SPRINT3_API_REST.md)** - Endpoints et exemples

---

## 🎓 Points clés

### Statuts
- **CREE** - Demande créée, pièces non scannées
- **SCANNEE** - Toutes les pièces obligatoires ont été scannées

### Formats acceptés
- PDF (.pdf)
- JPEG (.jpg, .jpeg)
- PNG (.png)
- Taille max: 10 MB

### Sécurité
- Noms de fichiers anonymisés (UUID)
- Isolation par demande
- Validation format et taille

---

## ✅ Résumé

| Fonctionnalité | Statut |
|---|---|
| Upload de fichiers | ✅ Complètement implémenté |
| Affichage du scan | ✅ Complètement implémenté |
| Persistance en BD | ✅ Complètement implémenté |
| Upload fragmenté | ✅ Complètement implémenté |
| Validation | ✅ Complètement implémenté |
| Changement statut | ✅ Complètement implémenté |
| Interface UI | ✅ Moderne et responsive |
| Documentation | ✅ Exhaustive |
| Tests | ✅ 11 scénarios couverts |

---

## 🎉 Conclusion

Le **Sprint 3 est complet et prêt pour la production**! 🚀

Les utilisateurs peuvent maintenant:
1. ✅ Créer une demande
2. ✅ Scanner les pièces justificatives
3. ✅ Fragmenter les uploads
4. ✅ Valider et changer le statut

**Happy scanning! 📄✨**

---

**Dernière mise à jour:** 29/04/2026  
**Version:** 1.0 Sprint 3  
**Statut:** ✅ Complet et testé
