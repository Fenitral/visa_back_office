package com.demo.gestionVisa.controller;

import com.demo.gestionVisa.dto.DemandeRequestDTO;
import com.demo.gestionVisa.dto.DemandeTransformationCreateRequest;
import com.demo.gestionVisa.dto.DemandeTransformationCreateResponse;
import com.demo.gestionVisa.dto.DemandeurDTO;
import com.demo.gestionVisa.dto.VisaTransformableDTO;
import com.demo.gestionVisa.enums.SituationFamiliale;
import com.demo.gestionVisa.enums.TypeDemande;
import com.demo.gestionVisa.model.Demande;
import com.demo.gestionVisa.model.Demandeur;
import com.demo.gestionVisa.service.DemandeService;
import com.demo.gestionVisa.service.DemandeurService;
import com.demo.gestionVisa.service.VisaTransformableService;
import java.time.Year;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demandes-transformation")
public class DemandeTransformationApiController {

    @Autowired
    private DemandeService demandeService;

    @Autowired
    private DemandeurService demandeurService;

    @Autowired
    private VisaTransformableService visaTransformableService;

    @GetMapping
    public ResponseEntity<Map<String, String>> aideEndpoint() {
        Map<String, String> response = new LinkedHashMap<>();
        response.put("message", "Endpoint actif. Utiliser POST pour creer une demande.");
        response.put("formulaire", "/demandes-transformation/nouvelle");
        response.put("api", "POST /api/demandes-transformation");
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> creerDemande(@RequestBody DemandeTransformationCreateRequest request) {
        try {
            validerRequest(request);

            DemandeurDTO demandeurDTO = new DemandeurDTO();
            demandeurDTO.setNom(request.getNom().trim());
            demandeurDTO.setPrenom(request.getPrenom().trim());
            demandeurDTO.setNomJeuneFille(clean(request.getNomDeJeuneFille()));
            demandeurDTO.setDateNaissance(request.getDateDeNaissance());
            demandeurDTO.setSituationFamiliale(mapSituation(request.getSituationDeFamille()));
            demandeurDTO.setNationalite(request.getNationalite().trim());
            demandeurDTO.setAdresseMadagascar(request.getAdresse().trim());
            demandeurDTO.setTelephone(clean(request.getTelephone()));

            Demandeur demandeur = demandeurService.getOuCreerDemandeur(demandeurDTO);

            VisaTransformableDTO visaDTO = new VisaTransformableDTO();
            visaDTO.setReference(request.getReference().trim());
            visaDTO.setDemandeurId(demandeur.getId());
            visaDTO.setDateEntree(request.getDateArrivee());
            visaDTO.setLieuEntree(request.getLieuDArrivee().trim());
            visaDTO.setDateExpiration(request.getDateExpiration());
            visaTransformableService.creerVisaTransformable(visaDTO);

            DemandeRequestDTO demandeRequestDTO = new DemandeRequestDTO();
            demandeRequestDTO.setDemandeur(demandeurDTO);
            demandeRequestDTO.setVisaTransformable(visaDTO);
            demandeRequestDTO.setTypeDemande(mapTypeDemande(request.getIdTypeVisa()));

            Demande demande = demandeService.creerDemande(demandeRequestDTO);
            String numeroDemande = String.format("DT-%d-%03d", Year.now().getValue(), demande.getId());

            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new DemandeTransformationCreateResponse(
                    demande.getId(),
                    numeroDemande,
                    demande.getStatut()
                ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur lors de la creation de la demande: " + e.getMessage());
        }
    }

    private void validerRequest(DemandeTransformationCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Le corps de la requete est obligatoire");
        }
        if (!StringUtils.hasText(request.getNom())) {
            throw new IllegalArgumentException("Le nom est obligatoire");
        }
        if (!StringUtils.hasText(request.getPrenom())) {
            throw new IllegalArgumentException("Le prenom est obligatoire");
        }
        if (request.getDateDeNaissance() == null) {
            throw new IllegalArgumentException("La date de naissance est obligatoire");
        }
        if (!StringUtils.hasText(request.getNationalite())) {
            throw new IllegalArgumentException("La nationalite est obligatoire");
        }
        if (!StringUtils.hasText(request.getAdresse())) {
            throw new IllegalArgumentException("L'adresse est obligatoire");
        }
        if (!StringUtils.hasText(request.getReference())) {
            throw new IllegalArgumentException("La reference visa est obligatoire");
        }
        if (request.getDateExpiration() == null) {
            throw new IllegalArgumentException("La date d'expiration est obligatoire");
        }
        if (request.getDateArrivee() == null) {
            throw new IllegalArgumentException("La date d'arrivee est obligatoire");
        }
        if (!StringUtils.hasText(request.getLieuDArrivee())) {
            throw new IllegalArgumentException("Le lieu d'arrivee est obligatoire");
        }
        if (request.getIdTypeVisa() == null) {
            throw new IllegalArgumentException("Le type de visa est obligatoire");
        }
    }

    private SituationFamiliale mapSituation(String situation) {
        if (!StringUtils.hasText(situation)) {
            return SituationFamiliale.CELIBATAIRE;
        }
        String value = situation.trim().toUpperCase()
            .replace('É', 'E')
            .replace('È', 'E')
            .replace('Ê', 'E')
            .replace('À', 'A')
            .replace('Ù', 'U')
            .replace('Ï', 'I')
            .replace(' ', '_')
            .replace('-', '_');

        if ("MARIEE".equals(value) || "MARIE(E)".equals(value) || "MARIE".equals(value)) {
            return SituationFamiliale.MARIE;
        }
        if ("DIVORCEE".equals(value) || "DIVORCE(E)".equals(value) || "DIVORCE".equals(value)) {
            return SituationFamiliale.DIVORCE;
        }
        if ("VEUVE".equals(value) || "VEUF(VE)".equals(value) || "VEUF".equals(value)) {
            return SituationFamiliale.VEUF;
        }
        if ("UNION_LIBRE".equals(value)) {
            return SituationFamiliale.UNION_LIBRE;
        }
        if ("PACS".equals(value) || "PACSE(E)".equals(value)) {
            return SituationFamiliale.PACS;
        }
        if ("CELIBATAIRE".equals(value)) {
            return SituationFamiliale.CELIBATAIRE;
        }
        return SituationFamiliale.CELIBATAIRE;
    }

    private TypeDemande mapTypeDemande(Integer idTypeVisa) {
        if (idTypeVisa == 1) {
            return TypeDemande.INVESTISSEUR;
        }
        if (idTypeVisa == 2) {
            return TypeDemande.TRAVAILLEUR;
        }
        throw new IllegalArgumentException("id_type_visa invalide. Valeurs attendues: 1 ou 2");
    }

    private String clean(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
