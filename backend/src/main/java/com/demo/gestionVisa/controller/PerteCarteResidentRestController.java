package com.demo.gestionVisa.controller;

import com.demo.gestionVisa.model.Demandeur;
import com.demo.gestionVisa.model.Nationalite;
import com.demo.gestionVisa.service.PerteCarteResidentService;
import com.demo.gestionVisa.repository.NationaliteRepository;
import com.demo.gestionVisa.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST API Controller for "Perte de Carte de Résident" (Loss of Resident Card) feature
 * 
 * Workflow:
 * 1. Search for existing demandeur by name
 * 2. If exists: Update card number + create new demand with type DUPLICATA (id=2)
 * 3. If not exists: Create new demandeur + create new demand with type DUPLICATA (id=2)
 */
@RestController
@RequestMapping("/api/perte-carte")
@RequiredArgsConstructor
public class PerteCarteResidentRestController {

    private final PerteCarteResidentService perteCarteResidentService;
    private final NationaliteRepository nationaliteRepository;

    /**
     * Search for existing demandeur by name
     * GET /api/perte-carte/search?nom=Dupont
     */
    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> searchDemandeur(
            @RequestParam String nom) {
        try {
            List<Map<String, Object>> results = perteCarteResidentService.searchDemandeur(nom);
            return ResponseEntity.ok(results);
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Process lost carte resident for EXISTING demandeur
     * - Updates the card number in carte_resident
     * - Creates new demand with type DUPLICATA (id=2)
     * 
     * POST /api/perte-carte/existing
     * {
     *   "demandeurId": 1,
     *   "newNumeroCarte": "CR123456789"
     * }
     */
    @PostMapping("/existing")
    public ResponseEntity<Map<String, Object>> reportLostExisting(
            @RequestBody Map<String, Object> request) {
        try {
            Long demandeurId = Long.parseLong(request.get("demandeurId").toString());
            String newNumeroCarte = request.get("newNumeroCarte").toString();
            
            Map<String, Object> result = perteCarteResidentService.reportLostCarteResidentExisting(
                    demandeurId, newNumeroCarte);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Process lost carte resident for EXISTING demandeur WITHOUT previous data
     * - Creates new passport with provided number
     * - Creates carte resident linked to passport
     * - Creates 2 new demands: first with type ID (id=1), then DUPLICATA (id=2)
     * 
     * POST /api/perte-carte/new
     * {
     *   "demandeurId": 1,
     *   "numeroCarte": "CR123456789",
     *   "numeroPassport": "ABC123456"
     * }
     */
    @PostMapping("/new")
    public ResponseEntity<Map<String, Object>> reportLostNew(
            @RequestBody Map<String, Object> request) {
        try {
            // Get demandeurId
            Object demandeurIdObj = request.get("demandeurId");
            if (demandeurIdObj == null) {
                return ResponseEntity.badRequest().body(
                        Map.of("success", false, "message", "demandeurId est requis"));
            }
            
            Long demandeurId;
            try {
                demandeurId = Long.parseLong(demandeurIdObj.toString());
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body(
                        Map.of("success", false, "message", "demandeurId invalide"));
            }
            
            String numeroCarte = (String) request.get("numeroCarte");
            String numeroPassport = (String) request.get("numeroPassport");
            
            if (numeroPassport == null || numeroPassport.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(
                        Map.of("success", false, "message", "Le numéro de passeport est requis"));
            }
            
            if (numeroCarte == null || numeroCarte.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(
                        Map.of("success", false, "message", "Le numéro de carte résident est requis"));
            }
            
            Map<String, Object> result = perteCarteResidentService.reportLostCarteResidentNew(
                    demandeurId, numeroCarte, numeroPassport);
            return ResponseEntity.ok(result);
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", "Erreur: " + e.getMessage()));
        }
    }
}
