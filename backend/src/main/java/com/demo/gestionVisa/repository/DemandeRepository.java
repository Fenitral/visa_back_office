package com.demo.gestionVisa.repository;

import com.demo.gestionVisa.model.Demande;
import com.demo.gestionVisa.model.Demandeur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DemandeRepository extends JpaRepository<Demande, Long> {
    List<Demande> findByDemandeur(Demandeur demandeur);
    List<Demande> findByDemandeurOrderByDateDemandeDesc(Demandeur demandeur);
}
