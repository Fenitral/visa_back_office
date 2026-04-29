# SPRINT 3 - Documentation API REST

## Vue d'ensemble

L'API REST du Sprint 3 fournit les endpoints pour gérer le scan des pièces justificatives.

## Endpoints

### 1. Récupérer l'état du scan (HTML)

**GET** `/demandes/{id}/scan`

Affiche la page HTML de scan pour une demande.

**Paramètres:**
- `id` (Path): L'ID de la demande

**Réponse:**
- 200 OK: Template HTML `scan-pieces.html`
- 404 NOT FOUND: Demande non trouvée

**Exemple:**
```bash
curl http://localhost:8082/demandes/1/scan
```

---

### 2. Récupérer l'état du scan (JSON)

**GET** `/demandes/{id}/scan/pieces`

Retourne l'état du scan en JSON pour mise à jour AJAX.

**Paramètres:**
- `id` (Path): L'ID de la demande

**Réponse:** (200 OK)
```json
{
  "idDemande": 1,
  "numeroDemande": "#1",
  "nomDemandeur": "DUPONT Jean",
  "statutActuel": "CREE",
  "pieces": [
    {
      "idPiece": 1,
      "nomPiece": "Passeport",
      "obligatoire": true,
      "scannee": true,
      "nomFichier": "passeport.pdf",
      "dateUpload": "2026-04-29T14:30:00"
    },
    {
      "idPiece": 2,
      "nomPiece": "Visa",
      "obligatoire": true,
      "scannee": false,
      "nomFichier": null,
      "dateUpload": null
    }
  ]
}
```

**Codes d'erreur:**
- 404 NOT FOUND: Demande non trouvée

**Exemple:**
```bash
curl http://localhost:8082/demandes/1/scan/pieces
```

---

### 3. Uploader un fichier

**POST** `/demandes/{id}/scan/upload`

Télécharge un fichier pour une pièce justificative.

**Paramètres:**
- `id` (Path): L'ID de la demande
- `idPiece` (Query): L'ID de la pièce
- `file` (Form Data): Le fichier à uploader

**Validations:**
- Fichier non vide
- Taille < 10 MB
- Extension: .pdf, .jpg, .jpeg, .png

**Réponse:** (200 OK)
```json
{
  "message": "Fichier uploadé avec succès",
  "success": true
}
```

**Réponses d'erreur:**

400 BAD REQUEST - Fichier vide:
```json
{
  "message": "Le fichier ne peut pas être vide",
  "success": false
}
```

400 BAD REQUEST - Taille trop grande:
```json
{
  "message": "La taille du fichier dépasse la limite de 10 MB",
  "success": false
}
```

400 BAD REQUEST - Format non autorisé:
```json
{
  "message": "Format de fichier non autorisé. Formats acceptés: pdf, jpg, jpeg, png",
  "success": false
}
```

404 NOT FOUND - Demande ou pièce manquante:
```json
{
  "message": "Pièce non trouvée pour cette demande",
  "success": false
}
```

**Exemple avec cURL:**
```bash
curl -X POST http://localhost:8082/demandes/1/scan/upload \
  -F "idPiece=1" \
  -F "file=@passeport.pdf"
```

**Exemple avec JavaScript:**
```javascript
const formData = new FormData();
formData.append('idPiece', 1);
formData.append('file', fileInput.files[0]);

fetch('/demandes/1/scan/upload', {
  method: 'POST',
  body: formData
})
.then(response => response.json())
.then(data => console.log(data));
```

---

### 4. Supprimer un fichier

**DELETE** `/demandes/{id}/scan/fichier/{idDemandePiece}`

Supprime un fichier uploadé et réinitialise l'état du scan.

**Paramètres:**
- `id` (Path): L'ID de la demande
- `idDemandePiece` (Path): L'ID du lien demande-pièce

**Réponse:** (200 OK)
```json
{
  "message": "Fichier supprimé avec succès",
  "success": true
}
```

**Codes d'erreur:**
- 404 NOT FOUND: Lien demande-pièce non trouvé

**Exemple avec cURL:**
```bash
curl -X DELETE http://localhost:8082/demandes/1/scan/fichier/5
```

**Exemple avec JavaScript:**
```javascript
fetch('/demandes/1/scan/fichier/5', {
  method: 'DELETE'
})
.then(response => response.json())
.then(data => console.log(data));
```

---

### 5. Valider le scan

**POST** `/demandes/{id}/scan/valider`

Valide que toutes les pièces obligatoires sont scannées et change le statut de la demande à "SCANNEE".

**Paramètres:**
- `id` (Path): L'ID de la demande

**Réponse:** (302 FOUND)
Redirection vers `/demandes/{id}/details`

Headers:
```
Location: /demandes/1/details
X-Flash-Attribute: successMessage=Scan validé avec succès. Statut changé à SCANNEE
```

**Codes d'erreur:**

