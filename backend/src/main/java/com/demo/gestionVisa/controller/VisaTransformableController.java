package com.demo.gestionVisa.controller;

import com.demo.gestionVisa.model.VisaTransformable;
import com.demo.gestionVisa.model.Demandeur;
import com.demo.gestionVisa.service.VisaTransformableService;
import com.demo.gestionVisa.service.DemandeurService;
import com.demo.gestionVisa.dto.VisaTransformableDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.Optional;

/**
 * Controller pour la gestion des visas transformables (MVC)
 */
@Controller
@RequestMapping("/visas-transformables")
public class VisaTransformableController {
    
    @Autowired
    private VisaTransformableService visaTransformableService;
    
    @Autowired
    private DemandeurService demandeurService;
    
    /**
     * Afficher le formulaire de création d'un visa transformable
     * GET /visas-transformables/nouveau
     */
    @GetMapping("/nouveau")
    public String afficherFormulaireVisa(Model model) {
        List<Demandeur> demandeurs = demandeurService.getAllDemandeurs();
        model.addAttribute("demandeurs", demandeurs);
        return "visa/formulaire-visa";
    }
    
    /**
     * Créer un nouveau visa transformable
     * POST /visas-transformables/enregistrer
     */
    @PostMapping("/enregistrer")
    public String createVisaTransformable(
            @ModelAttribute VisaTransformableDTO visaDTO,
            RedirectAttributes redirectAttributes) {
        try {
            VisaTransformable visa = visaTransformableService.creerVisaTransformable(visaDTO);
            
            redirectAttributes.addFlashAttribute("success", "Visa transformable créé avec succès");
            redirectAttributes.addFlashAttribute("visaId", visa.getId());
            
            return "redirect:/visas-transformables/" + visa.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/visas-transformables/nouveau";
        }
    }
    
    /**
     * Afficher les détails d'un visa transformable
     * GET /visas-transformables/{id}
     */
    @GetMapping("/{id}")
    public String afficherVisa(@PathVariable Long id, Model model) {
        Optional<VisaTransformable> visa = visaTransformableService.getVisaById(id);
        
        if (visa.isPresent()) {
            model.addAttribute("visa", visa.get());
            return "visa/detail-visa";
        }
        
        model.addAttribute("error", "Visa non trouvé");
        return "redirect:/visas-transformables";
    }
    
    /**
     * Afficher la liste de tous les visas transformables
     * GET /visas-transformables
     */
    @GetMapping
    public String afficherListeVisas(Model model) {
        List<VisaTransformable> visas = visaTransformableService.getAllVisas();
        model.addAttribute("visas", visas);
        model.addAttribute("total", visas.size());
        
        return "visa/liste-visas";
    }
    
    /**
     * Afficher le formulaire de modification d'un visa
     * GET /visas-transformables/{id}/modifier
     */
    @GetMapping("/{id}/modifier")
    public String afficherFormulaireModification(@PathVariable Long id, Model model) {
        Optional<VisaTransformable> visa = visaTransformableService.getVisaById(id);
        
        if (visa.isPresent()) {
            List<Demandeur> demandeurs = demandeurService.getAllDemandeurs();
            model.addAttribute("visa", visa.get());
            model.addAttribute("demandeurs", demandeurs);
            return "visa/formulaire-modification";
        }
        
        model.addAttribute("error", "Visa non trouvé");
        return "redirect:/visas-transformables";
    }
    
    /**
     * Mettre à jour un visa transformable
     * POST /visas-transformables/{id}/modifier
     */
    @PostMapping("/{id}/modifier")
    public String updateVisaTransformable(
            @PathVariable Long id,
            @ModelAttribute VisaTransformableDTO visaDTO,
            RedirectAttributes redirectAttributes) {
        try {
            VisaTransformable visaUpdated = visaTransformableService.updateVisaTransformable(id, visaDTO);
            
            if (visaUpdated != null) {
                redirectAttributes.addFlashAttribute("success", "Visa mis à jour avec succès");
                return "redirect:/visas-transformables/" + id;
            }
            
            redirectAttributes.addFlashAttribute("error", "Visa non trouvé");
            return "redirect:/visas-transformables";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/visas-transformables/" + id + "/modifier";
        }
    }
    
    /**
     * Supprimer un visa transformable
     * POST /visas-transformables/{id}/supprimer
     */
    @PostMapping("/{id}/supprimer")
    public String deleteVisaTransformable(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            Optional<VisaTransformable> visa = visaTransformableService.getVisaById(id);
            
            if (visa.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Visa non trouvé");
                return "redirect:/visas-transformables";
            }
            
            visaTransformableService.deleteVisa(id);
            
            redirectAttributes.addFlashAttribute("success", "Visa supprimé avec succès");
            return "redirect:/visas-transformables";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/visas-transformables";
        }
    }
}
