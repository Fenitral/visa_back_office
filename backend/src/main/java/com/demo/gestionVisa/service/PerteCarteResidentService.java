package com.demo.gestionVisa.service;

import com.demo.gestionVisa.model.*;
import com.demo.gestionVisa.repository.*;
import com.demo.gestionVisa.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalDate;
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
    private final PasseportRepository passeportRepository;

    /**
     * Search for existing demandeur by name
     * Include information about whether they have existing carte resident
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
            boolean hasCarteResident = false;
            
            if (!demandes.isEmpty()) {
                // Get the most recent carte resident
                CarteResident carteActuelle = null;
                for (Demande demande : demandes) {
                    Set<CarteResident> cartes = demande.getCartesResidents();
                    if (!cartes.isEmpty()) {
                        carteActuelle = cartes.iterator().next();
                        hasCarteResident = true;
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
            
            response.put("hasCarteResident", hasCarteResident);
            results.add(response);
        }
        return results;
    }

    /**
     * Process lost carte resident for existing demandeur
     * - Use the existing passport of the demandeur
     * - Update the existing carte resident number
     * - Create a new demand of type DUPLICATA (id=2)
     */
    public Map<String, Object> reportLostCarteResidentExisting(Long demandeurId, String newNumeroCarte) {
        // Get demandeur
        Demandeur demandeur = demandeurRepository.findById(demandeurId)
                .orElseThrow(() -> new BusinessException("Demandeur not found with id: " + demandeurId));

        // Get the passport of the demandeur
        // The demandeur must have at least one passport
        Set<Passeport> passeports = demandeur.getPasseports();
        if (passeports == null || passeports.isEmpty()) {
            throw new BusinessException("Aucun passeport trouvé pour le demandeur");
        }
        
        Passeport passeportActuel = passeports.iterator().next();

        // Find and update the existing carte resident
        List<Demande> demandes = demandeRepository.findByDemandeur(demandeur);
        CarteResident carteAncienne = null;

        for (Demande demande : demandes) {
            Set<CarteResident> cartes = demande.getCartesResidents();
            if (!cartes.isEmpty()) {
                carteAncienne = cartes.iterator().next();
                // Update the card number
                carteAncienne.setNumeroCarte(newNumeroCarte);
                // Ensure the passport is linked
                carteAncienne.setPasseport(passeportActuel);
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
        response.put("passeportUtilise", passeportActuel.getNumero());

        return response;
    }

    /**
     * Create new demands for existing demandeur (when no previous data exists)
     * For EXISTING demandeur without previous carte resident data:
     * - Create a new Passeport with the provided passport number
     * - Create a CartResident linked to this passport
     * - First create a demand with type ID (id=1)
     * - Then create a demand with type DUPLICATA (id=2)
     */
    public Map<String, Object> reportLostCarteResidentNew(Long demandeurId, String numeroCarte, String numeroPassport) {
        // Get existing demandeur
        Demandeur demandeur = demandeurRepository.findById(demandeurId)
                .orElseThrow(() -> new BusinessException("Demandeur not found with id: " + demandeurId));

        // Get the initial status (CREE - id=1)
        StatutDemande statutCree = statutDemandeRepository.findById(1L)
                .orElseThrow(() -> new BusinessException("Statut CREE not found"));

        // Step 1: Create a new Passeport if passport number is provided
        Passeport newPasseport = null;
        if (numeroPassport != null && !numeroPassport.trim().isEmpty()) {
            // Check if passport already exists
            Optional<Passeport> existingPasseport = passeportRepository.findByNumero(numeroPassport);
            if (existingPasseport.isPresent()) {
                throw new BusinessException("Numéro de passeport déjà enregistré: " + numeroPassport);
            }
            
            newPasseport = Passeport.builder()
                    .numero(numeroPassport)
                    .demandeur(demandeur)
                    .dateDelivrance(LocalDate.now())
                    // dateExpiration can be set to null or to a future date if needed
                    .build();
            
            newPasseport = passeportRepository.save(newPasseport);
        }

        // Step 2: Create CarteResident linked to the passport
        CarteResident newCarteResident = CarteResident.builder()
                .numeroCarte(numeroCarte)
                .dateDebut(LocalDate.now())
                .dateFin(LocalDate.now().plusYears(10))  // Default 10 years validity
                .passeport(newPasseport)  // Link to the created passport
                .build();
        
        CarteResident savedCarteResident = carteResidentRepository.save(newCarteResident);

        // Step 3: Create first demand with type ID (id=1)
        TypeDemande typeId = typeDemandeRepository.findById(1L)
                .orElseThrow(() -> new BusinessException("Type demande ID not found"));

        Demande demandePremiere = Demande.builder()
                .dateDemande(LocalDateTime.now())
                .demandeur(demandeur)
                .typeDemande(typeId)
                .statutDemande(statutCree)
                .build();

        Demande savedDemandePremiere = demandeRepository.save(demandePremiere);
        
        // Link the carte resident to the first demand
        newCarteResident.setDemande(savedDemandePremiere);
        carteResidentRepository.save(newCarteResident);

        // Step 4: Create second demand with type DUPLICATA (id=2)
        TypeDemande typeDuplicata = typeDemandeRepository.findById(2L)
                .orElseThrow(() -> new BusinessException("Type demande DUPLICATA not found"));

        Demande demandeSeconde = Demande.builder()
                .dateDemande(LocalDateTime.now())
                .demandeur(demandeur)
                .typeDemande(typeDuplicata)
                .statutDemande(statutCree)
                .build();

        Demande savedDemandeSeconde = demandeRepository.save(demandeSeconde);

        // Build response
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Nouvelles données enregistrées avec passeport et 2 demandes créées (ID + DUPLICATA)");
        response.put("demandeurId", demandeur.getId());
        response.put("passeportId", newPasseport != null ? newPasseport.getId() : null);
        response.put("carteResidentId", savedCarteResident.getId());
        response.put("demandeIdPrimaire", savedDemandePremiere.getId());
        response.put("demandeIdDuplicata", savedDemandeSeconde.getId());
        response.put("typeDemandePrimaire", "ID");
        response.put("typeDemandeDuplicata", "DUPLICATA");
        response.put("demandeur", demandeur.getNom() + " " + demandeur.getPrenom());
        response.put("numeroCarte", numeroCarte);
        response.put("numeroPassport", numeroPassport);

        return response;
    }
}
