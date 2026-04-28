package com.demo.gestionVisa.service;

import com.demo.gestionVisa.dto.VisaTransformableDTO;
import com.demo.gestionVisa.exception.BusinessException;
import com.demo.gestionVisa.mapper.VisaTransformableMapper;
import com.demo.gestionVisa.model.Passeport;
import com.demo.gestionVisa.model.VisaTransformable;
import com.demo.gestionVisa.repository.VisaTransformableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VisaTransformableService {

    private final VisaTransformableRepository visaTransformableRepository;
    private final VisaTransformableMapper visaTransformableMapper;

    public VisaTransformableService(VisaTransformableRepository visaTransformableRepository,
                                   VisaTransformableMapper visaTransformableMapper) {
        this.visaTransformableRepository = visaTransformableRepository;
        this.visaTransformableMapper = visaTransformableMapper;
    }

    /**
     * Crée et persiste un nouveau VisaTransformable.
     * La référence visa est générée automatiquement avec le préfixe "VISTRANS-" + l'ID.
     *
     * Règles appliquées :
     *   RG-02 : dateExpiration > dateEntree
     *   RG-03 : passeport non null
     *
     * @param dto       données du formulaire
     * @param passeport entité Passeport résolue (non null)
     * @return          entité persistée
     * @throws BusinessException si dates incohérentes
     */
    public VisaTransformable creer(VisaTransformableDTO dto, Passeport passeport) {
        // Vérifier cohérence des dates
        if (!dto.getDateExpiration().isAfter(dto.getDateEntree())) {
            throw new BusinessException("La date d'expiration doit être postérieure à la date d'entrée.");
        }

        // Mapper et sauvegarder d'abord pour obtenir l'ID généré
        VisaTransformable entity = visaTransformableMapper.toEntity(dto, passeport);
        entity = visaTransformableRepository.save(entity);

        // Générer la référence avec préfixe + ID
        String referencePrefixée = "VISTRANS-" + entity.getId();
        entity.setReferenceVisa(referencePrefixée);
        
        return visaTransformableRepository.save(entity);
    }

    /**
     * Vérifie si une référence visa est déjà en base.
     *
     * @param referenceVisa     référence à tester
     * @return                  true si elle existe déjà
     */
    public boolean existeParReference(String referenceVisa) {
        return visaTransformableRepository.findByReferenceVisa(referenceVisa).isPresent();
    }

    public VisaTransformable modifier(VisaTransformable visaTransformable, VisaTransformableDTO dto) {
        if (!dto.getDateExpiration().isAfter(dto.getDateEntree())) {
            throw new BusinessException("La date d'expiration doit être postérieure à la date d'entrée.");
        }

        visaTransformable.setDateEntree(dto.getDateEntree());
        visaTransformable.setLieuEntree(dto.getLieuEntree());
        visaTransformable.setDateExpiration(dto.getDateExpiration());
        return visaTransformableRepository.save(visaTransformable);
    }
}
