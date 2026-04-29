# SPRINT 3 - Documentation: Scan des Pièces Justificatives

## Vue d'ensemble

Le Sprint 3 introduit la fonctionnalité de scan (upload) de pièces justificatives pour les demandes de visa. Cette fonctionnalité permet aux utilisateurs de:

1. ✅ Uploader un fichier pour chaque pièce justificative
2. ✅ Voir l'état du scan (pièces scannées vs non scannées)  
3. ✅ Fragmenter les uploads (peut être fait en plusieurs fois avec sauvegarde en BD)
4. ✅ Valider l'upload et changer le statut de "CREE" à "SCANNEE"

## Architecture technique

### Modèles et entités

#### DemandePiece (amélioré)
Le modèle `DemandePiece` a été étendu avec 3 nouvelles colonnes:

```java
@Column(name = "chemin_fichier", length = 500)
private String cheminFichier;        // Chemin du fichier uploadé

@Column(name = "nom_fichier", length = 255)
private String nomFichier;           // Nom original du fichier

@Column(name = "date_upload")
private LocalDateTime dateUpload;    // Date et heure d'upload
```

### Base de données

#### Migration V3
File: `src/main/resources/db/migration/V3__AddScanPiecesColumns.sql`

Ajoute les colonnes à la table `demande_piece`:
- `chemin_fichier` VARCHAR(500)
- `nom_fichier` VARCHAR(255)  
- `date_upload` TIMESTAMP

Ajoute aussi le statut "SCANNEE" à la table `statut_demande`.

### DTOs

#### ScanPieceDTO
Représente l'état du scan d'une pièce unique:

```java
private Long idPiece;
private String nomPiece;
private Boolean obligatoire;
private Boolean scannee;            // true si cheminFichier != null
private String nomFichier;
private LocalDateTime dateUpload;
```

#### ScanDemandeDTO
Représente l'état complet du scan d'une demande:

```java
private Long idDemande;
private String numeroDemande;
private String nomDemandeur;
private String statutActuel;
private List<ScanPieceDTO> pieces;
```

Méthode utile:
```java
public boolean sontToutesPiecesObligatoiresScannees()
```

### Services

#### ScanPiecesService
Location: `src/main/java/com/demo/gestionVisa/service/ScanPiecesService.java`

**Méthodes principales:**

1. **`getEtatScan(Long idDemande)`**
   - Récupère l'état complet du scan d'une demande
   - Retourne un `ScanDemandeDTO`

2. **`uploadFichier(Long idDemande, Long idPiece, MultipartFile file)`**
   - Upload un fichier pour une pièce
   - Validations:
     - Fichier non vide
     - Taille < 10 MB
     - Extension autorisée (PDF, JPG, JPEG, PNG)
   - Crée le répertoire s'il n'existe pas
   - Génère un UUID pour le nom du fichier
   - Met à jour `DemandePiece` avec chemin, nom et date

3. **`validerScan(Long idDemande)`**
   - Vérifie que toutes les pièces obligatoires sont scannées
   - Change le statut de la demande à "SCANNEE"
   - Lève une `BusinessException` si validation échoue

4. **`supprimerFichier(Long idDemandePiece)`**
   - Supprime un fichier uploadé
   - Réinitialise les colonnes de scan

### Contrôleur

#### ScanPiecesController
Location: `src/main/java/com/demo/gestionVisa/controller/ScanPiecesController.java`

**Routes disponibles:**

| Méthode | Route | Description |
|---------|-------|-------------|
| GET | `/demandes/{id}/scan` | Affiche la page de scan |
| GET | `/demandes/{id}/scan/pieces` | Retourne l'état du scan en JSON (AJAX) |
| POST | `/demandes/{id}/scan/upload` | Upload un fichier |
| DELETE | `/demandes/{id}/scan/fichier/{idDemandePiece}` | Supprime un fichier |
| POST | `/demandes/{id}/scan/valider` | Valide le scan et change le statut |

### Interface utilisateur

#### Template: scan-pieces.html
Location: `src/main/resources/templates/demande/scan-pieces.html`

**Fonctionnalités:**

- 📊 Barre de progression montrant le nombre de pièces scannées
- 📄 Liste des pièces avec statut (scannée/non scannée)
- 🎯 Distinction visuelles pour pièces obligatoires vs optionnelles
- 📤 Zone de drag-and-drop pour chaque pièce
- 🔄 Upload avec aperçu de la barre de progression
- ✅ Résumé latéral avec état des obligatoires et optionnelles
- 🔘 Bouton "Valider" activé seulement si toutes les obligatoires sont scannées
- 🗑️ Bouton de suppression de fichier avec confirmation

**Styles:**
- Utilise Bootstrap 5
- Bootstrap Icons pour les icônes
- Code couleur: rouge (obligatoire), bleu (optionnel), vert (scannée)

