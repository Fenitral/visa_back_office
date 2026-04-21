package com.demo.gestionVisa.service;

import com.demo.gestionVisa.model.Demande;
import com.demo.gestionVisa.model.Demandeur;
import com.demo.gestionVisa.model.VisaTransformable;
import com.demo.gestionVisa.repository.DemandeRepository;
import com.demo.gestionVisa.dto.DemandeRequestDTO;
import com.demo.gestionVisa.dto.DemandeResponseDTO;
import com.demo.gestionVisa.dto.DemandeurDTO;
import com.demo.gestionVisa.dto.PieceJustificativeDTO;
import com.demo.gestionVisa.dto.VisaTransformableDTO;
import com.demo.gestionVisa.enums.StatutDemande;
import com.demo.gestionVisa.enums.TypeDemande;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Service principal pour la gestion des demandes de visa
 * Contient la logique métier complexe
 */
@Service
public class DemandeService {
    
    @Autowired
    private DemandeRepository demandeRepository;
    
    @Autowired
    private DemandeurService demandeurService;
    
    @Autowired
    private VisaTransformableService visaTransformableService;
    
    @Autowired
    private PieceJustificativeService pieceJustificativeService;
    
    /**
     * Créer une nouvelle demande de visa
     * Applique la logique métier complexe pour l'attribution du statut
     */
    public Demande creerDemande(DemandeRequestDTO demandeRequest) throws Exception {
        // Validation des données obligatoires
        validerDonnees(demandeRequest);
        
        // Récupérer ou créer le demandeur
        Demandeur demandeur = demandeurService.getOuCreerDemandeur(demandeRequest.getDemandeur());
        
        // Récupérer le visa transformable
        VisaTransformable visa = visaTransformableService.getVisaByReference(
            demandeRequest.getVisaTransformable().getReference()
        ).orElseThrow(() -> new Exception("Visa transformable non trouvé"));
        
        // Créer la demande
        Demande demande = new Demande(demandeur, visa, demandeRequest.getTypeDemande());

        // Initialiser les champs non nuls avant le premier insert
        demande.setStatut(StatutDemande.DOSSIER_CREE);
        demande.setPiecesObligatoiresCompletes(false);
        demande.setDossierComplet(false);

        // Persister d'abord la demande pour obtenir un ID avant toute association/requete sur les pieces
        demande = demandeRepository.save(demande);
        
        // Créer les pièces justificatives
        if (demandeRequest.getPiecesJustificatives() != null) {
            for (PieceJustificativeDTO pieceDTO : demandeRequest.getPiecesJustificatives()) {
                pieceJustificativeService.creerPieceJustificative(demande, pieceDTO);
            }
        }
        
        // Recalculer les indicateurs seulement si des pieces sont fournies
        if (demandeRequest.getPiecesJustificatives() != null && !demandeRequest.getPiecesJustificatives().isEmpty()) {
            attribuerStatut(demande);
        }
        
        return demandeRepository.save(demande);
    }
    
    /**
     * Valider les données obligatoires de la demande
     */
    private void validerDonnees(DemandeRequestDTO demandeRequest) throws Exception {
        // Validation demandeur
        if (demandeRequest.getDemandeur() == null) {
            throw new Exception("Les informations du demandeur sont obligatoires");
        }
        
        if (demandeRequest.getDemandeur().getNom() == null || 
            demandeRequest.getDemandeur().getNom().isEmpty()) {
            throw new Exception("Le nom du demandeur est obligatoire");
        }
        
        if (demandeRequest.getDemandeur().getPrenom() == null || 
            demandeRequest.getDemandeur().getPrenom().isEmpty()) {
            throw new Exception("Le prénom du demandeur est obligatoire");
        }
        
        if (demandeRequest.getDemandeur().getDateNaissance() == null) {
            throw new Exception("La date de naissance est obligatoire");
        }
        
        if (demandeRequest.getDemandeur().getSituationFamiliale() == null) {
            throw new Exception("La situation familiale est obligatoire");
        }
        
        if (demandeRequest.getDemandeur().getNationalite() == null || 
            demandeRequest.getDemandeur().getNationalite().isEmpty()) {
            throw new Exception("La nationalité est obligatoire");
        }
        
        if (demandeRequest.getDemandeur().getAdresseMadagascar() == null || 
            demandeRequest.getDemandeur().getAdresseMadagascar().isEmpty()) {
            throw new Exception("L'adresse à Madagascar est obligatoire");
        }
        
        // Validation visa transformable
        if (demandeRequest.getVisaTransformable() == null) {
            throw new Exception("Les informations du visa transformable sont obligatoires");
        }
        
        if (demandeRequest.getVisaTransformable().getReference() == null || 
            demandeRequest.getVisaTransformable().getReference().isEmpty()) {
            throw new Exception("La référence du visa est obligatoire");
        }
        
        if (demandeRequest.getVisaTransformable().getDateEntree() == null) {
            throw new Exception("La date d'entrée à Madagascar est obligatoire");
        }
        
        if (demandeRequest.getVisaTransformable().getLieuEntree() == null || 
            demandeRequest.getVisaTransformable().getLieuEntree().isEmpty()) {
            throw new Exception("Le lieu d'entrée est obligatoire");
        }
        
        if (demandeRequest.getVisaTransformable().getDateExpiration() == null) {
            throw new Exception("La date d'expiration du visa est obligatoire");
        }
        
        // Validation type de demande
        if (demandeRequest.getTypeDemande() == null) {
            throw new Exception("Le type de demande est obligatoire");
        }
    }
    