400 BAD REQUEST - Pièces obligatoires manquantes:
```
Location: /demandes/1/scan
X-Flash-Attribute: errorMessage=Toutes les pièces obligatoires doivent être scannées avant de valider
```

404 NOT FOUND - Demande non trouvée:
```
Location: /demandes
X-Flash-Attribute: errorMessage=Demande non trouvée: 1
```

**Exemple avec cURL:**
```bash
curl -X POST http://localhost:8082/demandes/1/scan/valider
```

---

## Flux de requêtes typique

### 1. Charger la page de scan
```
GET /demandes/1/scan
↓
Retourne: scan-pieces.html
```

### 2. Initialiser les pièces
```
GET /demandes/1/scan/pieces
↓
Retourne: ScanDemandeDTO JSON
```

### 3. Uploader le premier fichier
```
POST /demandes/1/scan/upload
  - idPiece: 1
  - file: passeport.pdf
↓
Retourne: {"success": true}
↓
GET /demandes/1/scan/pieces  (optionnel - pour rafraîchir)
```

### 4. Uploader les fichiers suivants
Répéter l'étape 3 pour chaque pièce

### 5. Valider le scan
```
POST /demandes/1/scan/valider
↓
Redirect: /demandes/1/details
↓
GET /demandes/1/details
```

---

## DTOs

### ScanPieceDTO
```json
{
  "idPiece": 1,
  "nomPiece": "Passeport",
  "obligatoire": true,
  "scannee": true,
  "nomFichier": "passeport.pdf",
  "dateUpload": "2026-04-29T14:30:00"
}
```

### ScanDemandeDTO
```json
{
  "idDemande": 1,
  "numeroDemande": "#1",
  "nomDemandeur": "DUPONT Jean",
  "statutActuel": "CREE",
  "pieces": [
    { /* ScanPieceDTO */ }
  ]
}
```

**Méthode utile:**
```
ScanDemandeDTO.sontToutesPiecesObligatoiresScannees() → boolean
```

---

## Statuts HTTP

| Code | Signification |
|------|---------------|
| 200 | OK - Requête réussie |
| 302 | REDIRECT - Redirection (POST réussi) |
| 400 | BAD REQUEST - Erreur métier (validation) |
| 404 | NOT FOUND - Ressource non trouvée |
| 500 | SERVER ERROR - Erreur serveur |

---

## Formats de fichiers acceptés

| Extension | Type | Taille max |
|-----------|------|-----------|
| .pdf | PDF | 10 MB |
| .jpg | JPEG | 10 MB |
| .jpeg | JPEG | 10 MB |
| .png | PNG | 10 MB |

---

## Configuration du serveur

**Port:** 8082

**Context Path:** `/` (racine)

**Upload Directory:** `uploads/` (répertoire courant)

**Max File Size:** 10 MB

---

## Exemples d'intégration

### JavaScript Vanilla
```javascript
// Récupérer l'état
async function getEtatScan(demandeId) {
  const response = await fetch(`/demandes/${demandeId}/scan/pieces`);
  return await response.json();
}

// Uploader un fichier
async function uploadFichier(demandeId, pieceId, file) {
  const formData = new FormData();
  formData.append('idPiece', pieceId);
  formData.append('file', file);
  
  const response = await fetch(`/demandes/${demandeId}/scan/upload`, {
    method: 'POST',
    body: formData
  });
  return await response.json();
}

// Valider
async function validerScan(demandeId) {
  const response = await fetch(`/demandes/${demandeId}/scan/valider`, {
    method: 'POST'
  });
  if (response.ok) {
    window.location.href = response.url;
  }
}
```

### jQuery
```javascript
// Uploader
$.ajax({
  url: `/demandes/${demandeId}/scan/upload`,
  type: 'POST',
  data: new FormData($('#uploadForm')[0]),
  contentType: false,
  processData: false,
  success: function(data) {
    alert('Upload réussi!');
  }
});
```

### Axios (Node.js/React)
```javascript
const FormData = require('form-data');
const fs = require('fs');

const formData = new FormData();
formData.append('idPiece', 1);
formData.append('file', fs.createReadStream('passeport.pdf'));

const response = await axios.post(
  `/demandes/1/scan/upload`,
  formData,
  { headers: formData.getHeaders() }
);
```

---

## Gestion des erreurs

Tous les endpoints retournent des messages d'erreur clairs en JSON.

**Format d'erreur standard:**
```json
{
  "message": "Description de l'erreur",
  "success": false
}
```

**À implémenter dans le client:**
```javascript
if (!data.success) {
  console.error(data.message);
  // Afficher à l'utilisateur
  showAlert(data.message, 'danger');
}
```

---

## Notes

- Les fichiers sont stockés localement (pas de cloud)
- Les noms de fichiers sont anonymisés (UUID)
- Le chemin original est conservé en base de données
- Pas d'authentification/autorisation spécifiée (à implémenter)
- Les uploads sont limités à 10 MB par défaut
- Tous les uploads sont persistés en base de données
