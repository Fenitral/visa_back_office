package com.demo.gestionVisa.service;

import com.demo.gestionVisa.model.CarteResident;
import com.demo.gestionVisa.model.Demande;
import com.demo.gestionVisa.model.Passeport;
import com.demo.gestionVisa.model.VisaTransformable;
import com.demo.gestionVisa.repository.CarteResidentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CarteResidentService {

    private final CarteResidentRepository carteResidentRepository;

    public CarteResidentService(CarteResidentRepository carteResidentRepository) {
        this.carteResidentRepository = carteResidentRepository;
    }

    /**
     * Crée une nouvelle carte résident liée à un passeport et une demande.
     * Les dates sont extraites du VisaTransformable lié à la demande.
     * Le numéro de carte est généré automatiquement avec le préfixe "CR-" + l'ID.
     *
     * @param passeport     le passeport lié à la carte résident
     * @param demande       la demande associée à la carte résident
     * @return              la carte résident créée et persistée
     */
    public CarteResident creer(Passeport passeport, Demande demande) {
        CarteResident carteResident = new CarteResident();
        carteResident.setPasseport(passeport);
        carteResident.setDemande(demande);

        // Extraire les dates du VisaTransformable
        VisaTransformable visa = demande.getVisaTransformable();
        if (visa != null) {
            carteResident.setDateDebut(visa.getDateEntree());
            carteResident.setDateFin(visa.getDateExpiration());
        }

        // Sauvegarder d'abord pour obtenir l'ID généré
        carteResident = carteResidentRepository.save(carteResident);

        // Générer le numéro avec préfixe + ID
        String numeroCarte = "CR-" + carteResident.getId();
        carteResident.setNumeroCarte(numeroCarte);

        return carteResidentRepository.save(carteResident);
    }
}
