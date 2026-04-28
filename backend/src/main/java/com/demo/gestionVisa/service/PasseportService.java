package com.demo.gestionVisa.service;

import com.demo.gestionVisa.dto.PasseportDTO;
import com.demo.gestionVisa.exception.BusinessException;
import com.demo.gestionVisa.model.Demandeur;
import com.demo.gestionVisa.model.Passeport;
import com.demo.gestionVisa.repository.PasseportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PasseportService {

    private final PasseportRepository passeportRepository;

    public PasseportService(PasseportRepository passeportRepository) {
        this.passeportRepository = passeportRepository;
    }

    public List<Passeport> findAll() {
        return passeportRepository.findAll();
    }

    public Optional<Passeport> findById(Long id) {
        return passeportRepository.findById(id);
    }

    public Passeport save(Passeport passeport) {
        return passeportRepository.save(passeport);
    }

    public void deleteById(Long id) {
        passeportRepository.deleteById(id);
    }

    /**
     * Crée un nouveau passeport lié à un demandeur.
     * Le numéro est généré automatiquement avec le préfixe "PASS-" + l'ID.
     *
     * @param dto       données du formulaire
     * @param demandeur le demandeur propriétaire du passeport
     * @return          le passeport créé et persiste
     * @throws BusinessException si le numéro de passeport existe déjà
     */
    public Passeport creer(PasseportDTO dto, Demandeur demandeur) {
        Passeport passeport = new Passeport();
        passeport.setDateDelivrance(dto.getDateDelivrance());
        passeport.setDateExpiration(dto.getDateExpiration());
        passeport.setDemandeur(demandeur);

        // Sauvegarder d'abord pour obtenir l'ID généré
        passeport = passeportRepository.save(passeport);

        // Générer le numéro avec préfixe + ID
        String numeroPrefixé = "PASS-" + passeport.getId();
        
        // Vérifier l'unicité (normalement impossible, mais par sécurité)
        if (passeportRepository.findByNumero(numeroPrefixé).isPresent()) {
            throw new BusinessException("Le numéro de passeport '" + numeroPrefixé + "' est déjà utilisé.");
        }

        passeport.setNumero(numeroPrefixé);
        return passeportRepository.save(passeport);
    }

    public Passeport modifier(Passeport passeport, PasseportDTO dto) {
        passeport.setDateDelivrance(dto.getDateDelivrance());
        passeport.setDateExpiration(dto.getDateExpiration());
        return passeportRepository.save(passeport);
    }
}
