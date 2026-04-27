package com.demo.gestionVisa.service;

import com.demo.gestionVisa.model.SituationFamiliale;
import com.demo.gestionVisa.repository.SituationFamilialeRepository;
import org.springframework.stereotype.Service;

@Service
public class SituationFamilialeService {

    private final SituationFamilialeRepository situationFamilialeRepository;

    public SituationFamilialeService(SituationFamilialeRepository situationFamilialeRepository) {
        this.situationFamilialeRepository = situationFamilialeRepository;
    }
}
