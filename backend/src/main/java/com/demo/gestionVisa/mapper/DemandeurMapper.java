package com.demo.gestionVisa.mapper;

import com.demo.gestionVisa.dto.DemandeurDTO;
import com.demo.gestionVisa.model.Demandeur;
import org.springframework.stereotype.Component;

/**
 * Mapper pour convertir entre Demandeur (entité) et DemandeurDTO.
 */
@Component
public class DemandeurMapper {

    /**
     * Convertit une entité Demandeur en DTO.
     *
     * @param demandeur l'entité à convertir
     * @return le DTO correspondant
     */
    public DemandeurDTO toDTO(Demandeur demandeur) {
        if (demandeur == null) {
            return null;
        }

        return DemandeurDTO.builder()
                .id(demandeur.getId())
                .nom(demandeur.getNom())
                .prenom(demandeur.getPrenom())
                .nomJeuneFille(demandeur.getNomJeuneFille())
                .dateNaissance(demandeur.getDateNaissance())
                .lieuNaissance(demandeur.getLieuNaissance())
                .idSituationFamiliale(demandeur.getSituationFamiliale() != null ? demandeur.getSituationFamiliale().getId() : null)
                .idNationalite(demandeur.getNationalite() != null ? demandeur.getNationalite().getId() : null)
                .adresseMadagascar(demandeur.getAdresseMadagascar())
                .telephone(demandeur.getTelephone())
                .email(demandeur.getEmail())
                .build();
    }

    /**
     * Convertit un DTO en entité Demandeur.
     *
     * @param dto le DTO à convertir
     * @return l'entité correspondante
     */
    public Demandeur toEntity(DemandeurDTO dto) {
        if (dto == null) {
            return null;
        }

        Demandeur demandeur = new Demandeur();
        demandeur.setId(dto.getId());
        demandeur.setNom(dto.getNom());
        demandeur.setPrenom(dto.getPrenom());
        demandeur.setNomJeuneFille(dto.getNomJeuneFille());
        demandeur.setDateNaissance(dto.getDateNaissance());
        demandeur.setLieuNaissance(dto.getLieuNaissance());
        demandeur.setAdresseMadagascar(dto.getAdresseMadagascar());
        demandeur.setTelephone(dto.getTelephone());
        demandeur.setEmail(dto.getEmail());
        return demandeur;
    }
}
