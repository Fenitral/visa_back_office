package com.demo.gestionVisa.service;

import com.demo.gestionVisa.model.DemandePiece;
import com.demo.gestionVisa.repository.DemandePieceRepository;
import org.springframework.stereotype.Service;

@Service
public class DemandePieceService {

    private final DemandePieceRepository demandePieceRepository;

    public DemandePieceService(DemandePieceRepository demandePieceRepository) {
        this.demandePieceRepository = demandePieceRepository;
    }
}
