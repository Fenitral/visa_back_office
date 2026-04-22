package com.demo.gestionVisa.repository;

import com.demo.gestionVisa.model.Passeport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasseportRepository extends JpaRepository<Passeport, Long> {
    Passeport findByNumeroPasseport(String numeroPasseport);
}
