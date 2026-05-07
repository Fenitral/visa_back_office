package com.demo.gestionVisa.controller;

import com.demo.gestionVisa.dto.DemandeResponseDTO;
import com.demo.gestionVisa.service.DemandeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API Controller pour les demandes de visa
 * Endpoints: GET /api/demandes, GET /api/demandes/{id}
 */
@RestController
@RequestMapping("/api/demandes")
@CrossOrigin(origins = "*", maxAge = 3600)  // Permet les requêtes Vue.js depuis n'importe quelle origine
public class DemandeRestController {

    private final DemandeService demandeService;

    public DemandeRestController(DemandeService demandeService) {
        this.demandeService = demandeService;
    }

    /**
     * GET /api/demandes
     * Récupère la liste de toutes les demandes
     * @return liste de DemandeResponseDTO
     */
    @GetMapping
    public ResponseEntity<List<DemandeResponseDTO>> listerDemandes() {
        List<DemandeResponseDTO> demandes = demandeService.getToutesDemandes();
        return ResponseEntity.ok(demandes);
    }

    /**
     * GET /api/demandes/{id}
     * Récupère les détails d'une demande avec son historique de statuts
     * @param id identifiant de la demande
     * @return DemandeResponseDTO avec historiques
     */
    @GetMapping("/{id}")
    public ResponseEntity<DemandeResponseDTO> obtenirDemande(@PathVariable Long id) {
        DemandeResponseDTO demande = demandeService.getDemande(id);
        return ResponseEntity.ok(demande);
    }
}
