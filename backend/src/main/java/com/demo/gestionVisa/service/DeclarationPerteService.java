package com.demo.gestionVisa.service;

import com.demo.gestionVisa.dto.DeclarationPerteDTO;
import com.demo.gestionVisa.dto.DemandeResponseDTO;
import com.demo.gestionVisa.exception.BusinessException;
import com.demo.gestionVisa.exception.ResourceNotFoundException;
import com.demo.gestionVisa.mapper.DemandeMapper;
import com.demo.gestionVisa.model.*;
import com.demo.gestionVisa.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class DeclarationPerteService {

    private final DemandeurRepository demandeurRepository;
    private final DemandeRepository demandeRepository;
    private final PasseportRepository passeportRepository;
    private final CarteResidentRepository carteResidentRepository;
    private final VisaTransformableRepository visaTransformableRepository;
    private final VisaRepository visaRepository;
    private final HistoriqueStatutRepository historiqueStatutRepository;
    private final TypeDemandeRepository typeDemandeRepository;
    private final StatutDemandeRepository statutDemandeRepository;
    private final TypeVisaRepository typeVisaRepository;
    private final PasseportService passeportService;
    private final CarteResidentService carteResidentService;
    private final VisaService visaService;
    private final DemandeMapper demandeMapper;

    public DeclarationPerteService(
            DemandeurRepository demandeurRepository,
            DemandeRepository demandeRepository,
            PasseportRepository passeportRepository,
            CarteResidentRepository carteResidentRepository,
            VisaTransformableRepository visaTransformableRepository,
            VisaRepository visaRepository,
            HistoriqueStatutRepository historiqueStatutRepository,
            TypeDemandeRepository typeDemandeRepository,
            StatutDemandeRepository statutDemandeRepository,
            TypeVisaRepository typeVisaRepository,
            PasseportService passeportService,
            CarteResidentService carteResidentService,
            VisaService visaService,
            DemandeMapper demandeMapper) {
        this.demandeurRepository = demandeurRepository;
        this.demandeRepository = demandeRepository;
        this.passeportRepository = passeportRepository;
        this.carteResidentRepository = carteResidentRepository;
        this.visaTransformableRepository = visaTransformableRepository;
        this.visaRepository = visaRepository;
        this.historiqueStatutRepository = historiqueStatutRepository;
        this.typeDemandeRepository = typeDemandeRepository;
        this.statutDemandeRepository = statutDemandeRepository;
        this.typeVisaRepository = typeVisaRepository;
        this.passeportService = passeportService;
        this.carteResidentService = carteResidentService;
        this.visaService = visaService;
        this.demandeMapper = demandeMapper;
    }

    /**
     * Déclare une perte de passeport et/ou carte résident pour un demandeur.
     * 
     * Règles:
        * 1. Créer une NOUVELLE demande (DUPLICATA ou TRANSFERT_VISA selon le cas)
     * 2. Récupérer le VisaTransformable le plus récent de la dernière demande
     * 3. Créer les nouveaux passeport et/ou carte résident
     * 4. Ne JAMAIS modifier les anciennes données
     * 5. Conserver l'historique complet
     *
     * @param dto   données de la déclaration de perte
     * @return      DemandeResponseDTO de la nouvelle demande créée
     * @throws ResourceNotFoundException si le demandeur n'existe pas
     * @throws BusinessException si les données sont incohérentes
     */
    public DemandeResponseDTO declarerPerte(DeclarationPerteDTO dto) {
        // ÉTAPE 1 — Valider les paramètres
        if (dto.getPertePasseport() == null || dto.getPerteCarteResident() == null) {
            throw new BusinessException("Vous devez déclarer au moins une perte (passeport et/ou carte résident).");
        }
        
        if (!dto.getPertePasseport() && !dto.getPerteCarteResident()) {
            throw new BusinessException("Vous devez cocher au moins une case de perte.");
        }

        // ÉTAPE 2 — Récupérer le demandeur
        Demandeur demandeur = demandeurRepository.findById(dto.getIdDemandeur())
                .orElseThrow(() -> new ResourceNotFoundException("Demandeur introuvable : id=" + dto.getIdDemandeur()));

        // ÉTAPE 3 — Récupérer la dernière demande pour ce demandeur (pour copier TypeVisa et VisaTransformable)
        // Si aucune demande antérieure, on créera une demande DUPLICATA de zéro
        Demande demandePrecedente = demandeRepository.findByDemandeurOrderByDateDemandeDesc(demandeur)
                .stream()
                .findFirst()
                .orElse(null);

        TypeVisa typeVisa = null;
        VisaTransformable visaTransformable = null;
        Passeport ancienPasseport = null;

        // Si une demande antérieure existe, récupérer ses données
        if (demandePrecedente != null) {
            typeVisa = demandePrecedente.getTypeVisa();
            visaTransformable = demandePrecedente.getVisaTransformable();

            if (typeVisa == null || visaTransformable == null) {
                throw new BusinessException("Les données de la demande précédente sont incomplètes.");
            }

            ancienPasseport = passeportRepository.findById(
                    visaTransformable.getPasseport().getId()
            ).orElseThrow(() -> new ResourceNotFoundException("Passeport introuvable"));
        } else {
            // Aucune demande antérieure : créer un TypeVisa par défaut (première visa transformable)
            typeVisa = typeVisaRepository.findAll()
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Aucun TypeVisa disponible en base"));
        }

        // ÉTAPE 4 — Créer les nouvelles entités (SANS modifier les anciennes)
        Passeport nouveauPasseport = null;
        if (ancienPasseport != null && Boolean.TRUE.equals(dto.getPertePasseport())) {
            // Cas normal : copier l'ancien passeport si perdu
            nouveauPasseport = creerNouveauPasseport(ancienPasseport);
        } else if (ancienPasseport != null) {
            // Ancien passeport existe mais pas perdu : réutiliser
            nouveauPasseport = ancienPasseport;
        } else {
            // Aucun ancien passeport : créer un nouveau passeport initial
            nouveauPasseport = creerPasseportInitial(demandeur);
        }

        // ÉTAPE 5 — Créer le nouveau VisaTransformable SEULEMENT si passeport perdu
        VisaTransformable nouvelleVisaTransformable;
        if (visaTransformable != null && Boolean.TRUE.equals(dto.getPertePasseport())) {
            // Perte de passeport (TRANSFERT_VISA): le numéro de passeport change, mais la référence visa doit rester la même
            nouvelleVisaTransformable = creerNouvelleVisaTransformable(visaTransformable, nouveauPasseport, true);
        } else if (visaTransformable != null) {
            // Visa transformable existe mais passeport pas perdu : réutiliser
            nouvelleVisaTransformable = visaTransformable;
        } else {
            // Aucun visa transformable antérieur : créer un nouveau initial
            nouvelleVisaTransformable = creerVisaTransformableInitial(nouveauPasseport);
        }

        // ÉTAPE 6 — TypeDemande selon le type de perte
        // - Perte carte résident (sans perte passeport) => DUPLICATA (sans changer passeport/référence)
        // - Perte passeport => TRANSFERT_VISA (passeport change, référence visa reste la même)
        final String typeDemandeLibelle = Boolean.TRUE.equals(dto.getPertePasseport()) ? "TRANSFERT_VISA" : "DUPLICATA";
        TypeDemande typeDemande = typeDemandeRepository.findByLibelle(typeDemandeLibelle)
                .orElseThrow(() -> new ResourceNotFoundException("TypeDemande " + typeDemandeLibelle + " introuvable en base"));

        // ÉTAPE 8 — StatutDemande = CREE
        StatutDemande statutCree = statutDemandeRepository.findByLibelle("CREE")
                .orElseThrow(() -> new ResourceNotFoundException("StatutDemande CREE introuvable en base"));

        // ÉTAPE 9 — Construire et sauvegarder la nouvelle Demande (copie de l'ancienne, mais TypeDemande = DUPLICATA)
        Demande nouvelleDemande = new Demande();
        nouvelleDemande.setDateDemande(LocalDateTime.now());
        nouvelleDemande.setDemandeur(demandeur);
        nouvelleDemande.setVisaTransformable(nouvelleVisaTransformable);
        nouvelleDemande.setTypeVisa(typeVisa);
        nouvelleDemande.setTypeDemande(typeDemande);
        nouvelleDemande.setStatutDemande(statutCree);
        nouvelleDemande = demandeRepository.save(nouvelleDemande);

        // ÉTAPE 9 — Créer la carte résident si déclarée comme perdue
        if (Boolean.TRUE.equals(dto.getPerteCarteResident())) {
            carteResidentService.creer(nouveauPasseport, nouvelleDemande);
        } else if (demandePrecedente != null) {
            // Copier l'ancienne carte résident vers la nouvelle demande (si passeport pas perdu)
            copierAncienneCarteResident(demandePrecedente, nouveauPasseport, nouvelleDemande);
        }
        // Sinon : aucune ancien CR et pas de perte déclarée, aucun CR créé

        // ÉTAPE 10 — Visa
        // Exigences métier (avec antécédents):
        // - DUPLICATA (perte carte résident): ne pas changer la référence visa
        // - TRANSFERT_VISA (perte passeport): passeport change mais la référence visa reste la même
        if (demandePrecedente != null) {
            if (Boolean.TRUE.equals(dto.getPertePasseport())) {
                transfererVisaEnConservantReference(demandePrecedente, nouvelleDemande, nouveauPasseport);
            } else {
                copierAncienVisaEnConservantReference(demandePrecedente, nouvelleDemande);
            }
        } else {
            // Aucune demande antérieure : créer un nouveau visa initial
            visaService.creer(nouveauPasseport, nouvelleDemande);
        }

        // ÉTAPE 12 — Créer l'historique de statut
        creerHistoriqueStatut(nouvelleDemande, statutCree, 
            "Déclaration de perte - " + (dto.getMotif() != null ? dto.getMotif() : "Non spécifié"));

        // ÉTAPE 13 — Recharger et retourner
        Demande demandeFinal = demandeRepository.findById(nouvelleDemande.getId()).orElseThrow();
        return demandeMapper.toResponseDTO(demandeFinal);
    }

    /**
     * Crée un nouveau passeport avec dates par défaut (quand aucune demande antérieure n'existe).
     *
     * @param demandeur     le demandeur propriétaire du passeport
     * @return              le nouveau passeport créé
     */
    private Passeport creerPasseportInitial(Demandeur demandeur) {
        Passeport nouveau = new Passeport();
        nouveau.setDateDelivrance(LocalDate.now());
        nouveau.setDateExpiration(LocalDate.now().plusYears(5)); // Expiration par défaut
        nouveau.setDemandeur(demandeur);
        
        // Sauvegarder d'abord pour obtenir l'ID
        nouveau = passeportRepository.save(nouveau);
        
        // Générer le numéro avec le nouvel ID
        nouveau.setNumero("PASS-" + nouveau.getId());
        return passeportRepository.save(nouveau);
    }

    /**
     * Crée un nouveau VisaTransformable avec dates par défaut (quand aucune demande antérieure n'existe).
     *
     * @param nouveauPasseport  le nouveau passeport à lier
     * @return                  le nouveau visa transformable créé
     */
    private VisaTransformable creerVisaTransformableInitial(Passeport nouveauPasseport) {
        VisaTransformable nouveau = new VisaTransformable();
        nouveau.setDateEntree(LocalDate.now());
        nouveau.setLieuEntree("Non spécifié");
        nouveau.setDateExpiration(nouveauPasseport.getDateExpiration() != null ? 
            nouveauPasseport.getDateExpiration() : LocalDate.now().plusYears(5));
        nouveau.setPasseport(nouveauPasseport);
        
        // Sauvegarder d'abord pour obtenir l'ID
        nouveau = visaTransformableRepository.save(nouveau);
        
        // Générer la référence avec le nouvel ID
        nouveau.setReferenceVisa("VISTRANS-" + nouveau.getId());
        return visaTransformableRepository.save(nouveau);
    }

    /**
     * Crée un nouveau passeport basé sur les données de l'ancien (sans copier le numéro).
     *
     * @param ancienPasseport   le passeport à copier
     * @return                  le nouveau passeport créé
     */
    private Passeport creerNouveauPasseport(Passeport ancienPasseport) {
        Passeport nouveau = new Passeport();
        nouveau.setDateDelivrance(LocalDate.now());
        nouveau.setDateExpiration(ancienPasseport.getDateExpiration()); // Garder l'expiration de l'ancien
        nouveau.setDemandeur(ancienPasseport.getDemandeur());
        
        // Sauvegarder d'abord pour obtenir l'ID
        nouveau = passeportRepository.save(nouveau);
        
        // Générer le numéro avec le nouvel ID
        nouveau.setNumero("PASS-" + nouveau.getId());
        return passeportRepository.save(nouveau);
    }

    /**
     * Crée une nouvelle VisaTransformable basée sur l'ancienne avec le nouveau passeport.
     *
     * @param ancienVisa        le visa transformable à copier
     * @param nouveauPasseport  le nouveau passeport à lier
     * @return                  le nouveau visa transformable créé
     */
    private VisaTransformable creerNouvelleVisaTransformable(VisaTransformable ancienVisa, Passeport nouveauPasseport, boolean conserverReferenceVisa) {
        VisaTransformable nouveau = new VisaTransformable();
        
        // Copier les dates de l'ancien visa
        nouveau.setDateEntree(ancienVisa.getDateEntree());
        nouveau.setLieuEntree(ancienVisa.getLieuEntree());
        nouveau.setDateExpiration(ancienVisa.getDateExpiration());
        
        // Si les dates ne sont pas présentes, utiliser des dates par défaut basées sur le passeport
        if (nouveau.getDateEntree() == null) {
            nouveau.setDateEntree(LocalDate.now());
        }
        if (nouveau.getDateExpiration() == null) {
            nouveau.setDateExpiration(nouveauPasseport.getDateExpiration() != null ? 
                nouveauPasseport.getDateExpiration() : LocalDate.now().plusYears(5));
        }
        
        nouveau.setPasseport(nouveauPasseport);

        if (conserverReferenceVisa) {
            nouveau.setReferenceVisa(ancienVisa.getReferenceVisa());
            return visaTransformableRepository.save(nouveau);
        }

        // Sauvegarder d'abord pour obtenir l'ID
        nouveau = visaTransformableRepository.save(nouveau);

        // Générer la référence avec le nouvel ID
        nouveau.setReferenceVisa("VISTRANS-" + nouveau.getId());
        return visaTransformableRepository.save(nouveau);
    }

    /**
     * Copie l'ancienne carte résident vers la nouvelle demande (si pas déclarée comme perdue).
     *
     * @param ancienneDemande   la demande antérieure
     * @param nouveauPasseport  le nouveau passeport
     * @param nouvelleDemande   la nouvelle demande
     */
    private void copierAncienneCarteResident(Demande ancienneDemande, Passeport nouveauPasseport, Demande nouvelleDemande) {
        // Chercher la carte résident de l'ancienne demande
        List<CarteResident> anciennesCartes = carteResidentRepository.findByDemande(ancienneDemande);
        if (!anciennesCartes.isEmpty()) {
            CarteResident ancienneCarte = anciennesCartes.get(0); // Prendre la première
            
            // Créer une nouvelle carte avec les mêmes données
            CarteResident nouvelleCarte = new CarteResident();
            nouvelleCarte.setDateDebut(ancienneCarte.getDateDebut());
            nouvelleCarte.setDateFin(ancienneCarte.getDateFin());
            nouvelleCarte.setPasseport(nouveauPasseport);
            nouvelleCarte.setDemande(nouvelleDemande);
            
            // Si les dates ne sont pas présentes, utiliser des dates par défaut
            if (nouvelleCarte.getDateDebut() == null) {
                nouvelleCarte.setDateDebut(LocalDate.now());
            }
            if (nouvelleCarte.getDateFin() == null && nouveauPasseport.getDateExpiration() != null) {
                nouvelleCarte.setDateFin(nouveauPasseport.getDateExpiration());
            } else if (nouvelleCarte.getDateFin() == null) {
                nouvelleCarte.setDateFin(LocalDate.now().plusYears(5));
            }
            
            nouvelleCarte = carteResidentRepository.save(nouvelleCarte);
            
            // Générer le numéro avec le nouvel ID
            nouvelleCarte.setNumeroCarte("CR-" + nouvelleCarte.getId());
            carteResidentRepository.save(nouvelleCarte);
        }
    }

    /**
     * Copie l'ancien visa vers la nouvelle demande (si passeport non perdu).
     * Créé un nouveau Visa avec les mêmes dates mais lié à la nouvelle demande.
     *
     * @param ancienneDemande   la demande antérieure
     * @param nouvelleDemande   la nouvelle demande
     */
    private void copierAncienVisaEnConservantReference(Demande ancienneDemande, Demande nouvelleDemande) {
        // Chercher un visa lié à l'ancienne demande
        List<Visa> ancienVisa = visaRepository.findByDemande(ancienneDemande);
        
        if (!ancienVisa.isEmpty()) {
            Visa oldVisa = ancienVisa.get(0); // Prendre le premier
            
            // Créer une nouvelle visa avec les mêmes données
            Visa nouvelleVisa = new Visa();
            nouvelleVisa.setPasseport(oldVisa.getPasseport()); // Réutiliser le même passeport
            nouvelleVisa.setDateDebut(oldVisa.getDateDebut());
            nouvelleVisa.setDateFin(oldVisa.getDateFin());
            nouvelleVisa.setDemande(nouvelleDemande);
            nouvelleVisa.setReferenceVisa(oldVisa.getReferenceVisa());
            
            visaRepository.save(nouvelleVisa);
        }
    }

    /**
     * Transfère le visa vers un nouveau passeport en conservant la référence du visa.
     * (Perte passeport / TRANSFERT_VISA avec antécédents)
     */
    private void transfererVisaEnConservantReference(Demande ancienneDemande, Demande nouvelleDemande, Passeport nouveauPasseport) {
        List<Visa> ancienVisa = visaRepository.findByDemande(ancienneDemande);
        if (ancienVisa.isEmpty()) {
            // S'il n'y a pas de visa à copier, on crée un nouveau visa lié au nouveau passeport.
            // (Cas rare, mais évite de bloquer la déclaration)
            visaService.creer(nouveauPasseport, nouvelleDemande);
            return;
        }

        Visa oldVisa = ancienVisa.get(0);
        Visa nouvelleVisa = new Visa();
        nouvelleVisa.setPasseport(nouveauPasseport);
        nouvelleVisa.setDateDebut(oldVisa.getDateDebut());
        nouvelleVisa.setDateFin(oldVisa.getDateFin());
        nouvelleVisa.setDemande(nouvelleDemande);
        nouvelleVisa.setReferenceVisa(oldVisa.getReferenceVisa());
        visaRepository.save(nouvelleVisa);
    }

    /**
     * Crée une entrée dans l'historique des statuts.
     *
     * @param demande       la demande concernée
     * @param statut        le nouveau statut
     * @param commentaire   motif du changement
     */
    private void creerHistoriqueStatut(Demande demande, StatutDemande statut, String commentaire) {
        HistoriqueStatut historique = new HistoriqueStatut();
        historique.setDemande(demande);
        historique.setStatutDemande(statut);
        historique.setDateChangement(LocalDateTime.now());
        historique.setCommentaire(commentaire);
        historiqueStatutRepository.save(historique);
    }
}
