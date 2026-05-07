package com.demo.gestionVisa.controller;

import com.demo.gestionVisa.dto.DemandeResponseDTO;
import com.demo.gestionVisa.service.DemandeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST API Controller pour les demandes de visa
 * Endpoints: GET /api/demandes, GET /api/demandes/{id}, GET /api/demandes/search/*
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
     * GET /api/demandes/search/id/{id}
     * Récupère une demande par ID avec toutes les demandes liées du même demandeur
     * Retourne: { "principale": {...}, "liees": [{...}, {...}] }
     * @param id identifiant de la demande
     * @return Map contenant la demande principale et les demandes liées
     */
    @GetMapping("/search/id/{id}")
    public ResponseEntity<Map<String, Object>> rechercherParId(@PathVariable Long id) {
        Map<String, Object> result = demandeService.getDemandeWithRelated(id);
        return ResponseEntity.ok(result);
    }

    /**
     * GET /api/demandes/search/passport/{numeroPasSeport}
     * Récupère toutes les demandes associées à un numéro de passeport
     * @param numeroPasSeport numéro du passeport
     * @return liste de DemandeResponseDTO
     */
    @GetMapping("/search/passport/{numeroPasSeport}")
    public ResponseEntity<List<DemandeResponseDTO>> rechercherParPasseport(@PathVariable String numeroPasSeport) {
        List<DemandeResponseDTO> demandes = demandeService.getDemandesByPasseport(numeroPasSeport);
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
