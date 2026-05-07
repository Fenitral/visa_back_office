Devs   : ETU003245 / [Nom Backend Dev]
         ETU003368 / [Nom Frontend Dev]

═══════════════════════════════════════════════════════════════
PROJET : GESTION DE VISA TRANSFORMABLE
SPRINT 2 : Gestion des cas sans données antérieures

───────────────────────────────────────────────────────────────
⚙️  BACKEND — Responsable : ETU00XXXX / [Nom Backend Dev]
───────────────────────────────────────────────────────────────

    Module B1 : Base de données & Scripts
    ──────────────────────────────────────
        - Ajouter champ type_declaration     → valeurs : DUPLICATA_CARTE | TRANSFERT_VISA
        - Ajouter champ has_previous_data    → true (données trouvées) | false (saisie manuelle)
        - Ajouter champs ancien_numero et nouveau_numero
              → stocke l'ancien et le nouveau N° selon le type
        - Fournir script DROP + CREATE de toutes les tables concernées
        - Fournir script d'initialisation avec données de test
              → inclure : clients existants ET clients inexistants

    Module B2 : API — Bouton Déclaration & Détection  
    ────────────────────────────────────────────────────────────────────
        - Créer GET /declaration/types
              → retourne : [ DUPLICATA_CARTE, TRANSFERT_VISA ]
              → utilisé par le frontend pour afficher les 2 boutons dynamiquement

        - Créer POST /declaration/verifier
              → reçoit  : { type, numero }
              → retourne : { found: true/false, data: {...} }
              → si found: true  → renvoie les données existantes du client
              → si found: false → indique au frontend de passer en saisie manuelle

    Module B3 : Cas Transfert Visa
    ────────────────────────────────
        *Sous-cas A — Avec données antérieures  

            - Créer GET /transfert-visa/search?passport={numero}
                  → recherche le client par son ancien N° passeport
                  → retourne toutes ses informations si trouvé

            - Implémenter logique d'incrémentation du N° passeport   [RG-03]
                  → extraire la partie numérique en fin de numéro
                  → ajouter un chiffre : CTT123 → CTT1234
                  → gérer les cas particuliers (fin par lettre, etc.)

            - Créer PUT /transfert-visa/update-passport
                  → reçoit  : { dossier_id, ancien_passport, nouveau_passport }
                  → met à jour le numéro en base
                  → enregistre un historique de modification

        *Sous-cas B — Sans données antérieures 

            - Créer POST /transfert-visa/nouveau
                  → reçoit le formulaire complet saisi par l'opérateur
                  → génère automatiquement le titre du dossier
                        Format : TV-{ANNEE}-{SEQUENCE}   ex : TV-2025-00042
                  → persiste le dossier avec has_previous_data = false
                  → champs obligatoires à valider :
                        - Nom, Prénom, Date de naissance
                        - Nationalité
                        - Nouveau N° passeport
                        - Date d'expiration passeport
                        - Type de visa
                  → champs optionnels :
                        - Adresse, Téléphone, Notes

            - Valider les champs côté backend (ne pas faire confiance au frontend seul)
                  → retourner erreurs claires par champ manquant ou invalide
                        Ex : { errors: { nom: "Champ obligatoire", passport: "Format invalide" } }

    Module B4 : Cas Duplicata Carte Résident
    ──────────────────────────────────────────
    (Même logique que Module B3, appliquée au N° de carte résident)

        *Sous-cas A — Avec données antérieures  

            - Créer GET /duplicata/search?carte={numero}
                  → recherche par ancien N° carte résident
                  → retourne les données du client si trouvé

            - Implémenter logique d'incrémentation du N° carte  
                  → même logique que RG-03

            - Créer PUT /duplicata/update-carte
                  → met à jour le N° carte en base
                  → enregistre l'historique

        *Sous-cas B — Sans données antérieures  

            - Créer POST /duplicata/nouveau
                  → génère automatiquement le titre du dossier
                        Format : DC-{ANNEE}-{SEQUENCE}   ex : DC-2025-00015
                  → persiste avec has_previous_data = false
                  → champs obligatoires à valider :
                        - Nom, Prénom, Date de naissance
                        - Nationalité
                        - Nouveau N° carte résident
                        - Date d'expiration carte
                  → champs optionnels :
                        - Adresse, Téléphone, Notes

            - Valider les champs côté backend
                  → même logique que B3 (erreurs par champ)

    Module B5 : Tests Backend
    ──────────────────────────
        - Tester POST /declaration/verifier avec client existant
              → doit retourner found: true + données complètes
        - Tester POST /declaration/verifier avec client inexistant
              → doit retourner found: false
        - Tester incrémentation : CTT123 → CTT1234 → CTT12345
        - Tester POST /transfert-visa/nouveau avec tous les champs → dossier créé
        - Tester POST /duplicata/nouveau avec champ manquant → erreur retournée
        - Vérifier que has_previous_data est bien enregistré selon le sous-cas

