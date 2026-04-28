package com.demo.gestionVisa.controller;

import com.demo.gestionVisa.model.Demandeur;
import com.demo.gestionVisa.model.Passeport;
import com.demo.gestionVisa.service.PertePassportService;
import com.demo.gestionVisa.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST API Controller for "Perte de Passeport" (Loss of Passport) feature
 * 
 * Workflow:
 * 1. Search for existing demandeur by passport number
 * 2. If exists: Update passport number + create new demand with type DUPLICATA (id=2)
 * 3. If not exists: Create new demandeur + create 2 new demands (type ID=1, then DUPLICATA=2)
 */
@RestController
@RequestMapping("/api/perte-passport")
@RequiredArgsConstructor
public class PertePassportRestController {

    private final PertePassportService pertePassportService;

    /**
     * Search for existing demandeur by passport number
     * GET /api/perte-passport/search?numero=ABC123456
     */
    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> searchDemandeur(
            @RequestParam String numero) {
        try {
            List<Map<String, Object>> results = pertePassportService.searchDemandeur(numero);
            return ResponseEntity.ok(results);
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Process lost passport for EXISTING demandeur
     * - Updates the passport number
     * - Creates new demand with type DUPLICATA (id=2)
     * 
     * POST /api/perte-passport/existing
     * {
     *   "demandeurId": 1,
     *   "newNumeroPasseport": "XYZ789012"
     * }
     */
    @PostMapping("/existing")
    public ResponseEntity<Map<String, Object>> reportLostExisting(
            @RequestBody Map<String, Object> request) {
        try {
            Long demandeurId = Long.parseLong(request.get("demandeurId").toString());
            String newNumeroPasseport = request.get("newNumeroPasseport").toString();
            
            Map<String, Object> result = pertePassportService.reportLostPassportExisting(
                    demandeurId, newNumeroPasseport);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Process lost passport for NEW demandeur
     * - Creates new demandeur
     * - Creates 2 new demands: first with type ID (id=1), then DUPLICATA (id=2)
     * 
     * POST /api/perte-passport/new
     * {
     *   "demandeur": {
     *     "nom": "Dupont",
     *     "prenom": "Jean",
     *     "dateNaissance": "1990-01-15",
     *     "idNationalite": 1,
     *     "idSituationFamiliale": 1,
     *     "adresseMadagascar": "Antananarivo",
     *     "telephone": "0328888888"
     *   },
     *   "passeport": {
     *     "numero": "ABC123456",
     *     "dateDelivrance": "2020-03-10",
     *     "dateExpiration": "2030-03-10"
     *   }
     * }
     */
    @PostMapping("/new")
    public ResponseEntity<Map<String, Object>> reportLostNew(
            @RequestBody Map<String, Object> request) {
        try {
            // Parse demandeur from request
            Map<String, Object> demandeurData = (Map<String, Object>) request.get("demandeur");
            Demandeur newDemandeur = new Demandeur();
            newDemandeur.setNom((String) demandeurData.get("nom"));
            newDemandeur.setPrenom((String) demandeurData.get("prenom"));
            
            // Parse passeport from request
            Map<String, Object> passeportData = (Map<String, Object>) request.get("passeport");
            Passeport newPasseport = new Passeport();
            newPasseport.setNumero((String) passeportData.get("numero"));
            
            Map<String, Object> result = pertePassportService.reportLostPassportNew(
                    newDemandeur, newPasseport);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
