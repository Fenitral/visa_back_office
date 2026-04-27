package com.demo.gestionVisa.service;

import com.demo.gestionVisa.model.TypePiece;
import com.demo.gestionVisa.repository.TypePieceRepository;
import org.springframework.stereotype.Service;

@Service
public class TypePieceService {

    private final TypePieceRepository typePieceRepository;

    public TypePieceService(TypePieceRepository typePieceRepository) {
        this.typePieceRepository = typePieceRepository;
    }
}
