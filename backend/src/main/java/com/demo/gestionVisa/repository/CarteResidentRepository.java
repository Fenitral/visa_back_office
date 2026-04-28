package com.demo.gestionVisa.repository;

import com.demo.gestionVisa.model.CarteResident;
import com.demo.gestionVisa.model.Demande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarteResidentRepository extends JpaRepository<CarteResident, Long> {
    List<CarteResident> findByDemande(Demande demande);
}
