package com.demo.gestionVisa.service;

import com.demo.gestionVisa.model.Visa;
import com.demo.gestionVisa.repository.VisaRepository;
import org.springframework.stereotype.Service;

@Service
public class VisaService {

    private final VisaRepository visaRepository;

    public VisaService(VisaRepository visaRepository) {
        this.visaRepository = visaRepository;
    }
}
