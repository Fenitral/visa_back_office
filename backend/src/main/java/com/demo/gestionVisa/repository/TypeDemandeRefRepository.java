package com.demo.gestionVisa.repository;

import com.demo.gestionVisa.model.TypeDemandeRef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeDemandeRefRepository extends JpaRepository<TypeDemandeRef, Long> {
    TypeDemandeRef findByLibelle(String libelle);
}