    /**
     * Attribuer le statut approprié à la demande selon les pièces justificatives
     * 
     * Cas 1: Si pièces obligatoires manquent → DOSSIER_CREE
     * Cas 2: Si seules les pièces facultatives manquent → DOSSIER_CREE
     * Cas 3: Si toutes les pièces obligatoires présentes → DOSSIER_CREE (en attente de validation)
     * Cas 4: Si toutes les pièces (obligatoires + facultatives) présentes → DOSSIER_CREE (en attente de validation)
     */
    private void attribuerStatut(Demande demande) {
        // Vérifier les pièces obligatoires
        boolean piecesObligatoiresOk = pieceJustificativeService.verifierPiecesObligatoires(demande);
        
        // Vérifier le dossier complet
        boolean dossierComplet = pieceJustificativeService.verifierDossierComplet(demande);
        
        demande.setPiecesObligatoiresCompletes(piecesObligatoiresOk);
        demande.setDossierComplet(dossierComplet);
        
        // Toute demande commence en DOSSIER_CREE jusqu'à validation
        demande.setStatut(StatutDemande.DOSSIER_CREE);
    }
    
    /**
     * Récupérer une demande par ID
     */
    public Optional<Demande> getDemandeById(Long id) {
        return demandeRepository.findById(Objects.requireNonNull(id));
    }
    
    /**
     * Récupérer toutes les demandes d'un demandeur
     */
    public List<Demande> getDemandesByDemandeur(Demandeur demandeur) {
        return demandeRepository.findByDemandeur(demandeur);
    }
    
    /**
     * Récupérer toutes les demandes par statut
     */
    public List<Demande> getDemandesByStatut(StatutDemande statut) {
        return demandeRepository.findByStatut(statut);
    }
    
    /**
     * Récupérer toutes les demandes par type
     */
    public List<Demande> getDemandesByType(com.demo.gestionVisa.enums.TypeDemande type) {
        return demandeRepository.findByTypeDemande(type);
    }
    
    /**
     * Récupérer toutes les demandes
     */
    public List<Demande> getAllDemandes() {
        return demandeRepository.findAll();
    }
    
    /**
     * Mettre à jour le statut d'une demande
     */
    public Demande updateStatut(Long demandeId, StatutDemande nouveauStatut) {
        Optional<Demande> demandeOptional = demandeRepository.findById(Objects.requireNonNull(demandeId));
        
        if (demandeOptional.isPresent()) {
            Demande demande = demandeOptional.get();
            demande.setStatut(nouveauStatut);
            demande.setDateModification(LocalDateTime.now());
            
            if (nouveauStatut == StatutDemande.VALIDEE || nouveauStatut == StatutDemande.REJETEE) {
                demande.setDateTraitement(LocalDateTime.now());
            }
            
            return demandeRepository.save(demande);
        }
        
        return null;
    }

    /**
     * Modifier une demande existante (demandeur + visa + type)
     */
    public Demande modifierDemande(Long demandeId, DemandeurDTO demandeurDTO, VisaTransformableDTO visaDTO, TypeDemande typeDemande) throws Exception {
        Optional<Demande> demandeOptional = demandeRepository.findById(Objects.requireNonNull(demandeId));
        if (demandeOptional.isEmpty()) {
            throw new Exception("Demande non trouvée");
        }
        Demande demande = demandeOptional.get();

        if (demandeurDTO == null || visaDTO == null || typeDemande == null) {
            throw new Exception("Données de modification incomplètes");
        }

        Demandeur updatedDemandeur = demandeurService.updateDemandeur(demande.getDemandeur().getId(), demandeurDTO);
        if (updatedDemandeur == null) {
            throw new Exception("Demandeur non trouvé");
        }

        VisaTransformable updatedVisa = visaTransformableService.updateVisaTransformable(demande.getVisaTransformable().getId(), visaDTO);
        if (updatedVisa == null) {
            throw new Exception("Visa transformable non trouvé");
        }

        demande.setDemandeur(updatedDemandeur);
        demande.setVisaTransformable(updatedVisa);
        demande.setTypeDemande(typeDemande);
        demande.setDateModification(LocalDateTime.now());

        return demandeRepository.save(demande);
    }
    
    /**
     * Revérifier les statuts après ajout de pièces
     */
    public Demande revérifierEtMajStatut(Long demandeId) {
        Optional<Demande> demandeOptional = demandeRepository.findById(Objects.requireNonNull(demandeId));
        
        if (demandeOptional.isPresent()) {
            Demande demande = demandeOptional.get();
            attribuerStatut(demande);
            demande.setDateModification(LocalDateTime.now());
            
            return demandeRepository.save(demande);
        }
        
        return null;
    }
    
    /**
     * Supprimer une demande
     */
    public void deleteDemande(Long id) {
        demandeRepository.deleteById(Objects.requireNonNull(id));
    }
    
    /**
     * Convertir une Demande en DemandeResponseDTO
     */
    public DemandeResponseDTO convertToResponseDTO(Demande demande) {
        DemandeResponseDTO response = new DemandeResponseDTO();
        response.setId(demande.getId());
        response.setStatut(demande.getStatut());
        response.setTypeDemande(demande.getTypeDemande());
        response.setPiecesObligatoiresCompletes(demande.isPiecesObligatoiresCompletes());
        response.setDossierComplet(demande.isDossierComplet());
        response.setDateCreation(demande.getDateCreation());
        response.setDateModification(demande.getDateModification());
        
        return response;
    }
}
