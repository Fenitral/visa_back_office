package com.demo.gestionVisa.service;

import com.demo.gestionVisa.dto.DemandeCreateDTO;
import com.demo.gestionVisa.dto.DemandeurDTO;
import com.demo.gestionVisa.dto.DemandeResponseDTO;
import com.demo.gestionVisa.dto.PasseportDTO;
import com.demo.gestionVisa.dto.PieceDTO;
import com.demo.gestionVisa.dto.VisaTransformableDTO;
import com.demo.gestionVisa.exception.BusinessException;
import com.demo.gestionVisa.exception.ResourceNotFoundException;
import com.demo.gestionVisa.mapper.DemandeMapper;
import com.demo.gestionVisa.model.*;
import com.demo.gestionVisa.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class DemandeService {

    private final DemandeRepository demandeRepository;
    private final HistoriqueStatutRepository historiqueStatutRepository;
    private final DemandePieceRepository demandePieceRepository;
    private final TypeVisaRepository typeVisaRepository;
    private final TypeDemandeRepository typeDemandeRepository;
    private final StatutDemandeRepository statutDemandeRepository;
    private final PieceRepository pieceRepository;
    private final VisaTransformableService visaTransformableService;
    private final DemandeurService demandeurService;
    private final DemandeurRepository demandeurRepository;
    private final PasseportService passeportService;
    private final DemandeMapper demandeMapper;
    private final PieceService pieceService;
    private final CarteResidentService carteResidentService;
    private final VisaService visaService;

    public DemandeService(DemandeRepository demandeRepository,
                         HistoriqueStatutRepository historiqueStatutRepository,
                         DemandePieceRepository demandePieceRepository,
                         TypeVisaRepository typeVisaRepository,
                         TypeDemandeRepository typeDemandeRepository,
                         StatutDemandeRepository statutDemandeRepository,
                         PieceRepository pieceRepository,
                         VisaTransformableService visaTransformableService,
                         DemandeurService demandeurService,
                         DemandeurRepository demandeurRepository,
                         PasseportService passeportService,
                         DemandeMapper demandeMapper,
                         PieceService pieceService,
                         CarteResidentService carteResidentService,
                         VisaService visaService) {
        this.demandeRepository = demandeRepository;
        this.historiqueStatutRepository = historiqueStatutRepository;
        this.demandePieceRepository = demandePieceRepository;
        this.typeVisaRepository = typeVisaRepository;
        this.typeDemandeRepository = typeDemandeRepository;
        this.statutDemandeRepository = statutDemandeRepository;
        this.pieceRepository = pieceRepository;
        this.visaTransformableService = visaTransformableService;
        this.demandeurService = demandeurService;
        this.demandeurRepository = demandeurRepository;
        this.passeportService = passeportService;
        this.demandeMapper = demandeMapper;
        this.pieceService = pieceService;
        this.carteResidentService = carteResidentService;
        this.visaService = visaService;
    }

    /**
     * Crée une nouvelle demande de visa complète.
     *
     * Étapes internes (dans cet ordre) :
     *   1. Créer ou récupérer le Demandeur (Dev1)
     *   2. Créer le Passeport lié (Dev1)
     *   3. Créer le VisaTransformable
     *   4. Résoudre TypeVisa par id
     *   5. Forcer TypeDemande = "NOUVELLE"
     *   6. Forcer StatutDemande = "CREE"
     *   7. Construire et sauvegarder la Demande
     *   8. Créer la Carte Résident liée au passeport
     *   9. Créer le Visa lié au passeport et à la demande
     *  10. Créer l'historique de statut initial
     *  11. Lier les pièces à la demande
     *  12. Retourner le DTO de réponse
     *
     * @param dto   formulaire complet soumis par l'utilisateur
     * @return      DemandeResponseDTO avec id, statut, pièces
     * @throws BusinessException si règle métier violée
     * @throws ResourceNotFoundException si TypeVisa introuvable
     */
    public DemandeResponseDTO creerDemande(DemandeCreateDTO dto) {

                LocalDateTime demandeDate = LocalDateTime.now();

        // ÉTAPE 1 — Demandeur (Dev1)
        Demandeur demandeur = demandeurService.creerOuRecuperer(dto.getDemandeurDTO());

        boolean demandeurSansAntecedent = demandeRepository.countByDemandeur(demandeur) == 0;

        boolean skipPieces = Boolean.TRUE.equals(dto.getSkipPiecesObligatoires())
                || (dto.getTypeDemandeSecondaire() != null && !dto.getTypeDemandeSecondaire().trim().isEmpty())
                || demandeurSansAntecedent;

        // ÉTAPE 2 — Passeport (Dev1)
        Passeport passeport = passeportService.creer(dto.getPasseportDTO(), demandeur);

        // ÉTAPE 3 — VisaTransformable
        VisaTransformable visa = visaTransformableService.creer(dto.getVisaDTO(), passeport);

        // ÉTAPE 4 — TypeVisa
        TypeVisa typeVisa = typeVisaRepository.findById(dto.getIdTypeVisa())
                .orElseThrow(() -> new ResourceNotFoundException("TypeVisa introuvable : id=" + dto.getIdTypeVisa()));

        // ÉTAPE 5 — TypeDemande selon le DTO (NOUVELLE ou DUPLICATA)
        String typeDemandeName = dto.getTypeDemande() != null ? dto.getTypeDemande() : "NOUVELLE";
        TypeDemande typeDemande = typeDemandeRepository.findByLibelle(typeDemandeName)
                .orElseThrow(() -> new ResourceNotFoundException("TypeDemande " + typeDemandeName + " introuvable en base"));

        // ÉTAPE 6 — StatutDemande: CREE pour création de demande
        StatutDemande statutCree = statutDemandeRepository.findByLibelle("CREE")
                .orElseThrow(() -> new ResourceNotFoundException("StatutDemande CREE introuvable en base"));
        StatutDemande statutInitial = statutCree;

        // ÉTAPE 7 — Construire et sauvegarder la Demande
        Demande demande = new Demande();
        demande.setDateDemande(demandeDate);
        demande.setDemandeur(demandeur);
        demande.setVisaTransformable(visa);
        demande.setTypeVisa(typeVisa);
        demande.setTypeDemande(typeDemande);
        demande.setStatutDemande(statutInitial);
        // demande.setDemandeurExistant(demandeurExistait);
        demande = demandeRepository.save(demande);

        // ÉTAPE 8 — Créer la Carte Résident liée au passeport
        carteResidentService.creer(passeport, demande);

        // ÉTAPE 9 — Créer le Visa lié au passeport et à la demande
        visaService.creer(passeport, demande);

        // ÉTAPE 10 — Historique statut initial (RG-07 : obligatoire)
        creerHistoriqueStatut(demande, statutInitial, "Création de la demande");

        // ÉTAPE 11 — Lier les pièces
        lierPiecesADemande(demande, dto.getPiecesFournies(), typeVisa, skipPieces);

        String typeDemandeSecondaire = dto.getTypeDemandeSecondaire();
        if (typeDemandeSecondaire != null && !typeDemandeSecondaire.trim().isEmpty()
                && !typeDemandeSecondaire.equalsIgnoreCase(typeDemandeName)) {
            TypeDemande typeSecondaire = typeDemandeRepository.findByLibelle(typeDemandeSecondaire.trim())
                    .orElseThrow(() -> new ResourceNotFoundException("TypeDemande " + typeDemandeSecondaire + " introuvable en base"));

            Demande demandeSecondaire = new Demande();
            demandeSecondaire.setDateDemande(demandeDate);
            demandeSecondaire.setDemandeur(demandeur);
            demandeSecondaire.setVisaTransformable(visa);
            demandeSecondaire.setTypeVisa(typeVisa);
            demandeSecondaire.setTypeDemande(typeSecondaire);
            demandeSecondaire.setStatutDemande(statutInitial);
            demandeSecondaire = demandeRepository.save(demandeSecondaire);

                        carteResidentService.creer(passeport, demandeSecondaire);

            creerHistoriqueStatut(demandeSecondaire, statutInitial, "Création de la demande (secondaire)");
            lierPiecesADemande(demandeSecondaire, dto.getPiecesFournies(), typeVisa, skipPieces);
        }

        // ÉTAPE 12 — Recharger avec pièces et retourner DTO
        Demande demandeFinal = demandeRepository.findById(demande.getId()).orElseThrow();
        return demandeMapper.toResponseDTO(demandeFinal);
    }

    /**
     * Crée une entrée dans l'historique des statuts.
     * Appelée à chaque changement de statut (y compris la création).
     *
     * @param demande       la demande concernée
     * @param statut        le nouveau statut
     * @param commentaire   motif du changement (ex: "Création de la demande")
     */
    private void creerHistoriqueStatut(Demande demande, StatutDemande statut, String commentaire) {
        HistoriqueStatut historique = new HistoriqueStatut();
        historique.setDemande(demande);
        historique.setStatutDemande(statut);
        historique.setDateChangement(LocalDateTime.now());
        historique.setCommentaire(commentaire);
        historiqueStatutRepository.save(historique);
    }

    /**
     * Lie les pièces (communes + spécifiques) à une demande.
     * Vérifie que toutes les pièces obligatoires sont fournies.
     *
     * Règles :
     *   RG-08 : lier COMMUN + spécifiques selon typeVisa
     *   RG-09 : pièce obligatoire non fournie → BusinessException
     *
     * @param demande           la demande à lier
     * @param piecesFournies    ids des pièces cochées dans le formulaire
     * @param typeVisa          type de visa sélectionné
     * @throws BusinessException si pièce obligatoire manquante
     */
        private void lierPiecesADemande(Demande demande, List<Long> piecesFournies, TypeVisa typeVisa, boolean skipPiecesObligatoires) {
        List<String> codes = List.of("COMMUN", typeVisa.getLibelle().toUpperCase());
        List<Piece> toutes = pieceRepository.findByTypePieceCodeIn(codes);

        for (Piece piece : toutes) {
            boolean fourni = piecesFournies != null && piecesFournies.contains(piece.getId());

            // RG-09 : contrôle pièce obligatoire
                        if (!skipPiecesObligatoires && Boolean.TRUE.equals(piece.getObligatoire()) && !fourni) {
                throw new BusinessException("Pièce obligatoire non fournie : " + piece.getNom());
            }

            DemandePiece dp = new DemandePiece();
            dp.setDemande(demande);
            dp.setPiece(piece);
            dp.setFourni(fourni);
            demandePieceRepository.save(dp);
        }
    }

    /**
     * Récupère une demande par son identifiant.
     *
     * @param id    identifiant de la demande
     * @return      DemandeResponseDTO
     * @throws ResourceNotFoundException si demande absente
     */
    public DemandeResponseDTO getDemande(Long id) {
        Demande demande = demandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Demande introuvable : id=" + id));
        return demandeMapper.toResponseDTO(demande);
    }

    /**
     * Retourne les pièces à afficher pour un type visa donné (AJAX).
     * Retourne uniquement les pièces SPÉCIFIQUES (pas les communes,
     * qui sont déjà affichées statiquement dans le formulaire).
     *
     * @param idTypeVisa    id du type visa sélectionné dans le select
     * @return              liste de PieceDTO spécifiques
     */
    public List<PieceDTO> getPiecesFormulaire(Long idTypeVisa) {
        TypeVisa typeVisa = typeVisaRepository.findById(idTypeVisa)
                .orElseThrow(() -> new ResourceNotFoundException("TypeVisa introuvable : id=" + idTypeVisa));
        return pieceService.getPiecesParTypeVisa(typeVisa.getLibelle());
    }

    /**
     * Récupère toutes les demandes.
     *
     * @return  liste de toutes les DemandeResponseDTO
     */
    public List<DemandeResponseDTO> getToutesDemandes() {
        return demandeRepository.findAll()
                .stream()
                .map(demandeMapper::toResponseDTO)
                .toList();
    }

    /**
     * Supprime une demande (et ses dépendances en cascade).
     *
     * @param id  id de la demande
     * @throws ResourceNotFoundException si demande absente
     */
    public void supprimerDemande(Long id) {
        Demande demande = demandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Demande introuvable : id=" + id));

        // Supprimer les historiques
        historiqueStatutRepository.deleteAll(demande.getHistoriques());

        // Supprimer les pièces
        demandePieceRepository.deleteAll(demande.getDemandePieces());

        // Supprimer la demande
        demandeRepository.delete(demande);
    }

    public DemandeCreateDTO getDemandePourModification(Long id) {
        Demande demande = demandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Demande introuvable : id=" + id));

        if (demande.getDemandeur() == null || demande.getVisaTransformable() == null || demande.getVisaTransformable().getPasseport() == null) {
            throw new BusinessException("La demande est incomplète et ne peut pas être modifiée via le formulaire.");
        }

        Demandeur demandeur = demande.getDemandeur();
        Passeport passeport = demande.getVisaTransformable().getPasseport();
        VisaTransformable visa = demande.getVisaTransformable();

        DemandeurDTO demandeurDTO = DemandeurDTO.builder()
                .nom(demandeur.getNom())
                .prenom(demandeur.getPrenom())
                .nomJeuneFille(demandeur.getNomJeuneFille())
                .dateNaissance(demandeur.getDateNaissance())
                .lieuNaissance(demandeur.getLieuNaissance())
                .idSituationFamiliale(demandeur.getSituationFamiliale() != null ? demandeur.getSituationFamiliale().getId() : null)
                .idNationalite(demandeur.getNationalite() != null ? demandeur.getNationalite().getId() : null)
                .adresseMadagascar(demandeur.getAdresseMadagascar())
                .telephone(demandeur.getTelephone())
                .email(demandeur.getEmail())
                .build();

        PasseportDTO passeportDTO = PasseportDTO.builder()
                .numero(passeport.getNumero())
                .dateDelivrance(passeport.getDateDelivrance())
                .dateExpiration(passeport.getDateExpiration())
                .build();

        VisaTransformableDTO visaDTO = VisaTransformableDTO.builder()
                .referenceVisa(visa.getReferenceVisa())
                .dateEntree(visa.getDateEntree())
                .lieuEntree(visa.getLieuEntree())
                .dateExpiration(visa.getDateExpiration())
                .build();

        List<Long> piecesFournies = demande.getDemandePieces() == null ? List.of() :
                demande.getDemandePieces().stream()
                        .filter(dp -> Boolean.TRUE.equals(dp.getFourni()))
                        .map(dp -> dp.getPiece() != null ? dp.getPiece().getId() : null)
                        .filter(Objects::nonNull)
                        .toList();

        return DemandeCreateDTO.builder()
                .demandeurDTO(demandeurDTO)
                .passeportDTO(passeportDTO)
                .visaDTO(visaDTO)
                .idTypeVisa(demande.getTypeVisa() != null ? demande.getTypeVisa().getId() : null)
                .typeDemande(demande.getTypeDemande() != null ? demande.getTypeDemande().getLibelle() : "NOUVELLE")
                .piecesFournies(piecesFournies)
                .build();
    }

    public DemandeResponseDTO modifierDemande(Long id, DemandeCreateDTO dto) {
        Demande demande = demandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Demande introuvable : id=" + id));

        if (demande.getDemandeur() == null || demande.getVisaTransformable() == null || demande.getVisaTransformable().getPasseport() == null) {
            throw new BusinessException("La demande est incomplète et ne peut pas être modifiée via le formulaire.");
        }

        // TypeVisa
        TypeVisa typeVisa = typeVisaRepository.findById(dto.getIdTypeVisa())
                .orElseThrow(() -> new ResourceNotFoundException("TypeVisa introuvable : id=" + dto.getIdTypeVisa()));
        demande.setTypeVisa(typeVisa);

        // Demandeur / Passeport / VisaTransformable
        demandeurService.modifier(demande.getDemandeur(), dto.getDemandeurDTO());
        passeportService.modifier(demande.getVisaTransformable().getPasseport(), dto.getPasseportDTO());
        visaTransformableService.modifier(demande.getVisaTransformable(), dto.getVisaDTO());

        demandeRepository.save(demande);

        // Pièces (recalculées sur le typeVisa courant)
        demandePieceRepository.deleteAll(demande.getDemandePieces());
                boolean skipPieces = Boolean.TRUE.equals(dto.getSkipPiecesObligatoires());
                lierPiecesADemande(demande, dto.getPiecesFournies(), typeVisa, skipPieces);

        return demandeMapper.toResponseDTO(demande);
    }

    /**
     * Récupère une demande par son identifiant et toutes les demandes du même demandeur.
     * Retourne un objet avec la demande principale et les demandes liées.
     *
     * @param id    identifiant de la demande
     * @return      map contenant "principale" (la demande) et "liees" (autres demandes du même demandeur)
     * @throws ResourceNotFoundException si demande absente
     */
    public java.util.Map<String, Object> getDemandeWithRelated(Long id) {
        Demande demande = demandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Demande introuvable : id=" + id));
        
        DemandeResponseDTO demandePrincipale = demandeMapper.toResponseDTO(demande);
        
        // Récupérer toutes les demandes du même demandeur
        List<DemandeResponseDTO> demandesLiees = demandeRepository.findByDemandeur(demande.getDemandeur())
                .stream()
                .filter(d -> !d.getId().equals(id)) // Exclure la demande principale
                .map(demandeMapper::toResponseDTO)
                .toList();
        
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("principale", demandePrincipale);
        result.put("liees", demandesLiees);
        return result;
    }

    /**
     * Récupère toutes les demandes associées à un numéro de passeport.
     *
     * @param numeroPasSeport   numéro du passeport
     * @return                  liste de DemandeResponseDTO
     * @throws ResourceNotFoundException si passeport non trouvé
     */
    public List<DemandeResponseDTO> getDemandesByPasseport(String numeroPasSeport) {
        // Récupérer le passeport par son numéro
        Passeport passeport = passeportService.findByNumero(numeroPasSeport)
                .orElseThrow(() -> new ResourceNotFoundException("Passeport introuvable : " + numeroPasSeport));
        
        // Récupérer le demandeur associé au passeport
        Demandeur demandeur = passeport.getDemandeur();
        if (demandeur == null) {
            throw new ResourceNotFoundException("Demandeur associé au passeport introuvable");
        }
        
        // Retourner toutes les demandes du demandeur
        return demandeRepository.findByDemandeur(demandeur)
                .stream()
                .map(demandeMapper::toResponseDTO)
                .toList();
    }
}