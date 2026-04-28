package com.demo.gestionVisa.service;

import com.demo.gestionVisa.dto.DemandeurDTO;
import com.demo.gestionVisa.model.Demandeur;
import com.demo.gestionVisa.model.Nationalite;
import com.demo.gestionVisa.model.SituationFamiliale;
import com.demo.gestionVisa.repository.DemandeurRepository;
import com.demo.gestionVisa.repository.NationaliteRepository;
import com.demo.gestionVisa.repository.SituationFamilialeRepository;
import com.demo.gestionVisa.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DemandeurService {

    private final DemandeurRepository demandeurRepository;
    private final NationaliteRepository nationaliteRepository;
    private final SituationFamilialeRepository situationFamilialeRepository;

    public DemandeurService(DemandeurRepository demandeurRepository,
                           NationaliteRepository nationaliteRepository,
                           SituationFamilialeRepository situationFamilialeRepository) {
        this.demandeurRepository = demandeurRepository;
        this.nationaliteRepository = nationaliteRepository;
        this.situationFamilialeRepository = situationFamilialeRepository;
    }

    public List<Demandeur> findAll() {
        return demandeurRepository.findAll();
    }

    public Optional<Demandeur> findById(Long id) {
        return demandeurRepository.findById(id);
    }

    public Demandeur save(Demandeur demandeur) {
        return demandeurRepository.save(demandeur);
    }

    public void deleteById(Long id) {
        demandeurRepository.deleteById(id);
    }

    /**
     * Crée ou récupère un demandeur existant.
     *
     * Cherche d'abord un demandeur par nom et prénom, sinon crée un nouveau.
     *
     * @param dto les données du formulaire
     * @return le demandeur créé ou existant
     */
    public Demandeur creerOuRecuperer(DemandeurDTO dto) {
        // Chercher si un demandeur existe déjà avec le même nom et date de naissance
        Optional<Demandeur> existing = demandeurRepository.findByNomAndDateNaissance(dto.getNom(), dto.getDateNaissance());

        if (existing.isPresent()) {
            return existing.get();
        }

        // Créer un nouveau demandeur
        Demandeur demandeur = new Demandeur();
        demandeur.setNom(dto.getNom());
        demandeur.setPrenom(dto.getPrenom());
        demandeur.setNomJeuneFille(dto.getNomJeuneFille());
        demandeur.setDateNaissance(dto.getDateNaissance());
        demandeur.setLieuNaissance(dto.getLieuNaissance());
        demandeur.setAdresseMadagascar(dto.getAdresseMadagascar());
        demandeur.setTelephone(dto.getTelephone());
        demandeur.setEmail(dto.getEmail());

        // Résoudre SituationFamiliale
        if (dto.getIdSituationFamiliale() != null) {
            SituationFamiliale sf = situationFamilialeRepository.findById(dto.getIdSituationFamiliale())
                    .orElse(null);
            demandeur.setSituationFamiliale(sf);
        }

        // Résoudre Nationalité (obligatoire)
        Nationalite nationalite = nationaliteRepository.findById(dto.getIdNationalite())
                .orElseThrow(() -> new RuntimeException("Nationalité introuvable"));
        demandeur.setNationalite(nationalite);

        return demandeurRepository.save(demandeur);
    }

    public Demandeur modifier(Demandeur demandeur, DemandeurDTO dto) {
        demandeur.setNom(dto.getNom());
        demandeur.setPrenom(dto.getPrenom());
        demandeur.setNomJeuneFille(dto.getNomJeuneFille());
        demandeur.setDateNaissance(dto.getDateNaissance());
        demandeur.setLieuNaissance(dto.getLieuNaissance());
        demandeur.setAdresseMadagascar(dto.getAdresseMadagascar());
        demandeur.setTelephone(dto.getTelephone());
        demandeur.setEmail(dto.getEmail());

        if (dto.getIdSituationFamiliale() != null) {
            SituationFamiliale sf = situationFamilialeRepository.findById(dto.getIdSituationFamiliale())
                    .orElseThrow(() -> new ResourceNotFoundException("Situation familiale introuvable : id=" + dto.getIdSituationFamiliale()));
            demandeur.setSituationFamiliale(sf);
        } else {
            demandeur.setSituationFamiliale(null);
        }

        Nationalite nationalite = nationaliteRepository.findById(dto.getIdNationalite())
                .orElseThrow(() -> new ResourceNotFoundException("Nationalité introuvable : id=" + dto.getIdNationalite()));
        demandeur.setNationalite(nationalite);

        return demandeurRepository.save(demandeur);
    }

    /**
     * Recherche les demandeurs par nom (recherche insensible à la casse et partielle).
     *
     * @param nom le nom ou partie du nom à rechercher
     * @return liste des demandeurs correspondants
     */
    public List<Demandeur> rechercherParNom(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            return List.of();
        }
        return demandeurRepository.findByNomContainingIgnoreCase(nom.trim());
    }
}
