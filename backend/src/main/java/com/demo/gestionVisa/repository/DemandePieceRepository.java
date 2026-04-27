package com.demo.gestionVisa.repository;

import com.demo.gestionVisa.model.DemandePiece;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DemandePieceRepository extends JpaRepository<DemandePiece, Long> {
}
