package com.demo.gestionVisa.controller;

import com.demo.gestionVisa.dto.DeclarationPerteDTO;
import com.demo.gestionVisa.dto.DemandeResponseDTO;
import com.demo.gestionVisa.dto.DemandeurDTO;
import com.demo.gestionVisa.dto.DemandeurDetailsDTO;
import com.demo.gestionVisa.dto.SearchDemandeurDTO;
import com.demo.gestionVisa.exception.BusinessException;
import com.demo.gestionVisa.exception.ResourceNotFoundException;
import com.demo.gestionVisa.mapper.DemandeurMapper;
import com.demo.gestionVisa.model.Demande;
import com.demo.gestionVisa.model.Demandeur;
import com.demo.gestionVisa.repository.DemandeRepository;
import com.demo.gestionVisa.service.DeclarationPerteService;
import com.demo.gestionVisa.service.DemandeurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for Declaration page (Loss declarations - Perte de carte/passeport)
 */
@Controller
@RequestMapping("/declaration")
@RequiredArgsConstructor
public class DeclarationController {

    private final DemandeurService demandeurService;
    private final DeclarationPerteService declarationPerteService;
    private final DemandeurMapper demandeurMapper;
    private final DemandeRepository demandeRepository;

    /**
     * Display the unified Declaration of Loss form (Passeport + Carte Résident)
     */
    @GetMapping("/perte")
    public String perte() {
        return "declaration/perte";
    }

    /**
     * Legacy endpoint: Redirect "Perte de Carte Résident" to unified form
     */
    @GetMapping("/perte-carte")
    public String perteCarteResident() {
        return "redirect:/declaration/perte";
    }

    /**
     * Legacy endpoint: Redirect "Perte de Passeport" to unified form
     */
    @GetMapping("/perte-passport")
    public String pertePasseport() {
        return "redirect:/declaration/perte";
    }

    /**
     * API Endpoint: Rechercher les demandeurs par nom.
     * Retourne une liste de demandeurs avec infos sur leurs antécédents.
     * 
     * @param searchDTO DTO contenant le nom de recherche
     * @return liste des demandeurs trouvés avec leurs détails
     */
    @PostMapping("/api/search-demandeur")
    @ResponseBody
    public ResponseEntity<List<DemandeurDetailsDTO>> searchDemandeur(@RequestBody SearchDemandeurDTO searchDTO) {
        try {
            List<Demandeur> demandeurs = demandeurService.rechercherParNom(searchDTO.getNom());
            List<DemandeurDetailsDTO> resultat = demandeurs.stream()
                    .map(d -> {
                        DemandeurDTO dtoD = demandeurMapper.toDTO(d);
                        List<Demande> demandes = demandeRepository.findByDemandeurOrderByDateDemandeDesc(d);
                        DemandeurDetailsDTO details = new DemandeurDetailsDTO();
                        details.setDemandeur(dtoD);
                        details.setHasAntecedents(!demandes.isEmpty());
                        if (!demandes.isEmpty()) {
                            details.setLatestDemandId(demandes.get(0).getId());
                        }
                        return details;
                    })
                    .toList();
            return ResponseEntity.ok(resultat);
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }

    /**
     * API Endpoint: Déclarer une perte de passeport et/ou carte résident.
     * Crée une nouvelle demande de type DUPLICATA.
     * 
     * @param dto données de la déclaration de perte
     * @return la nouvelle demande créée
     */
    @PostMapping("/api/declarer-perte")
    @ResponseBody
    public ResponseEntity<?> declarerPerte(@RequestBody DeclarationPerteDTO dto) {
        try {
            DemandeResponseDTO resultat = declarationPerteService.declarerPerte(dto);
            return ResponseEntity.ok(resultat);
        } catch (BusinessException e) {
            // Erreur métier (données incohérentes, cas invalide)
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            errorResponse.put("type", "BUSINESS_ERROR");
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (ResourceNotFoundException e) {
            // Ressource non trouvée
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            errorResponse.put("type", "NOT_FOUND");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            // Erreur interne
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Erreur serveur: " + e.getMessage());
            errorResponse.put("type", "INTERNAL_ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}

