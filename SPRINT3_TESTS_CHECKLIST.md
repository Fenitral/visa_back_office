# SPRINT 3 - Checklist de Déploiement et Test

## ✅ Implémentation complète

Tous les fichiers ont été créés et modifiés. Vous devez maintenant compiler et tester.

## 📋 Étapes de déploiement

### 1. Compiler le projet
```bash
cd backend
mvn clean package
```

Attendez-vous à voir les migrations Flyway s'exécuter automatiquement:
```
Migration 3: AddScanPiecesColumns - Sucess
```

### 2. Démarrer l'application
```bash
mvn spring-boot:run
```

ou depuis votre IDE (Run GestionVisaApplication.java)

### 3. Vérifier que le serveur démarre
L'application devrait démarrer sur `http://localhost:8082`

## 🧪 Tests manuels

### Test 1: Créer une demande
1. Aller à http://localhost:8082/demandes/nouvelle
2. Remplir le formulaire complètement
3. Cliquer "ENREGISTRER"
4. **Résultat attendu:** Page de confirmation avec le bouton "Scanner les pièces justificatives"

### Test 2: Accéder à la page de scan
1. Sur la page de confirmation, cliquer le bouton "Scanner les pièces justificatives"
2. **Résultat attendu:** 
   - Affichage de la page `scan-pieces.html`
   - Liste des pièces (obligatoires = rouge, optionnelles = bleu)
   - Zones de drag-and-drop visibles
   - Bouton "Valider le scan" en gris (désactivé)

### Test 3: Upload d'un fichier
1. Sur la page de scan, cliquer sur une zone d'upload
2. Sélectionner un fichier PDF/JPG (< 10 MB)
3. **Résultat attendu:**
   - Fichier uploadé avec succès (message vert)
   - Pièce change de couleur (devient verte = scannée)
   - Informations du fichier affichées (nom, date)
   - Barre de progression mise à jour

### Test 4: Drag-and-drop
1. Glisser-déposer un fichier sur une zone d'upload
2. **Résultat attendu:** Même résultat que Test 3

### Test 5: Suppression de fichier
1. Uploader un fichier
2. Cliquer le bouton "Supprimer le fichier"
3. Confirmer la suppression
4. **Résultat attendu:**
   - Fichier supprimé
   - Pièce redevient non scannée
   - Zone d'upload redevient active

### Test 6: Validation du scan - Cas 1 (Échoue)
1. Uploader seulement les pièces optionnelles
2. Cliquer "Valider le scan" (si boutton actif)
3. **Résultat attendu:** Message d'erreur
   "Toutes les pièces obligatoires doivent être scannées avant de valider"

### Test 7: Validation du scan - Cas 2 (Réussit)
1. Uploader toutes les pièces obligatoires
2. Cliquer "Valider le scan"
3. Confirmer dans la modale
4. **Résultat attendu:**
   - Message de succès: "Scan validé avec succès"
   - Redirection vers `/demandes/{id}/details`
   - Dans les détails: Statut de la demande = **SCANNEE** (au lieu de CREE)

### Test 8: Persistance des données
1. Uploader des fichiers
2. Rafraîchir la page (F5)
3. **Résultat attendu:**
   - Les fichiers uploadés restent visibles
   - Aucune perte de données

### Test 9: Erreur de format de fichier
1. Essayer d'uploader un fichier .txt ou .exe
2. **Résultat attendu:** Message d'erreur
   "Format de fichier non autorisé. Formats acceptés: pdf, jpg, jpeg, png"

### Test 10: Erreur de taille de fichier
1. Créer ou utiliser un fichier > 10 MB
2. Essayer de l'uploader
3. **Résultat attendu:** Message d'erreur
   "La taille du fichier dépasse la limite de 10 MB"

### Test 11: Accès depuis la page de détails
1. Aller à la page de détails d'une demande
2. Cliquer le bouton "Scanner les pièces" (section Actions)
3. **Résultat attendu:** Affichage de la page de scan

## 🗂️ Vérification des fichiers

### Base de données
Vérifier que la migration s'est exécutée:

```sql
-- PostgreSQL
SELECT column_name FROM information_schema.columns 
WHERE table_name = 'demande_piece' AND column_name IN ('chemin_fichier', 'nom_fichier', 'date_upload');

-- Résultat attendu: 3 colonnes affichées
```

