package com.demo.gestionVisa.repository;

import com.demo.gestionVisa.model.Demande;
import com.demo.gestionVisa.model.Visa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisaRepository extends JpaRepository<Visa, Long> {
    List<Visa> findByDemande(Demande demande);
}
