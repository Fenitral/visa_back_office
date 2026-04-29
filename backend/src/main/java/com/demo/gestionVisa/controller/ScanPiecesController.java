package com.demo.gestionVisa.controller;

import com.demo.gestionVisa.dto.ScanDemandeDTO;
import com.demo.gestionVisa.exception.BusinessException;
import com.demo.gestionVisa.exception.ResourceNotFoundException;
import com.demo.gestionVisa.service.ScanPiecesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Contrôleur pour la gestion du scan des pièces justificatives
 * SPRINT 3
 */
@Controller
@RequestMapping("/demandes")
@Slf4j
public class ScanPiecesController {

    private final ScanPiecesService scanPiecesService;

    public ScanPiecesController(ScanPiecesService scanPiecesService) {
        this.scanPiecesService = scanPiecesService;
    }

    /**
     * GET /demandes/{id}/scan
     * Afficher la page de scan des pièces justificatives
     */
    @GetMapping("/{id}/scan")
    public String afficherPageScan(@PathVariable Long id, Model model) {
        try {
            ScanDemandeDTO etatScan = scanPiecesService.getEtatScan(id);
            model.addAttribute("scanDemande", etatScan);
            model.addAttribute("toutes_pieces_obligatoires_scannees", 
                etatScan.sontToutesPiecesObligatoiresScannees());
            return "demande/scan-pieces";
        } catch (ResourceNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error/404";
        }
    }

    /**
     * GET /demandes/{id}/scan/pieces (AJAX)
     * Récupère l'état du scan en JSON pour mise à jour dynamique
     */
    @GetMapping("/{id}/scan/pieces")
    @ResponseBody
    public ResponseEntity<ScanDemandeDTO> getEtatScanJSON(@PathVariable Long id) {
        try {
            ScanDemandeDTO etatScan = scanPiecesService.getEtatScan(id);
            return ResponseEntity.ok(etatScan);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * POST /demandes/{id}/scan/upload
     * Upload un fichier pour une pièce
     * 
     * Paramètres:
     *   - idPiece: L'ID de la pièce
     *   - file: Le fichier à uploader
     */
    @PostMapping("/{id}/scan/upload")
    @ResponseBody
    public ResponseEntity<?> uploadFichier(
            @PathVariable Long id,
            @RequestParam Long idPiece,
            @RequestParam("file") MultipartFile file) {
        
        try {
            scanPiecesService.uploadFichier(id, idPiece, file);
            log.info("Upload réussi - Demande: {}, Pièce: {}", id, idPiece);
            
            return ResponseEntity.ok()
                    .body("{\"message\": \"Fichier uploadé avec succès\", \"success\": true}");
        } catch (BusinessException e) {
            log.warn("Erreur métier lors de l'upload - Demande: {}, Pièce: {}", id, idPiece, e);
            return ResponseEntity.badRequest()
                    .body("{\"message\": \"" + e.getMessage() + "\", \"success\": false}");
        } catch (ResourceNotFoundException e) {
            log.warn("Ressource non trouvée lors de l'upload - Demande: {}", id, e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE /demandes/{id}/scan/fichier/{idDemandePiece}
     * Supprime un fichier scanné
     */
    @DeleteMapping("/{id}/scan/fichier/{idDemandePiece}")
    @ResponseBody
    public ResponseEntity<?> supprimerFichier(
            @PathVariable Long id,
            @PathVariable Long idDemandePiece) {
        
        try {
            scanPiecesService.supprimerFichier(idDemandePiece);
            log.info("Fichier supprimé - Demande: {}", id);
            
            return ResponseEntity.ok()
                    .body("{\"message\": \"Fichier supprimé avec succès\", \"success\": true}");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * POST /demandes/{id}/scan/valider
     * Valide que toutes les pièces obligatoires sont scannées et change le statut
     */
    @PostMapping("/{id}/scan/valider")
    public String validerScan(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        
        try {
            scanPiecesService.validerScan(id);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Scan validé avec succès. Statut changé à SCANNEE");
            log.info("Validation du scan réussie - Demande: {}", id);
            return "redirect:/demandes/" + id + "/details";
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            log.warn("Erreur lors de la validation du scan - Demande: {}", id, e);
            return "redirect:/demandes/" + id + "/scan";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            log.warn("Demande non trouvée - Demande: {}", id, e);
            return "redirect:/demandes";
        }
    }
}
