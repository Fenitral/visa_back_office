package com.demo.gestionVisa.repository;

import com.demo.gestionVisa.model.StatutCarteResident;
import com.demo.gestionVisa.model.CarteResident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StatutCarteResidentRepository extends JpaRepository<StatutCarteResident, Long> {
    List<StatutCarteResident> findByCarteResident(CarteResident carteResident);
    
    @Query("SELECT s FROM StatutCarteResident s WHERE s.carteResident.id = :carteResidentId ORDER BY s.dateModif DESC")
    List<StatutCarteResident> findByCarteResidentIdOrderByDateModif(Long carteResidentId);
    
    Optional<StatutCarteResident> findTopByCarteResidentIdOrderByDateModifDesc(Long carteResidentId);
}
