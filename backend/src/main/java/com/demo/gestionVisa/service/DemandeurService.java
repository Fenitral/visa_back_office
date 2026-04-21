package com.demo.gestionVisa.service;

import com.demo.gestionVisa.model.Demandeur;
import com.demo.gestionVisa.repository.DemandeurRepository;
import com.demo.gestionVisa.dto.DemandeurDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * Service pour la gestion des demandeurs
 */
@Service
public class DemandeurService {
    
    @Autowired
    private DemandeurRepository demandeurRepository;
    
    /**
     * Créer un nouveau demandeur
     */
    public Demandeur creerDemandeur(DemandeurDTO demandeurDTO) {
        Demandeur demandeur = new Demandeur();
        demandeur.setNom(demandeurDTO.getNom());
        demandeur.setPrenom(demandeurDTO.getPrenom());
        demandeur.setNomJeuneFille(demandeurDTO.getNomJeuneFille());
        demandeur.setDateNaissance(demandeurDTO.getDateNaissance());
        demandeur.setSituationFamiliale(demandeurDTO.getSituationFamiliale());
        demandeur.setNationalite(demandeurDTO.getNationalite());
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
            demandeur.setSituationFamiliale(demandeurDTO.getSituationFamiliale());
            demandeur.setNationalite(demandeurDTO.getNationalite());
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
}
