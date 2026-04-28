package com.demo.gestionVisa.repository;

import com.demo.gestionVisa.model.Passeport;
import com.demo.gestionVisa.model.Demandeur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface PasseportRepository extends JpaRepository<Passeport, Long> {
    Optional<Passeport> findByNumero(String numero);
    
    List<Passeport> findByDemandeur(Demandeur demandeur);
}
