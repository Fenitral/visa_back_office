package com.demo.gestionVisa.service;

import com.demo.gestionVisa.dto.StatutCarteResidentDTO;
import com.demo.gestionVisa.model.StatutCarteResident;
import com.demo.gestionVisa.model.CarteResident;
import com.demo.gestionVisa.repository.StatutCarteResidentRepository;
import com.demo.gestionVisa.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StatutCarteResidentService {

    private final StatutCarteResidentRepository statutCarteResidentRepository;

  

    /**
     * Get the latest status for a carte resident
     */
    public Optional<StatutCarteResidentDTO> getLatestStatut(Long carteResidentId) {
        return statutCarteResidentRepository.findTopByCarteResidentIdOrderByDateModifDesc(carteResidentId)
                .map(this::mapToDTO);
    }

    /**
     * Get all statuses for a carte resident
     */
    public List<StatutCarteResidentDTO> getStatuts(Long carteResidentId) {
        return statutCarteResidentRepository.findByCarteResidentIdOrderByDateModif(carteResidentId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert entity to DTO
     */
    private StatutCarteResidentDTO mapToDTO(StatutCarteResident statut) {
        return StatutCarteResidentDTO.builder()
                .id(statut.getId())
                .carteResidentId(statut.getCarteResident().getId())
                .carteResidentNumero(statut.getCarteResident().getNumeroCarte())
                .dateModif(statut.getDateModif())
                .build();
    }
}
