package com.demo.gestionVisa.controller;

import com.demo.gestionVisa.model.Demande;
import com.demo.gestionVisa.model.PieceJustificative;
import com.demo.gestionVisa.service.DemandeService;
import com.demo.gestionVisa.service.PieceJustificativeService;
import com.demo.gestionVisa.dto.DemandeRequestDTO;
import com.demo.gestionVisa.dto.PieceJustificativeDTO;
import com.demo.gestionVisa.enums.StatutDemande;
import com.demo.gestionVisa.enums.TypeDemande;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.Optional;

/**
 * Controller pour la gestion des demandes de visa (MVC)
 */
@Controller
@RequestMapping("/demandes")
public class DemandeController {
    
    @Autowired
    private DemandeService demandeService;
    
    @Autowired
    private PieceJustificativeService pieceJustificativeService;
    
    /**
     * Afficher le formulaire de création d'une nouvelle demande
     * GET /demandes/nouvelle
     */
    @GetMapping("/nouvelle")
    public String afficherFormulaireDemande(Model model) {
        model.addAttribute("types", TypeDemande.values());
        return "demande/formulaire-demande";
    }
    
    /**
     * Enregistrer une nouvelle demande de visa
     * POST /demandes/enregistrer
     */
    @PostMapping("/enregistrer")
    public String enregistrerDemande(
            @ModelAttribute DemandeRequestDTO demandeRequest,
            RedirectAttributes redirectAttributes) {
        try {
            Demande demande = demandeService.creerDemande(demandeRequest);
            
            redirectAttributes.addFlashAttribute("success", "Demande enregistrée avec succès");
            redirectAttributes.addFlashAttribute("demandeId", demande.getId());
            redirectAttributes.addFlashAttribute("statut", demande.getStatut());
            
            return "redirect:/demandes/" + demande.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/demandes/nouvelle";
        }
    }
    
    /**
     * Afficher les détails d'une demande
     * GET /demandes/{id}
     */
    @GetMapping("/{id}")
    public String afficherDemande(@PathVariable Long id, Model model) {
        Optional<Demande> demande = demandeService.getDemandeById(id);
        
        if (demande.isPresent()) {
            Demande dem = demande.get();
            model.addAttribute("demande", dem);
            
            List<PieceJustificative> pieces = pieceJustificativeService.getPiecesByDemande(dem);
            model.addAttribute("pieces", pieces);
            
            model.addAttribute("statuts", StatutDemande.values());
            
            return "demande/detail-demande";
        }
        
        model.addAttribute("error", "Demande non trouvée");
        return "redirect:/demandes/liste";
    }
    
    /**
     * Afficher la liste de toutes les demandes
     * GET /demandes
     */
    @GetMapping
    public String afficherListeDemandes(Model model) {
        List<Demande> demandes = demandeService.getAllDemandes();
        model.addAttribute("demandes", demandes);
        model.addAttribute("total", demandes.size());
        
        return "demande/liste-demandes";
    }
    
    /**
     * Filtrer les demandes par statut
     * GET /demandes/statut/{statut}
     */
    @GetMapping("/statut/{statut}")
    public String afficherDemandesByStatut(@PathVariable String statut, Model model) {
        try {
            StatutDemande statutEnum = StatutDemande.valueOf(statut.toUpperCase());
            List<Demande> demandes = demandeService.getDemandesByStatut(statutEnum);
            
            model.addAttribute("demandes", demandes);
            model.addAttribute("statutFiltre", statut);
            model.addAttribute("total", demandes.size());
            
            return "demande/liste-demandes";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Statut invalide");
            return "redirect:/demandes";
        }
    }
    
    /**
     * Afficher le formulaire pour ajouter une pièce justificative
     * GET /demandes/{demandeId}/ajouter-piece
     */
    @GetMapping("/{demandeId}/ajouter-piece")
    public String afficherFormulairePiece(@PathVariable Long demandeId, Model model) {
        Optional<Demande> demande = demandeService.getDemandeById(demandeId);
        
        if (demande.isPresent()) {
            model.addAttribute("demandeId", demandeId);
            model.addAttribute("demande", demande.get());
            return "demande/ajouter-piece";
        }
        
        model.addAttribute("error", "Demande non trouvée");
        return "redirect:/demandes";
    }
    
