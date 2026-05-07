📋 DÉTAILS FONCTIONNALITÉS - Sprint 1
FOCUS: Enregistrement Nouvelle Demande

═══════════════════════════════════════════════════════════════
BACK-END (ANGE) - FONCTIONNALITÉS
═══════════════════════════════════════════════════════════════

SEUL ENDPOINT:
   POST /api/demandes-transformation
   
   Entrée: {
      -- ÉTAT CIVIL --
      nom: string,
      prenom: string,
      nom_de_jeune_fille: string,
      date_de_naissance: date,
      lieu_de_naissance: string,
      situation_de_famille: string,
      nationalite: string,
      adresse: string,
      email: string,
      telephone: string,
      
      -- VISA TRANSFORMABLE --
      reference: int,
      date_expiration: date,
      date_arrivee: date,
      lieu_d_arrivee: string,
      
      -- TYPE VISA --
      id_type_visa: int (1=Investisseur, 2=Travailleur)
   }
   
   Sortie: {id_demande, numero_demande, statut}
   
   Logique:
   - Créer personne avec données civiles
   - Créer visa_transformable avec données visa
   - Créer demande_transformation avec type_visa
   - Générer numéro unique (DT-2026-001)
   - Retourner {id_demande, numero_demande, statut}

═══════════════════════════════════════════════════════════════
FRONT-END (TINA) - FORMULAIRE D'ENREGISTREMENT
═══════════════════════════════════════════════════════════════

📋 SECTION 1: ÉTAT CIVIL (REQUIS)
   ☐ Nom *
   ☐ Prénom *
   ☐ Nom de jeune fille
   ☐ Date de naissance *
   ☐ Lieu de naissance *
   ☐ Situation de famille
   ☐ Nationalité *
   ☐ Adresse *
   ☐ Email *
   ☐ Téléphone

🛂 SECTION 2: VISA TRANSFORMABLE (REQUIS)
   ☐ Référence visa *
   ☐ Date d'expiration *
   ☐ Date d'arrivée *
   ☐ Lieu d'arrivée *

🎯 SECTION 3: TYPE DE VISA (REQUIS)
   ○ Investisseur
   ○ Travailleur

🔘 BOUTON: VALIDER
   - Validations: Tous champs requis remplis
   - Appel API: POST /api/demandes-transformation
   - Succès: "Demande DT-2026-001 créée ✓"
   - Erreur: Affiche message erreur
   - Après succès: Reset formulaire