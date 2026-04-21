package com.demo.gestionVisa.service;

import com.demo.gestionVisa.model.VisaTransformable;
import com.demo.gestionVisa.model.Demandeur;
import com.demo.gestionVisa.repository.VisaTransformableRepository;
import com.demo.gestionVisa.dto.VisaTransformableDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * Service pour la gestion des visas transformables
 */
@Service
public class VisaTransformableService {
    
    @Autowired
    private VisaTransformableRepository visaTransformableRepository;
    
    @Autowired
    private DemandeurService demandeurService;
    
    /**
     * Créer un nouveau visa transformable
     */
    public VisaTransformable creerVisaTransformable(VisaTransformableDTO visaDTO) {
        Optional<Demandeur> demandeur = demandeurService.getDemandeurById(visaDTO.getDemandeurId());
        
        if (demandeur.isEmpty()) {
            throw new RuntimeException("Demandeur non trouvé avec l'ID: " + visaDTO.getDemandeurId());
        }
        
        VisaTransformable visa = new VisaTransformable();
        visa.setReference(visaDTO.getReference());
        visa.setDemandeur(demandeur.get());
        visa.setDateEntree(visaDTO.getDateEntree());
        visa.setLieuEntree(visaDTO.getLieuEntree());
        visa.setDateExpiration(visaDTO.getDateExpiration());
        
        return visaTransformableRepository.save(visa);
    }
    
    /**
     * Récupérer un visa par sa référence
     */
    public Optional<VisaTransformable> getVisaByReference(String reference) {
        return visaTransformableRepository.findByReference(reference);
    }
    
    /**
     * Récupérer un visa par ID
     */
    public Optional<VisaTransformable> getVisaById(Long id) {
        return visaTransformableRepository.findById(id);
    }
    
    /**
     * Récupérer tous les visas d'un demandeur
     */
    public List<VisaTransformable> getVisasByDemandeur(Demandeur demandeur) {
        return visaTransformableRepository.findByDemandeur(demandeur);
    }
    
    /**
     * Mettre à jour un visa transformable
     */
    public VisaTransformable updateVisaTransformable(Long id, VisaTransformableDTO visaDTO) {
        Optional<VisaTransformable> visaOptional = visaTransformableRepository.findById(id);
        
        if (visaOptional.isPresent()) {
            VisaTransformable visa = visaOptional.get();
            visa.setReference(visaDTO.getReference());
            visa.setDateEntree(visaDTO.getDateEntree());
            visa.setLieuEntree(visaDTO.getLieuEntree());
            visa.setDateExpiration(visaDTO.getDateExpiration());
            
            return visaTransformableRepository.save(visa);
        }
        
        return null;
    }
    
    /**
     * Récupérer tous les visas transformables
     */
    public List<VisaTransformable> getAllVisas() {
        return visaTransformableRepository.findAll();
    }
    
    /**
     * Supprimer un visa
     */
    public void deleteVisa(Long id) {
        visaTransformableRepository.deleteById(id);
    }
}
