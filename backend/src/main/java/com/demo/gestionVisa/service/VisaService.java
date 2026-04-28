package com.demo.gestionVisa.service;

import com.demo.gestionVisa.model.Demande;
import com.demo.gestionVisa.model.Passeport;
import com.demo.gestionVisa.model.Visa;
import com.demo.gestionVisa.model.VisaTransformable;
import com.demo.gestionVisa.repository.VisaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VisaService {

    private final VisaRepository visaRepository;

    public VisaService(VisaRepository visaRepository) {
        this.visaRepository = visaRepository;
    }

    /**
     * Crée un nouveau visa lié à un passeport et une demande.
     * Les dates sont extraites du VisaTransformable lié à la demande.
     * La référence visa est générée automatiquement avec le préfixe "VIS-" + l'ID.
     *
     * @param passeport     le passeport lié au visa
     * @param demande       la demande associée au visa
     * @return              le visa créé et persisté
     */
    public Visa creer(Passeport passeport, Demande demande) {
        Visa visa = new Visa();
        visa.setPasseport(passeport);
        visa.setDemande(demande);

        // Extraire les dates du VisaTransformable
        VisaTransformable visaTransformable = demande.getVisaTransformable();
        if (visaTransformable != null) {
            visa.setDateDebut(visaTransformable.getDateEntree());
            visa.setDateFin(visaTransformable.getDateExpiration());
        }

        // Sauvegarder d'abord pour obtenir l'ID généré
        visa = visaRepository.save(visa);

        // Générer la référence avec préfixe + ID
        String referenceVisa = "VIS-" + visa.getId();
        visa.setReferenceVisa(referenceVisa);

        return visaRepository.save(visa);
    }
}
