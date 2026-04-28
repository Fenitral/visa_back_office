package com.demo.gestionVisa.repository;

import com.demo.gestionVisa.model.StatutCR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatutCRRepository extends JpaRepository<StatutCR, Long> {
    Optional<StatutCR> findByLibelle(String libelle);
}
