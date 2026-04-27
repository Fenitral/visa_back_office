package com.demo.gestionVisa.service;

import com.demo.gestionVisa.model.StatutDemande;
import com.demo.gestionVisa.repository.StatutDemandeRepository;
import org.springframework.stereotype.Service;

@Service
public class StatutDemandeService {

    private final StatutDemandeRepository statutDemandeRepository;

    public StatutDemandeService(StatutDemandeRepository statutDemandeRepository) {
        this.statutDemandeRepository = statutDemandeRepository;
    }
}
