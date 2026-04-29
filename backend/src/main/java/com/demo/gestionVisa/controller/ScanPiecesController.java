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
     * IMPORTANT: Vérifie que toutes les pièces requises ont été fournies avant d'accéder au scan
     */
    @GetMapping("/{id}/scan")
    public String afficherPageScan(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            ScanDemandeDTO etatScan = scanPiecesService.getEtatScan(id);
            
            // Vérifier que toutes les pièces ont été fournies
            boolean toutesPiecesFournies = etatScan.getPieces().stream()
                    .allMatch(p -> p.getFourni() != null && p.getFourni());
            
            if (!toutesPiecesFournies) {
                // Il y a des pièces manquantes, rediriger vers la page de détails
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Vous devez d'abord compléter toutes les pièces manquantes avant de pouvoir scanner.");
                log.warn("Tentative d'accès au scan avec pièces manquantes - Demande: {}", id);
                return "redirect:/demandes/" + id + "/details";
            }
            
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
            
            // Récupérer l'état mis à jour après la suppression
            ScanDemandeDTO etatMisAJour = scanPiecesService.getEtatScan(id);
            
            return ResponseEntity.ok()
                    .body(Map.of(
                        "message", "Fichier supprimé avec succès",
                        "success", true,
                        "scanDemande", etatMisAJour
                    ));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Erreur lors de la suppression du fichier", e);
            return ResponseEntity.badRequest()
                    .body(Map.of(
                        "message", "Erreur lors de la suppression: " + e.getMessage(),
                        "success", false
                    ));
        }
    }

    /**
     * POST /demandes/{id}/marquer-piece-fournie
     * Marque une pièce comme fournie (complétée)
     * 
     * Paramètres:
     *   - idPiece: L'ID de la pièce
     */
    @PostMapping("/{id}/marquer-piece-fournie")
    @ResponseBody
    public ResponseEntity<?> marquerPieceCommeFournie(
            @PathVariable Long id,
            @RequestParam Long idPiece) {
        
        try {
            scanPiecesService.marquerPieceCommeFournie(id, idPiece);
            log.info("Pièce marquée comme fournie - Demande: {}, Pièce: {}", id, idPiece);
            
            return ResponseEntity.ok()
                    .body(Map.of(
                        "message", "Pièce marquée comme fournie",
                        "success", true
                    ));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Erreur lors du marquage de la pièce", e);
            return ResponseEntity.badRequest()
                    .body(Map.of(
                        "message", "Erreur: " + e.getMessage(),
                        "success", false
                    ));
        }
    }

    /**
     * POST /demandes/{id}/scan/valider
     * Valide que toutes les pièces obligatoires sont scannées et change le statut
     */
    @PostMapping("/{id}/scan/valider")
    @ResponseBody
    public ResponseEntity<?> validerScan(@PathVariable Long id) {
        
        try {
            scanPiecesService.validerScan(id);
            log.info("Validation du scan réussie - Demande: {}", id);
            return ResponseEntity.ok()
                    .body("{\"message\": \"Scan validé avec succès. Statut changé à SCANNEE\", \"success\": true, \"redirectUrl\": \"/demandes/" + id + "/details\"}");
        } catch (BusinessException e) {
            log.warn("Erreur lors de la validation du scan - Demande: {}", id, e);
            return ResponseEntity.badRequest()
                    .body("{\"message\": \"" + e.getMessage() + "\", \"success\": false}");
        } catch (ResourceNotFoundException e) {
            log.warn("Demande non trouvée - Demande: {}", id, e);
            return ResponseEntity.notFound().build();
        }
    }
}
