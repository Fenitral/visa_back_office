package com.demo.gestionVisa.repository;

import com.demo.gestionVisa.model.Demandeur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

@Repository
public interface DemandeurRepository extends JpaRepository<Demandeur, Long> {
    Optional<Demandeur> findByNomAndDateNaissance(String nom, LocalDate dateNaissance);
    List<Demandeur> findByNomContainingIgnoreCase(String nom);

    @Query("select distinct d from Demandeur d left join d.passeports p " +
           "where lower(d.nom) like lower(concat('%', :term, '%')) " +
           "or lower(d.prenom) like lower(concat('%', :term, '%')) " +
           "or lower(d.nomJeuneFille) like lower(concat('%', :term, '%')) " +
           "or lower(d.email) like lower(concat('%', :term, '%')) " +
           "or lower(d.telephone) like lower(concat('%', :term, '%')) " +
           "or lower(p.numero) like lower(concat('%', :term, '%'))")
    List<Demandeur> searchByTerm(@Param("term") String term);
}
