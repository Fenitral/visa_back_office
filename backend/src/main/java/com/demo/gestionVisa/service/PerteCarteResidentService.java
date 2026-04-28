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
public class PerteCarteResidentService {

    private final CarteResidentRepository carteResidentRepository;
    private final DemandeurRepository demandeurRepository;
    private final DemandeRepository demandeRepository;
    private final TypeDemandeRepository typeDemandeRepository;
    private final StatutDemandeRepository statutDemandeRepository;

    /**
     * Search for existing demandeur by name
     */
    public List<Map<String, Object>> searchDemandeur(String nom) {
        List<Demandeur> demandeurs = demandeurRepository.findByNomContainingIgnoreCase(nom);
        List<Map<String, Object>> results = new ArrayList<>();

        for (Demandeur demandeur : demandeurs) {
            Map<String, Object> response = new HashMap<>();
            response.put("demandeurId", demandeur.getId());
            response.put("nom", demandeur.getNom());
            response.put("prenom", demandeur.getPrenom());
            response.put("dateNaissance", demandeur.getDateNaissance());

            // Get the current carte resident if exists
            List<Demande> demandes = demandeRepository.findByDemandeur(demandeur);
            if (!demandes.isEmpty()) {
                // Get the most recent carte resident
                CarteResident carteActuelle = null;
                for (Demande demande : demandes) {
                    Set<CarteResident> cartes = demande.getCartesResidents();
                    if (!cartes.isEmpty()) {
                        carteActuelle = cartes.iterator().next();
                        break;
                    }
                }
                if (carteActuelle != null) {
                    response.put("carteResidentId", carteActuelle.getId());
                    response.put("numeroCarte", carteActuelle.getNumeroCarte());
                    response.put("dateDebut", carteActuelle.getDateDebut());
                    response.put("dateFin", carteActuelle.getDateFin());
                }
            }
            results.add(response);
        }
        return results;
    }

    /**
     * Process lost carte resident for existing demandeur
     * - Update the carte resident number
     * - Create a new demand of type DUPLICATA (id=2)
     */
    public Map<String, Object> reportLostCarteResidentExisting(Long demandeurId, String newNumeroCarte) {
        // Get demandeur
        Demandeur demandeur = demandeurRepository.findById(demandeurId)
                .orElseThrow(() -> new BusinessException("Demandeur not found with id: " + demandeurId));

        // Find and update the existing carte resident
        List<Demande> demandes = demandeRepository.findByDemandeur(demandeur);
        CarteResident carteAncienne = null;

        for (Demande demande : demandes) {
            Set<CarteResident> cartes = demande.getCartesResidents();
            if (!cartes.isEmpty()) {
                carteAncienne = cartes.iterator().next();
                // Update the card number
                carteAncienne.setNumeroCarte(newNumeroCarte);
                carteResidentRepository.save(carteAncienne);
                break;
            }
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
        response.put("message", "Carte de résident mise à jour et nouvelle demande créée (DUPLICATA)");
        response.put("demandeId", savedDemande.getId());
        response.put("typeDemande", "DUPLICATA");
        response.put("demandeur", demandeur.getNom() + " " + demandeur.getPrenom());
        response.put("nouveauNumeroCarte", newNumeroCarte);

        return response;
    }

    /**
     * Create new demandeur and new demands for lost carte resident
     * For NEW demandeur (without previous data):
     * - First create a demand with type ID (id=1)
     * - Then create a demand with type DUPLICATA (id=2)
     */
    public Map<String, Object> reportLostCarteResidentNew(Demandeur newDemandeur, String numeroCarte) {
        // Save new demandeur
        Demandeur savedDemandeur = demandeurRepository.save(newDemandeur);

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
        response.put("demandeIdPrimaire", savedDemandePremiere.getId());
        response.put("demandeIdDuplicata", savedDemandeSeconde.getId());
        response.put("typeDemandePrimaire", "ID");
        response.put("typeDemandeDuplicata", "DUPLICATA");
        response.put("demandeur", savedDemandeur.getNom() + " " + savedDemandeur.getPrenom());
        response.put("numeroCarte", numeroCarte);

        return response;
    }
}
