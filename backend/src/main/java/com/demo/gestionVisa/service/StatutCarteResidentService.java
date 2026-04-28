package com.demo.gestionVisa.service;

import com.demo.gestionVisa.dto.StatutCarteResidentDTO;
import com.demo.gestionVisa.model.StatutCarteResident;
import com.demo.gestionVisa.model.CarteResident;
import com.demo.gestionVisa.model.StatutCR;
import com.demo.gestionVisa.repository.StatutCarteResidentRepository;
import com.demo.gestionVisa.repository.StatutCRRepository;
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
    private final StatutCRRepository statutCRRepository;

    /**
     * Create a new status for a carte resident (e.g., mark it as lost)
     */
    public StatutCarteResidentDTO createStatut(CarteResident carteResident, Long statutCRId, LocalDate dateModif) {
        StatutCR statutCR = statutCRRepository.findById(statutCRId)
                .orElseThrow(() -> new BusinessException("StatutCR not found with id: " + statutCRId));

        StatutCarteResident statut = StatutCarteResident.builder()
                .carteResident(carteResident)
                .statutCR(statutCR)
                .dateModif(dateModif != null ? dateModif : LocalDate.now())
                .build();

        StatutCarteResident saved = statutCarteResidentRepository.save(statut);
        return mapToDTO(saved);
    }

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
                .statutCRId(statut.getStatutCR().getId())
                .statutCRLibelle(statut.getStatutCR().getLibelle())
                .dateModif(statut.getDateModif())
                .build();
    }
}
