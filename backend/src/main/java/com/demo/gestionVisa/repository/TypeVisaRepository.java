package com.demo.gestionVisa.repository;

import com.demo.gestionVisa.model.TypeVisa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeVisaRepository extends JpaRepository<TypeVisa, Long> {
}
