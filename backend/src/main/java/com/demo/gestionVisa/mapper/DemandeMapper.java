package com.demo.gestionVisa.mapper;

import com.demo.gestionVisa.dto.DemandePieceDTO;
import com.demo.gestionVisa.dto.DemandeResponseDTO;
import com.demo.gestionVisa.dto.HistoriqueStatutDTO;
import com.demo.gestionVisa.dto.PieceDTO;
import com.demo.gestionVisa.model.Demande;
import com.demo.gestionVisa.model.Piece;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class DemandeMapper {

    /**
     * Convertit une Demande persistée en DTO de réponse complet.
     * @param demande   entité avec toutes ses relations chargées
     * @return          DemandeResponseDTO prêt pour la vue
     */
    public DemandeResponseDTO toResponseDTO(Demande demande) {
        DemandeResponseDTO dto = new DemandeResponseDTO();
        dto.setId(demande.getId());
        dto.setDateDemande(demande.getDateDemande());

        if (demande.getDemandeur() != null) {
            dto.setNomDemandeur(demande.getDemandeur().getNom());
            dto.setPrenomDemandeur(demande.getDemandeur().getPrenom());
        }

        if (demande.getVisaTransformable() != null && demande.getVisaTransformable().getPasseport() != null) {
            dto.setNumeroPasSeport(demande.getVisaTransformable().getPasseport().getNumero());
            dto.setReferenceVisa(demande.getVisaTransformable().getReferenceVisa());
        }

        if (demande.getTypeVisa() != null) {
            dto.setTypeVisa(demande.getTypeVisa().getLibelle());
        }

        if (demande.getTypeDemande() != null) {
            dto.setTypeDemande(demande.getTypeDemande().getLibelle());
        }

        if (demande.getStatutDemande() != null) {
            dto.setStatutDemande(demande.getStatutDemande().getLibelle());
        }

        // mapping des pièces
        if (demande.getDemandePieces() != null) {
            dto.setPieces(demande.getDemandePieces().stream()
                .map(dp -> {
                    DemandePieceDTO dpDto = new DemandePieceDTO();
                    if (dp.getPiece() != null) {
                        dpDto.setIdPiece(dp.getPiece().getId());
                        dpDto.setNomPiece(dp.getPiece().getNom());
                        dpDto.setObligatoire(dp.getPiece().getObligatoire());
                    }
                    dpDto.setFourni(dp.getFourni());
                    return dpDto;
                })
                .collect(Collectors.toList()));
        }
        // mapping des historiques de statut
        if (demande.getHistoriques() != null) {
            dto.setHistoriques(demande.getHistoriques().stream()
                .map(h -> HistoriqueStatutDTO.builder()
                    .id(h.getId())
                    .statut(h.getStatutDemande() != null ? h.getStatutDemande().getLibelle() : null)
                    .dateChangement(h.getDateChangement())
                    .commentaire(h.getCommentaire())
                    .build())
                .collect(Collectors.toList()));
        }
        return dto;
    }

    /**
     * Convertit une Piece en PieceDTO.
     * @param piece     entité Piece
     * @return          PieceDTO
     */
    public PieceDTO toPieceDTO(Piece piece) {
        PieceDTO dto = new PieceDTO();
        dto.setId(piece.getId());
        dto.setNom(piece.getNom());
        dto.setObligatoire(piece.getObligatoire());
        if (piece.getTypePiece() != null) {
            dto.setTypePiece(piece.getTypePiece().getCode());
        }
        return dto;
    }
}
