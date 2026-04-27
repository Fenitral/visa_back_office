package com.demo.gestionVisa.repository;

import com.demo.gestionVisa.model.VisaTransformable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VisaTransformableRepository extends JpaRepository<VisaTransformable, Long> {
    Optional<VisaTransformable> findByReferenceVisa(String referenceVisa);
}
