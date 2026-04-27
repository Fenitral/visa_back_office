package com.demo.gestionVisa.service;

import com.demo.gestionVisa.model.TypeDemande;
import com.demo.gestionVisa.repository.TypeDemandeRepository;
import org.springframework.stereotype.Service;

@Service
public class TypeDemandeService {

    private final TypeDemandeRepository typeDemandeRepository;

    public TypeDemandeService(TypeDemandeRepository typeDemandeRepository) {
        this.typeDemandeRepository = typeDemandeRepository;
    }
}
