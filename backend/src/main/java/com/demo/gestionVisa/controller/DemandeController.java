package com.demo.gestionVisa.controller;

import com.demo.gestionVisa.model.Demande;
import com.demo.gestionVisa.model.PieceJustificative;
import com.demo.gestionVisa.service.DemandeService;
import com.demo.gestionVisa.service.PieceJustificativeService;
import com.demo.gestionVisa.dto.DemandeRequestDTO;
import com.demo.gestionVisa.dto.DemandeurDTO;
import com.demo.gestionVisa.dto.PieceJustificativeDTO;
import com.demo.gestionVisa.dto.VisaTransformableDTO;
import com.demo.gestionVisa.enums.SituationFamiliale;
import com.demo.gestionVisa.enums.StatutDemande;
import com.demo.gestionVisa.enums.TypeDemande;
import com.demo.gestionVisa.enums.TypePieceJustificative;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/demandes")
public class DemandeController {
    
    @Autowired
    private DemandeService demandeService;
    
    @Autowired
    private PieceJustificativeService pieceJustificativeService;
    
    @GetMapping("/nouvelle")
    public String afficherFormulaireDemande(Model model) {
        model.addAttribute("typesPieces", TypePieceJustificative.values());
        model.addAttribute("types", TypeDemande.values());
        model.addAttribute("demandeRequest", new DemandeRequestDTO());
        model.addAttribute("situations", SituationFamiliale.values());
        return "demande/formulaire-demande";
    }
    
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
    
    @GetMapping("/{id}")
    public String afficherDemande(@PathVariable Long id, Model model) {
        Optional<Demande> demande = demandeService.getDemandeById(id);
        
        if (demande.isPresent()) {
            Demande dem = demande.get();
            model.addAttribute("demande", dem);
            
            model.addAttribute("statuts", StatutDemande.values());
            
            return "demande/detail-demande";
        }
        
        model.addAttribute("error", "Demande non trouvée");
        return "redirect:/demandes";
    }

    @GetMapping("/{id}/modifier")
    public String afficherModification(@PathVariable Long id, Model model) {
        Optional<Demande> demande = demandeService.getDemandeById(id);
        if (demande.isEmpty()) {
            model.addAttribute("error", "Demande non trouvée");
            return "redirect:/demandes";
        }

        Demande dem = demande.get();
        model.addAttribute("demande", dem);

        List<PieceJustificative> pieces = pieceJustificativeService.getPiecesByDemande(dem);
        Map<String, PieceJustificative> piecesParType = new HashMap<>();
        for (PieceJustificative p : pieces) {
            if (p.getTypePiece() != null) {
                piecesParType.put(p.getTypePiece().name(), p);
            }
        }
        model.addAttribute("typesPieces", TypePieceJustificative.values());
        model.addAttribute("piecesParType", piecesParType);

        model.addAttribute("typesDemande", TypeDemande.values());
        model.addAttribute("situations", SituationFamiliale.values());

        return "demande/modifier-pieces";
    }

