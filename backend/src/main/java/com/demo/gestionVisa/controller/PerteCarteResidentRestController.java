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
     * Process lost carte resident for NEW demandeur
     * - Creates new demandeur
     * - Creates 2 new demands: first with type ID (id=1), then DUPLICATA (id=2)
     * 
     * POST /api/perte-carte/new
     * {
     *   "nom": "Dupont",
     *   "prenom": "Jean",
     *   "dateNaissance": "1990-01-15",
     *   "idNationalite": 1,
     *   "idSituationFamiliale": 1,
     *   "adresseMadagascar": "Antananarivo",
     *   "telephone": "0328888888",
     *   "numeroCarte": "CR123456789"
     * }
     */
    @PostMapping("/new")
    public ResponseEntity<Map<String, Object>> reportLostNew(
            @RequestBody Map<String, Object> request) {
        try {
            // Create Demandeur object from request
            Demandeur newDemandeur = new Demandeur();
            newDemandeur.setNom((String) request.get("nom"));
            newDemandeur.setPrenom((String) request.get("prenom"));
            newDemandeur.setAdresseMadagascar((String) request.get("adresseMadagascar"));
            newDemandeur.setTelephone((String) request.get("telephone"));
            
            // Parse date
            String dateNaissanceStr = (String) request.get("dateNaissance");
            if (dateNaissanceStr != null && !dateNaissanceStr.isEmpty()) {
                try {
                    java.time.LocalDate dateNaissance = java.time.LocalDate.parse(dateNaissanceStr);
                    newDemandeur.setDateNaissance(dateNaissance);
                } catch (Exception e) {
                    return ResponseEntity.badRequest().body(
                            Map.of("success", false, "message", "Format de date invalide. Utiliser YYYY-MM-DD"));
                }
            }
            
            // Parse nationalité ID and fetch Nationalite object
            Object idNationaliteObj = request.get("idNationalite");
            if (idNationaliteObj != null) {
                try {
                    Long idNationalite = Long.parseLong(idNationaliteObj.toString());
                    Nationalite nationalite = nationaliteRepository.findById(idNationalite)
                            .orElseThrow(() -> new BusinessException("Nationalité not found with id: " + idNationalite));
                    newDemandeur.setNationalite(nationalite);
                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest().body(
                            Map.of("success", false, "message", "ID Nationalité invalide"));
                }
            }
            
            String numeroCarte = (String) request.get("numeroCarte");
            
            Map<String, Object> result = perteCarteResidentService.reportLostCarteResidentNew(
                    newDemandeur, numeroCarte);
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