## Flux utilisateur

### Étape 1: Créer une demande
1. Utilisateur crée une demande via `/demandes/nouvelle`
2. Statut initial: **CREE**

### Étape 2: Accéder à la page de scan
Depuis la page de confirmation ou de détails de la demande:
```
Bouton "Scanner les pièces justificatives"
  ↓
GET /demandes/{id}/scan
```

### Étape 3: Scanner les pièces
1. Pour chaque pièce obligatoire:
   - Cliquer/drag-drop un fichier dans la zone
   - POST `/demandes/{id}/scan/upload`
   - Fichier sauvegardé localement
   - `DemandePiece` mise à jour en BD

2. Peut quitter et revenir plus tard - les données sont persistées

### Étape 4: Valider le scan
1. Une fois toutes les pièces obligatoires uploadées
2. Cliquer "Valider le scan"
3. Confirmation modale
4. POST `/demandes/{id}/scan/valider`
5. Statut change à **SCANNEE**
6. Redirection vers `/demandes/{id}/details`

## Configuration

### Properties
Dans `application.properties`:

```properties
# Répertoire de stockage des uploads (relatif au projet)
upload.directory=uploads

# Limite de taille pour upload
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

### Structure des fichiers
```
project-root/
├── uploads/
│   ├── demande_1/
│   │   ├── {uuid}.pdf
│   │   └── {uuid}.jpg
│   ├── demande_2/
│   │   └── {uuid}.png
│   └── ...
```

## Validations

### Upload de fichier
- ✓ Fichier non vide
- ✓ Taille < 10 MB
- ✓ Extension: .pdf, .jpg, .jpeg, .png (case-insensitive)

### Validation du scan
- ✓ Toutes les pièces obligatoires doivent être scannées
- ✓ Les pièces optionnelles ne sont pas requises

## Gestion des erreurs

| Situation | Code HTTP | Message |
|-----------|-----------|---------|
| Fichier vide | 400 | "Le fichier ne peut pas être vide" |
| Fichier trop gros | 400 | "La taille du fichier dépasse la limite de 10 MB" |
| Format non autorisé | 400 | "Format de fichier non autorisé..." |
| Demande introuvable | 404 | "Demande non trouvée" |
| Pièces obligatoires manquantes | 400 | "Toutes les pièces obligatoires doivent être scannées..." |

## Tests

### Test manuel

1. **Créer une demande:**
   ```bash
   GET /demandes/nouvelle
   # Remplir le formulaire
   POST /demandes/nouvelle
   ```

2. **Accéder à la page de scan:**
   ```bash
   GET /demandes/1/scan
   ```

3. **Uploader un fichier:**
   - Cliquer sur une zone d'upload
   - Sélectionner un fichier PDF/JPG/PNG
   - Vérifier que le fichier s'affiche

4. **Vérifier la persistance:**
   - Rafraîchir la page
   - Les fichiers doivent rester visibles

5. **Tester la validation:**
   - Scanner seulement les optionnelles
   - Le bouton "Valider" doit rester désactivé
   - Après avoir scanné toutes les obligatoires
   - Le bouton doit devenir actif

6. **Valider et vérifier le changement de statut:**
   - Cliquer "Valider le scan"
   - Confirmer
   - Vérifier que le statut passe à "SCANNEE"

### Test de suppression

1. Uploader un fichier
2. Cliquer le bouton "Supprimer le fichier"
3. Confirmer la suppression
4. Vérifier que le fichier est supprimé
5. Vérifier que la zone d'upload redevient active

### Test AJAX

Utiliser les appels JSON pour:
```bash
# Récupérer l'état
GET /demandes/1/scan/pieces
# Retourne un JSON ScanDemandeDTO

# Upload avec FormData
POST /demandes/1/scan/upload
# Form data: idPiece=1, file=...

# Suppression
DELETE /demandes/1/scan/fichier/5
```

## Prochaines étapes (futurs sprints)

- [ ] Télécharger les fichiers scannés
- [ ] Prévisualisation des fichiers
- [ ] Signature électronique des pièces
- [ ] Envoi par email des pièces
- [ ] Archive ZIP de tous les documents
- [ ] OCR/reconnaissance d'images
- [ ] Historique des modifications des pièces

## Notes de développement

- Les fichiers sont stockés localement (pas de cloud)
- Les noms de fichiers sont anonymisés (UUID)
- Le chemin original est conservé dans la BD
- Utilise MultipartFile de Spring
- Drag-and-drop supporté
- Mise à jour dynamique sans rechargement
- Interface responsive (mobile-friendly)

## Dépendances

Aucune nouvelle dépendance Maven requise - utilise les dépendances existantes:
- Spring Web (MultipartFile)
- Spring Data JPA (repositories)
- Lombok (annotations)
- Thymeleaf (templates)
