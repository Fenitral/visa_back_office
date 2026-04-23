package com.demo.gestionVisa.service;

import com.demo.gestionVisa.model.Demandeur;
import com.demo.gestionVisa.model.SituationFamilialeRef;
import com.demo.gestionVisa.model.Nationalite;
import com.demo.gestionVisa.repository.DemandeurRepository;
import com.demo.gestionVisa.repository.SituationFamilialeRefRepository;
import com.demo.gestionVisa.repository.NationaliteRepository;
import com.demo.gestionVisa.dto.DemandeurDTO;
import com.demo.gestionVisa.enums.SituationFamiliale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.Normalizer;
import java.util.List;
import java.util.Optional;

/**
 * Service pour la gestion des demandeurs
 */
@Service
public class DemandeurService {
    
    @Autowired
    private DemandeurRepository demandeurRepository;
    
    @Autowired
    private SituationFamilialeRefRepository situationFamilialeRefRepository;
    
    @Autowired
    private NationaliteRepository nationaliteRepository;
    
    /**
     * Créer un nouveau demandeur
     */
    public Demandeur creerDemandeur(DemandeurDTO demandeurDTO) {
        Demandeur demandeur = new Demandeur();
        demandeur.setNom(demandeurDTO.getNom());
        demandeur.setPrenom(demandeurDTO.getPrenom());
        demandeur.setNomJeuneFille(demandeurDTO.getNomJeuneFille());
        demandeur.setDateNaissance(demandeurDTO.getDateNaissance());

        demandeur.setSituationFamiliale(resolveSituationFamiliale(demandeurDTO.getSituationFamiliale()));
        demandeur.setNationalite(resolveOrCreateNationalite(demandeurDTO.getNationalite()));
        
        demandeur.setProfession(demandeurDTO.getProfession());
        demandeur.setAdresseMadagascar(demandeurDTO.getAdresseMadagascar());
        demandeur.setTelephone(demandeurDTO.getTelephone());
        
        return demandeurRepository.save(demandeur);
    }
    
    /**
     * Récupérer un demandeur par ID
     */
    public Optional<Demandeur> getDemandeurById(Long id) {
        return demandeurRepository.findById(id);
    }
    
    /**
     * Récupérer ou créer un demandeur
     */
    public Demandeur getOuCreerDemandeur(DemandeurDTO demandeurDTO) {
        Optional<Demandeur> demandeurExistant = 
            demandeurRepository.findByNomAndPrenom(demandeurDTO.getNom(), demandeurDTO.getPrenom());
        
        if (demandeurExistant.isPresent()) {
            return demandeurExistant.get();
        }
        
        return creerDemandeur(demandeurDTO);
    }
    
    /**
     * Mettre à jour un demandeur
     */
    public Demandeur updateDemandeur(Long id, DemandeurDTO demandeurDTO) {
        Optional<Demandeur> demandeurOptional = demandeurRepository.findById(id);
        
        if (demandeurOptional.isPresent()) {
            Demandeur demandeur = demandeurOptional.get();
            demandeur.setNom(demandeurDTO.getNom());
            demandeur.setPrenom(demandeurDTO.getPrenom());
            demandeur.setNomJeuneFille(demandeurDTO.getNomJeuneFille());
            demandeur.setDateNaissance(demandeurDTO.getDateNaissance());

            demandeur.setSituationFamiliale(resolveSituationFamiliale(demandeurDTO.getSituationFamiliale()));
            demandeur.setNationalite(resolveOrCreateNationalite(demandeurDTO.getNationalite()));
            
            demandeur.setProfession(demandeurDTO.getProfession());
            demandeur.setAdresseMadagascar(demandeurDTO.getAdresseMadagascar());
            demandeur.setTelephone(demandeurDTO.getTelephone());
            
            return demandeurRepository.save(demandeur);
        }
        
        return null;
    }
    
    /**
     * Récupérer tous les demandeurs
     */
    public List<Demandeur> getAllDemandeurs() {
        return demandeurRepository.findAll();
    }
    
    /**
     * Supprimer un demandeur
     */
    public void deleteDemandeur(Long id) {
        demandeurRepository.deleteById(id);
    }

    private SituationFamilialeRef resolveSituationFamiliale(SituationFamiliale situation) {
        if (situation == null) {
            throw new IllegalArgumentException("La situation familiale est obligatoire");
        }

        String expectedLabel = situation.getLabel();
        SituationFamilialeRef exact = situationFamilialeRefRepository.findByLibelle(expectedLabel);
        if (exact != null) {
            return exact;
        }

        String normalizedExpected = normalize(expectedLabel);
        for (SituationFamilialeRef ref : situationFamilialeRefRepository.findAll()) {
            if (normalizedExpected.equals(normalize(ref.getLibelle()))) {
                return ref;
            }
        }

        SituationFamilialeRef created = new SituationFamilialeRef();
        created.setLibelle(expectedLabel);
        return situationFamilialeRefRepository.save(created);
    }

    private Nationalite resolveOrCreateNationalite(String nationaliteInput) {
        if (nationaliteInput == null || nationaliteInput.trim().isEmpty()) {
            throw new IllegalArgumentException("La nationalite est obligatoire");
        }

        String trimmed = nationaliteInput.trim();
        Nationalite exact = nationaliteRepository.findByLibelle(trimmed);
        if (exact != null) {
            return exact;
        }

        String normalizedExpected = normalize(trimmed);
        for (Nationalite nationalite : nationaliteRepository.findAll()) {
            if (normalizedExpected.equals(normalize(nationalite.getLibelle()))) {
                return nationalite;
            }
        }

        Nationalite created = new Nationalite();
        created.setLibelle(trimmed);
        return nationaliteRepository.save(created);
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "")
            .toLowerCase()
            .trim();
        return normalized;
    }
}
