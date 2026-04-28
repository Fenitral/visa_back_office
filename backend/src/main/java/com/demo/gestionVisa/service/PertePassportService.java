package com.demo.gestionVisa.service;

import com.demo.gestionVisa.model.*;
import com.demo.gestionVisa.repository.*;
import com.demo.gestionVisa.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class PertePassportService {

    private final PasseportRepository passeportRepository;
    private final DemandeurRepository demandeurRepository;
    private final DemandeRepository demandeRepository;
    private final TypeDemandeRepository typeDemandeRepository;
    private final StatutDemandeRepository statutDemandeRepository;

    /**
     * Search for existing demandeur by passport number
     */
    public List<Map<String, Object>> searchDemandeur(String numeroPasseport) {
        Optional<Passeport> passeportOpt = passeportRepository.findByNumero(numeroPasseport);
        List<Map<String, Object>> results = new ArrayList<>();

        if (passeportOpt.isPresent()) {
            Passeport passeport = passeportOpt.get();
            Demandeur demandeur = passeport.getDemandeur();

            Map<String, Object> response = new HashMap<>();
            response.put("demandeurId", demandeur.getId());
            response.put("passeportId", passeport.getId());
            response.put("nom", demandeur.getNom());
            response.put("prenom", demandeur.getPrenom());
            response.put("dateNaissance", demandeur.getDateNaissance());
            response.put("numeroPasseport", passeport.getNumero());
            response.put("dateDelivrance", passeport.getDateDelivrance());
            response.put("dateExpiration", passeport.getDateExpiration());

            results.add(response);
        }

        return results;
    }

    /**
     * Process lost passport for existing demandeur
     * - Updates the passport number
     * - Creates a new demand of type DUPLICATA (id=2)
     */
    public Map<String, Object> reportLostPassportExisting(Long demandeurId, String newNumeroPasseport) {
        // Get demandeur
        Demandeur demandeur = demandeurRepository.findById(demandeurId)
                .orElseThrow(() -> new BusinessException("Demandeur not found with id: " + demandeurId));

        // Find and update the existing passport
        List<Passeport> passeports = passeportRepository.findAll();
        Passeport passeportAncien = null;

        for (Passeport p : passeports) {
            if (p.getDemandeur().getId().equals(demandeurId)) {
                passeportAncien = p;
                // Update the passport number
                p.setNumero(newNumeroPasseport);
                passeportRepository.save(p);
                break;
            }
        }

        if (passeportAncien == null) {
            throw new BusinessException("Passport not found for demandeur: " + demandeurId);
        }

        // Create new demand with type DUPLICATA (id=2)
        TypeDemande typeDuplicata = typeDemandeRepository.findById(2L)
                .orElseThrow(() -> new BusinessException("Type demande DUPLICATA not found"));

        // Get the initial status (CREE - id=1)
        StatutDemande statutCree = statutDemandeRepository.findById(1L)
                .orElseThrow(() -> new BusinessException("Statut CREE not found"));

        Demande newDemande = Demande.builder()
                .dateDemande(LocalDateTime.now())
                .demandeur(demandeur)
                .typeDemande(typeDuplicata)
                .statutDemande(statutCree)
                .build();

        Demande savedDemande = demandeRepository.save(newDemande);

        // Build response
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Passeport mis à jour et nouvelle demande créée (DUPLICATA)");
        response.put("demandeId", savedDemande.getId());
        response.put("typeDemande", "DUPLICATA");
        response.put("demandeur", demandeur.getNom() + " " + demandeur.getPrenom());
        response.put("nouveauNumeroPasseport", newNumeroPasseport);

        return response;
    }

    /**
     * Create new demandeur and new demands for lost passport
     * For NEW demandeur (without previous data):
     * - First create a demand with type ID (id=1)
     * - Then create a demand with type DUPLICATA (id=2)
     */
    public Map<String, Object> reportLostPassportNew(Demandeur newDemandeur, Passeport newPasseport) {
        // Save new demandeur
        Demandeur savedDemandeur = demandeurRepository.save(newDemandeur);

        // Link passport to demandeur and save
        newPasseport.setDemandeur(savedDemandeur);
        Passeport savedPasseport = passeportRepository.save(newPasseport);

        // Get the initial status (CREE - id=1)
        StatutDemande statutCree = statutDemandeRepository.findById(1L)
                .orElseThrow(() -> new BusinessException("Statut CREE not found"));

        // Step 1: Create first demand with type ID (id=1)
        TypeDemande typeId = typeDemandeRepository.findById(1L)
                .orElseThrow(() -> new BusinessException("Type demande ID not found"));

        Demande demandePremiere = Demande.builder()
                .dateDemande(LocalDateTime.now())
                .demandeur(savedDemandeur)
                .typeDemande(typeId)
                .statutDemande(statutCree)
                .build();

        Demande savedDemandePremiere = demandeRepository.save(demandePremiere);

        // Step 2: Create second demand with type DUPLICATA (id=2)
        TypeDemande typeDuplicata = typeDemandeRepository.findById(2L)
                .orElseThrow(() -> new BusinessException("Type demande DUPLICATA not found"));

        Demande demandeSeconde = Demande.builder()
                .dateDemande(LocalDateTime.now())
                .demandeur(savedDemandeur)
                .typeDemande(typeDuplicata)
                .statutDemande(statutCree)
                .build();

        Demande savedDemandeSeconde = demandeRepository.save(demandeSeconde);

        // Build response
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Nouveau demandeur créé avec 2 demandes enregistrées (ID + DUPLICATA)");
        response.put("demandeurId", savedDemandeur.getId());
        response.put("passeportId", savedPasseport.getId());
        response.put("demandeIdPrimaire", savedDemandePremiere.getId());
        response.put("demandeIdDuplicata", savedDemandeSeconde.getId());
        response.put("typeDemandePrimaire", "ID");
        response.put("typeDemandeDuplicata", "DUPLICATA");
        response.put("demandeur", savedDemandeur.getNom() + " " + savedDemandeur.getPrenom());
        response.put("numeroPasseport", savedPasseport.getNumero());

        return response;
    }
}
