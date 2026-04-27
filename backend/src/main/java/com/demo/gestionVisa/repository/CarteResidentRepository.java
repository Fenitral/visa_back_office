package com.demo.gestionVisa.repository;

import com.demo.gestionVisa.model.CarteResident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarteResidentRepository extends JpaRepository<CarteResident, Long> {
}
