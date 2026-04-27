package com.demo.gestionVisa.service;

import com.demo.gestionVisa.model.TypeVisa;
import com.demo.gestionVisa.repository.TypeVisaRepository;
import org.springframework.stereotype.Service;

@Service
public class TypeVisaService {

    private final TypeVisaRepository typeVisaRepository;

    public TypeVisaService(TypeVisaRepository typeVisaRepository) {
        this.typeVisaRepository = typeVisaRepository;
    }
}