───────────────────────────────────────────────────────────────
FRONTEND — Responsable : ETU00XXXX / [Nom Frontend Dev]
───────────────────────────────────────────────────────────────

    Module1 : Bouton Déclaration & Sélection du type   
    ──────────────────────────────────────────────────────────────
        - Ajouter bouton « Déclaration » sur la page principale
        - Au clic → afficher 2 boutons via GET /declaration/types :
              → Duplicata Carte Résident
              → Transfert Visa

    Module2 : Détection et redirection automatique 
    ────────────────────────────────────────────────────────────
        - Après choix du type → afficher champ de recherche :
              → Libellé : « N° passeport »     (si Transfert Visa)
              → Libellé : « N° carte résident » (si Duplicata)

        - Au clic « Rechercher » → appeler POST /declaration/verifier :
              → found: true  → aller vers formulaire avec données (pré-rempli)
              → found: false → aller vers formulaire sans données (saisie manuelle)

        - Afficher message clair selon résultat :
              → Trouvé     :  Client trouvé — données importées automatiquement
              → Non trouvé :  Aucune donnée trouvée — veuillez renseigner manuellement

    Module3 : Formulaire Transfert Visa
    ──────────────────────────────────────
        *Sous-cas— Avec données antérieures 
            - Afficher formulaire pré-rempli avec données importées
            - Afficher en lecture seule :
                  → Ancien N° passeport
                  → Nouveau N° passeport généré automatiquement (mis en évidence)
            - Bouton « Confirmer la mise à jour » → PUT /transfert-visa/update-passport
            - Afficher : Numéro de passeport mis à jour avec succès

        *Sous-cas— Sans données antérieures  
            - Créer formulaire de saisie manuelle complèt
                  (nouveau titre de dossier comme Sprint 1)
                  Champs obligatoires (*) :
                        - Nom *
                        - Prénom *
                        - Date de naissance *
                        - Nationalité *
                        - Nouveau N° passeport *
                        - Date d'expiration passeport *
                        - Type de visa *
                  Champs optionnels :
                        - Adresse, Téléphone, Notes

            - Bloquer soumission si champ obligatoire vide
                  → afficher erreur directement sous le champ concerné

            - Bouton « Soumettre » → POST /transfert-visa/nouveau
            - Afficher le titre du dossier créé :
                  Dossier TV-2025-00042 créé avec succès

    Module4 : Formulaire Duplicata Carte Résident
    ─────────────────────────────────────────────────
    (Même logique que Module3, appliquée au N° de carte résident)

        *Sous-cas A — Avec données antérieures  

            - Formulaire pré-rempli avec données importées
            - Afficher en lecture seule :
                  → Ancien N° carte résident
                  → Nouveau N° carte généré automatiquement
            - Bouton « Confirmer » → PUT /duplicata/update-carte
            - Afficher confirmation de mise à jour

        *Sous-cas B — Sans données antérieures

            - Créer formulaire de saisie manuelle complète
                  Champs obligatoires (*) :
                        - Nom *
                        - Prénom *
                        - Date de naissance *
                        - Nationalité *
                        - Nouveau N° carte résident *
                        - Date d'expiration carte *
                  Champs optionnels :
                        - Adresse, Téléphone, Notes

            - Bloquer soumission si champ obligatoire vide
                  → afficher erreur sous le champ concerné

            - Bouton « Soumettre » → POST /duplicata/nouveau
            - Afficher le titre du dossier créé :
                 Dossier DC-2025-00015 créé avec succès

    Module F: Tests & Intégration
    ────────────────────────────────
        - Tester flux complet AVEC données :
              recherche → pré-remplissage → confirmation → succès affiché
        - Tester flux complet SANS données :
              recherche → message aucune donnée → formulaire manuel → soumission → titre dossier
        - Tester validation : soumettre avec champ vide → erreur sous le bon champ
        - Vérifier affichage du nouveau numéro auto (passeport ET carte)
        - Tester les 2 cas : Transfert Visa ET Duplicata Carte Résident
        - Intégration finale avec toutes les APIs backend