    @PostMapping("/{id}/modifier")
    public String enregistrerModification(
            @PathVariable Long id,
            @RequestParam("nom") String nom,
            @RequestParam("prenom") String prenom,
            @RequestParam(value = "nomJeuneFille", required = false) String nomJeuneFille,
            @RequestParam("dateNaissance") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateNaissance,
            @RequestParam("situationFamiliale") SituationFamiliale situationFamiliale,
            @RequestParam("nationalite") String nationalite,
            @RequestParam(value = "profession", required = false) String profession,
            @RequestParam("adresseMadagascar") String adresseMadagascar,
            @RequestParam(value = "telephone", required = false) String telephone,
            @RequestParam("referenceVisa") String referenceVisa,
            @RequestParam("dateEntree") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateEntree,
            @RequestParam("lieuEntree") String lieuEntree,
            @RequestParam("dateExpiration") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateExpiration,
            @RequestParam("typeDemande") TypeDemande typeDemande,
            @RequestParam(value = "pieces", required = false) List<String> piecesSelectionnees,
            RedirectAttributes redirectAttributes) {
        try {
            Optional<Demande> demandeOptional = demandeService.getDemandeById(id);
            if (demandeOptional.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Demande non trouvée");
                return "redirect:/demandes";
            }

            Demande dem = demandeOptional.get();

            DemandeurDTO demandeurDTO = new DemandeurDTO();
            demandeurDTO.setNom(nom);
            demandeurDTO.setPrenom(prenom);
            demandeurDTO.setNomJeuneFille(nomJeuneFille);
            demandeurDTO.setDateNaissance(dateNaissance);
            demandeurDTO.setSituationFamiliale(situationFamiliale);
            demandeurDTO.setNationalite(nationalite);
            demandeurDTO.setProfession(profession);
            demandeurDTO.setAdresseMadagascar(adresseMadagascar);
            demandeurDTO.setTelephone(telephone);

            VisaTransformableDTO visaDTO = new VisaTransformableDTO();
            visaDTO.setReference(referenceVisa);
            visaDTO.setDateEntree(dateEntree);
            visaDTO.setLieuEntree(lieuEntree);
            visaDTO.setDateExpiration(dateExpiration);

            dem = demandeService.modifierDemande(id, demandeurDTO, visaDTO, typeDemande);

            for (TypePieceJustificative type : TypePieceJustificative.values()) {
                boolean shouldBeSubmitted = piecesSelectionnees != null && piecesSelectionnees.contains(type.name());

                Optional<PieceJustificative> existing = pieceJustificativeService.getPieceByDemandeAndType(dem, type);

                if (shouldBeSubmitted) {
                    if (existing.isPresent()) {
                        if (!existing.get().isSousmise()) {
                            pieceJustificativeService.soumettePieceJustificative(existing.get().getId());
                        }
                    } else {
                        PieceJustificativeDTO dto = new PieceJustificativeDTO();
                        dto.setTypePiece(type);
                        dto.setNomFichier("piece-" + type.name() + ".pdf");
                        dto.setSousmise(true);
                        pieceJustificativeService.creerPieceJustificative(dem, dto);
                    }
                } else {
                    if (existing.isPresent() && existing.get().isSousmise()) {
                        pieceJustificativeService.retirerPieceJustificative(existing.get().getId());
                    }
                }
            }

            demandeService.revérifierEtMajStatut(id);
            redirectAttributes.addFlashAttribute("success", "Modification enregistrée");
            return "redirect:/demandes";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/demandes/" + id + "/modifier";
        }
    }

    @GetMapping("/{id}/imprimer")
    public String imprimerDemande(@PathVariable Long id, Model model) {
        Optional<Demande> demande = demandeService.getDemandeById(id);
        if (demande.isEmpty()) {
            model.addAttribute("error", "Demande non trouvée");
            return "redirect:/demandes";
        }

        Demande dem = demande.get();
        model.addAttribute("demande", dem);

        List<PieceJustificative> pieces = pieceJustificativeService.getPiecesByDemande(dem);
        Map<String, PieceJustificative> piecesParType = new HashMap<>();
        for (PieceJustificative p : pieces) {
            if (p.getTypePiece() != null) {
                piecesParType.put(p.getTypePiece().name(), p);
            }
        }
        model.addAttribute("typesPieces", TypePieceJustificative.values());
        model.addAttribute("piecesParType", piecesParType);

        return "demande/imprimer-demande";
    }
    
    @GetMapping
    public String afficherListeDemandes(Model model) {
        List<Demande> demandes = demandeService.getAllDemandes();
        model.addAttribute("demandes", demandes);
        model.addAttribute("total", demandes.size());
        
        return "demande/liste-demandes";
    }
    
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
            pieceJustificativeService.creerPieceJustificative(demande, pieceDTO);
            
            demandeService.revérifierEtMajStatut(demandeId);
            
            redirectAttributes.addFlashAttribute("success", "Pièce justificative ajoutée avec succès");
            
            return "redirect:/demandes/" + demandeId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/demandes/" + demandeId + "/ajouter-piece";
        }
    }
    
    @PostMapping("/pieces/{pieceId}/soumettre")
    public String soumettePiece(
            @PathVariable Long pieceId,
            RedirectAttributes redirectAttributes) {
        try {
            PieceJustificative piece = pieceJustificativeService.soumettePieceJustificative(pieceId);
            
            if (piece != null) {
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
