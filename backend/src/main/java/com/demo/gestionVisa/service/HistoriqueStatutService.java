package com.demo.gestionVisa.service;

import com.demo.gestionVisa.model.HistoriqueStatut;
import com.demo.gestionVisa.repository.HistoriqueStatutRepository;
import org.springframework.stereotype.Service;

@Service
public class HistoriqueStatutService {

    private final HistoriqueStatutRepository historiqueStatutRepository;

    public HistoriqueStatutService(HistoriqueStatutRepository historiqueStatutRepository) {
        this.historiqueStatutRepository = historiqueStatutRepository;
    }
}
