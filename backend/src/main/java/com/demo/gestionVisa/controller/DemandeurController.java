package com.demo.gestionVisa.controller;

import com.demo.gestionVisa.model.Demandeur;
import com.demo.gestionVisa.service.DemandeurService;
import com.demo.gestionVisa.dto.DemandeurDTO;
import com.demo.gestionVisa.enums.SituationFamiliale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.Optional;

/**
 * Controller pour la gestion des demandeurs (MVC)
 */
@Controller
@RequestMapping("/demandeurs")
public class DemandeurController {
    
    @Autowired
    private DemandeurService demandeurService;
    
    /**
     * Afficher le formulaire de création d'un enquêteur
     * GET /demandeurs/nouveau
     */
    @GetMapping("/nouveau")
    public String afficherFormulaireDemandeur(Model model) {
        model.addAttribute("situations", SituationFamiliale.values());
        return "demandeur/formulaire-demandeur";
    }
    
    /**
     * Créer un nouveau demandeur
     * POST /demandeurs/enregistrer
     */
    @PostMapping("/enregistrer")
    public String createDemandeur(
            @ModelAttribute DemandeurDTO demandeurDTO,
            RedirectAttributes redirectAttributes) {
        try {
            Demandeur demandeur = demandeurService.creerDemandeur(demandeurDTO);
            
            redirectAttributes.addFlashAttribute("success", "Demandeur créé avec succès");
            redirectAttributes.addFlashAttribute("demandeurId", demandeur.getId());
            
            return "redirect:/demandeurs/" + demandeur.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/demandeurs/nouveau";
        }
    }
    
    /**
     * Afficher les détails d'un demandeur
     * GET /demandeurs/{id}
     */
    @GetMapping("/{id}")
    public String afficherDemandeur(@PathVariable Long id, Model model) {
        Optional<Demandeur> demandeur = demandeurService.getDemandeurById(id);
        
        if (demandeur.isPresent()) {
            model.addAttribute("demandeur", demandeur.get());
            return "demandeur/detail-demandeur";
        }
        
        model.addAttribute("error", "Demandeur non trouvé");
        return "redirect:/demandeurs";
    }
    
    /**
     * Afficher la liste de tous les demandeurs
     * GET /demandeurs
     */
    @GetMapping
    public String afficherListeDemandeurs(Model model) {
        List<Demandeur> demandeurs = demandeurService.getAllDemandeurs();
        model.addAttribute("demandeurs", demandeurs);
        model.addAttribute("total", demandeurs.size());
        
        return "demandeur/liste-demandeurs";
    }
    
    /**
     * Afficher le formulaire de modification d'un demandeur
     * GET /demandeurs/{id}/modifier
     */
    @GetMapping("/{id}/modifier")
    public String afficherFormulaireModification(@PathVariable Long id, Model model) {
        Optional<Demandeur> demandeur = demandeurService.getDemandeurById(id);
        
        if (demandeur.isPresent()) {
            model.addAttribute("demandeur", demandeur.get());
            model.addAttribute("situations", SituationFamiliale.values());
            return "demandeur/formulaire-modification";
        }
        
        model.addAttribute("error", "Demandeur non trouvé");
        return "redirect:/demandeurs";
    }
    
    /**
     * Mettre à jour un demandeur
     * POST /demandeurs/{id}/modifier
     */
    @PostMapping("/{id}/modifier")
    public String updateDemandeur(
            @PathVariable Long id,
            @ModelAttribute DemandeurDTO demandeurDTO,
            RedirectAttributes redirectAttributes) {
        try {
            Demandeur demandeurUpdated = demandeurService.updateDemandeur(id, demandeurDTO);
            
            if (demandeurUpdated != null) {
                redirectAttributes.addFlashAttribute("success", "Demandeur mis à jour avec succès");
                return "redirect:/demandeurs/" + id;
            }
            
            redirectAttributes.addFlashAttribute("error", "Demandeur non trouvé");
            return "redirect:/demandeurs";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/demandeurs/" + id + "/modifier";
        }
    }
    
    /**
     * Supprimer un demandeur
     * POST /demandeurs/{id}/supprimer
     */
    @PostMapping("/{id}/supprimer")
    public String deleteDemandeur(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            Optional<Demandeur> demandeur = demandeurService.getDemandeurById(id);
            
            if (demandeur.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Demandeur non trouvé");
                return "redirect:/demandeurs";
            }
            
            demandeurService.deleteDemandeur(id);
            
            redirectAttributes.addFlashAttribute("success", "Demandeur supprimé avec succès");
            return "redirect:/demandeurs";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/demandeurs";
        }
    }
}