    /**
     * Ajouter une pièce justificative à une demande
     * POST /demandes/{demandeId}/ajouter-piece
     */
    @PostMapping("/{demandeId}/ajouter-piece")
    public String ajouterPieceJustificative(
            @PathVariable Long demandeId,
            @ModelAttribute PieceJustificativeDTO pieceDTO,
            RedirectAttributes redirectAttributes) {
        try {
            Optional<Demande> demandeOptional = demandeService.getDemandeById(demandeId);
            
            if (demandeOptional.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Demande non trouvée");
                return "redirect:/demandes";
            }
            
            Demande demande = demandeOptional.get();
            PieceJustificative piece = pieceJustificativeService.creerPieceJustificative(demande, pieceDTO);
            
            // Revérifier et mettre à jour le statut
            demandeService.revérifierEtMajStatut(demandeId);
            
            redirectAttributes.addFlashAttribute("success", "Pièce justificative ajoutée avec succès");
            
            return "redirect:/demandes/" + demandeId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/demandes/" + demandeId + "/ajouter-piece";
        }
    }
    
    /**
     * Soumettre une pièce justificative
     * POST /demandes/pieces/{pieceId}/soumettre
     */
    @PostMapping("/pieces/{pieceId}/soumettre")
    public String soumettePiece(
            @PathVariable Long pieceId,
            RedirectAttributes redirectAttributes) {
        try {
            PieceJustificative piece = pieceJustificativeService.soumettePieceJustificative(pieceId);
            
            if (piece != null) {
                // Revérifier et mettre à jour le statut de la demande
                demandeService.revérifierEtMajStatut(piece.getDemande().getId());
                
                redirectAttributes.addFlashAttribute("success", "Pièce soumise avec succès");
                return "redirect:/demandes/" + piece.getDemande().getId();
            }
            
            redirectAttributes.addFlashAttribute("error", "Pièce non trouvée");
            return "redirect:/demandes";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/demandes";
        }
    }
    
    /**
     * Retirer une pièce justificative
     * POST /demandes/pieces/{pieceId}/retirer
     */
    @PostMapping("/pieces/{pieceId}/retirer")
    public String retirerPiece(
            @PathVariable Long pieceId,
            RedirectAttributes redirectAttributes) {
        try {
            Optional<PieceJustificative> pieceOptional = pieceJustificativeService.getPieceById(pieceId);
            
            if (pieceOptional.isPresent()) {
                PieceJustificative piece = pieceOptional.get();
                Long demandeId = piece.getDemande().getId();
                
                pieceJustificativeService.retirerPieceJustificative(pieceId);
                demandeService.revérifierEtMajStatut(demandeId);
                
                redirectAttributes.addFlashAttribute("success", "Pièce retirée avec succès");
                return "redirect:/demandes/" + demandeId;
            }
            
            redirectAttributes.addFlashAttribute("error", "Pièce non trouvée");
            return "redirect:/demandes";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/demandes";
        }
    }
    
    /**
     * Valider une demande
     * POST /demandes/{id}/valider
     */
    @PostMapping("/{id}/valider")
    public String validerDemande(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            Demande demandeUpdated = demandeService.updateStatut(id, StatutDemande.VALIDEE);
            
            if (demandeUpdated != null) {
                redirectAttributes.addFlashAttribute("success", "Demande validée avec succès");
                return "redirect:/demandes/" + id;
            }
            
            redirectAttributes.addFlashAttribute("error", "Demande non trouvée");
            return "redirect:/demandes";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/demandes/" + id;
        }
    }
    
    /**
     * Rejeter une demande
     * POST /demandes/{id}/rejeter
     */
    @PostMapping("/{id}/rejeter")
    public String rejeterDemande(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            Demande demandeUpdated = demandeService.updateStatut(id, StatutDemande.REJETEE);
            
            if (demandeUpdated != null) {
                redirectAttributes.addFlashAttribute("success", "Demande rejetée avec succès");
                return "redirect:/demandes/" + id;
            }
            
            redirectAttributes.addFlashAttribute("error", "Demande non trouvée");
            return "redirect:/demandes";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/demandes/" + id;
        }
    }
    
    /**
     * Supprimer une demande
     * POST /demandes/{id}/supprimer
     */
    @PostMapping("/{id}/supprimer")
    public String supprimerDemande(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            Optional<Demande> demande = demandeService.getDemandeById(id);
            
            if (demande.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Demande non trouvée");
                return "redirect:/demandes";
            }
            
            demandeService.deleteDemande(id);
            
            redirectAttributes.addFlashAttribute("success", "Demande supprimée avec succès");
            return "redirect:/demandes";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/demandes";
        }
    }
}
