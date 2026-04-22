package com.demo.gestionVisa.repository;

import com.demo.gestionVisa.model.StatutPasseport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatutPasseportRepository extends JpaRepository<StatutPasseport, Long> {
}