Vérifier le nouveau statut:
```sql
SELECT * FROM statut_demande WHERE libelle = 'SCANNEE';
-- Résultat attendu: 1 ligne
```

### Répertoire d'uploads
Le répertoire `uploads/` doit être créé automatiquement au premier upload:

```bash
ls -la uploads/
# Résultat: demande_1/, demande_2/, etc.

ls -la uploads/demande_1/
# Résultat: Fichiers avec noms UUID (ex: a1b2c3d4-e5f6-...)
```

### Logs de l'application
Chercher les logs d'upload:
```
INFO  ScanPiecesService - Fichier uploadé avec succès - Demande: 1, Pièce: 1
```

## 🐛 Dépannage

### Problème: Le bouton "Scanner les pièces" n'apparaît pas
**Solution:** 
- Vérifier que confirmation.html et details.html ont été modifiés
- Rafraîchir le cache du navigateur (Ctrl+F5)
- Recompiler l'application

### Problème: Erreur 404 sur `/demandes/{id}/scan`
**Solution:**
- Vérifier que `ScanPiecesController.java` existe
- Vérifier que le contrôleur est bien dans `@RequestMapping("/demandes")`
- Recompiler et redémarrer

### Problème: Upload échoue silencieusement
**Solution:**
- Vérifier les logs de l'application
- Vérifier que le répertoire `uploads/` a les permissions d'écriture
- Vérifier la taille du fichier (< 10 MB)

### Problème: Les fichiers disapparaissent après rafraîchissement
**Solution:**
- Vérifier que la migration SQL s'est exécutée
- Vérifier les colonnes dans PostgreSQL
- Vérifier que DemandePiece est bien mappée en JPA

### Problème: Le statut ne change pas à "SCANNEE"
**Solution:**
- Vérifier que le statut existe en BD: `SELECT * FROM statut_demande WHERE libelle = 'SCANNEE'`
- Si absent, l'ajouter manuellement: `INSERT INTO statut_demande (libelle) VALUES ('SCANNEE')`

## 📊 Cas d'usage avancés

### Cas d'usage 1: Upload fragmenté
1. Jour 1: Scanner 2 pièces obligatoires
2. Fermer l'application
3. Jour 2: Revenir et scanner les 2 dernières pièces
4. Valider le scan
5. **Résultat attendu:** Tout fonctionne - les données sont persistées

### Cas d'usage 2: Modification après scan
1. Valider le scan (statut = SCANNEE)
2. Aller à `/demandes/{id}/scan`
3. Vérifier que la page est accessible
4. Essayer de modifier un fichier
5. **Résultat attendu:** Fonctionnement complet même après validation

### Cas d'usage 3: Multiples demandes
1. Créer demande 1 et scanner les pièces
2. Créer demande 2 et scanner les pièces (avec des fichiers différents)
3. Aller à `/demandes/1/scan` et `/demandes/2/scan`
4. **Résultat attendu:** Chaque demande a ses fichiers isolés

## 📝 Rapport de test

Après avoir complété les tests, vérifiez:

- [ ] Test 1: Créer une demande - ✅ Réussi
- [ ] Test 2: Accéder à la page de scan - ✅ Réussi
- [ ] Test 3: Upload d'un fichier - ✅ Réussi
- [ ] Test 4: Drag-and-drop - ✅ Réussi
- [ ] Test 5: Suppression de fichier - ✅ Réussi
- [ ] Test 6: Validation - Cas échoue - ✅ Réussi
- [ ] Test 7: Validation - Cas réussit - ✅ Réussi
- [ ] Test 8: Persistance des données - ✅ Réussi
- [ ] Test 9: Erreur format - ✅ Réussi
- [ ] Test 10: Erreur taille - ✅ Réussi
- [ ] Test 11: Accès depuis détails - ✅ Réussi
- [ ] Vérification BD - ✅ Réussi
- [ ] Répertoire uploads - ✅ Réussi
- [ ] Logs - ✅ Réussi

## 🎉 Fin du Sprint 3

Une fois tous les tests réussis, le Sprint 3 est complet! 

Les utilisateurs peuvent maintenant:
1. ✅ Créer une demande (statut CREE)
2. ✅ Scanner les pièces justificatives (upload fragmenté possible)
3. ✅ Valider et changer le statut à SCANNEE

Prêt pour le Sprint 4! 🚀
