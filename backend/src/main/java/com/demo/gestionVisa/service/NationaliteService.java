package com.demo.gestionVisa.service;

import com.demo.gestionVisa.model.Nationalite;
import com.demo.gestionVisa.repository.NationaliteRepository;
import org.springframework.stereotype.Service;

@Service
public class NationaliteService {

    private final NationaliteRepository nationaliteRepository;

    public NationaliteService(NationaliteRepository nationaliteRepository) {
        this.nationaliteRepository = nationaliteRepository;
    }
}
