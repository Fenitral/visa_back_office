package com.demo.gestionVisa.service;

import com.demo.gestionVisa.dto.ScanDemandeDTO;
import com.demo.gestionVisa.dto.ScanPieceDTO;
import com.demo.gestionVisa.exception.BusinessException;
import com.demo.gestionVisa.exception.ResourceNotFoundException;
import com.demo.gestionVisa.model.Demande;
import com.demo.gestionVisa.model.DemandePiece;
import com.demo.gestionVisa.model.StatutDemande;
import com.demo.gestionVisa.repository.DemandeRepository;
import com.demo.gestionVisa.repository.DemandePieceRepository;
import com.demo.gestionVisa.repository.StatutDemandeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service pour gérer le scan/upload des pièces justificatives
 * SPRINT 3
 */
@Service
@Transactional
@Slf4j
public class ScanPiecesService {

    @Value("${upload.directory:uploads}")
    private String uploadDirectory;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB
    private static final String[] ALLOWED_EXTENSIONS = {"pdf", "jpg", "jpeg", "png"};

    private final DemandeRepository demandeRepository;
    private final DemandePieceRepository demandePieceRepository;
    private final StatutDemandeRepository statutDemandeRepository;

    public ScanPiecesService(DemandeRepository demandeRepository,
                            DemandePieceRepository demandePieceRepository,
                            StatutDemandeRepository statutDemandeRepository) {
        this.demandeRepository = demandeRepository;
        this.demandePieceRepository = demandePieceRepository;
        this.statutDemandeRepository = statutDemandeRepository;
    }

    /**
     * Récupère l'état complet du scan pour une demande
     *
     * @param idDemande l'ID de la demande
     * @return DTO avec l'état du scan de toutes les pièces
     * @throws ResourceNotFoundException si la demande n'existe pas
     */
    public ScanDemandeDTO getEtatScan(Long idDemande) {
        Demande demande = demandeRepository.findById(idDemande)
                .orElseThrow(() -> new ResourceNotFoundException("Demande non trouvée: " + idDemande));

        List<ScanPieceDTO> pieces = demande.getDemandePieces().stream()
                .map(this::convertToScanPieceDTO)
                .collect(Collectors.toList());

        return ScanDemandeDTO.builder()
                .idDemande(demande.getId())
                .numeroDemande("#" + demande.getId())
                .nomDemandeur(demande.getDemandeur().getNom() + " " + demande.getDemandeur().getPrenom())
                .statutActuel(demande.getStatutDemande() != null ? demande.getStatutDemande().getLibelle() : "INCONNU")
                .pieces(pieces)
                .build();
    }

    /**
     * Convertit une entité DemandePiece en DTO ScanPieceDTO
     */
    private ScanPieceDTO convertToScanPieceDTO(DemandePiece demandePiece) {
        boolean isScanned = demandePiece.getCheminFichier() != null;
        return ScanPieceDTO.builder()
                .idDemandePiece(demandePiece.getId())
                .idPiece(demandePiece.getPiece().getId())
                .nomPiece(demandePiece.getPiece().getNom())
                .obligatoire(demandePiece.getPiece().getObligatoire())
                .fourni(demandePiece.getFourni() != null ? demandePiece.getFourni() : false)
                .scannee(isScanned)
                .nomFichier(demandePiece.getNomFichier())
                .dateUpload(demandePiece.getDateUpload())
                .build();
    }

    /**
     * Upload un fichier pour une pièce d'une demande
     *
     * @param idDemande l'ID de la demande
     * @param idPiece l'ID de la pièce
     * @param file le fichier à uploader
     * @throws BusinessException si le fichier n'est pas valide
     * @throws ResourceNotFoundException si la demande ou la pièce n'existe pas
     */
    public void uploadFichier(Long idDemande, Long idPiece, MultipartFile file) {
        // Vérifier que la demande existe
        Demande demande = demandeRepository.findById(idDemande)
                .orElseThrow(() -> new ResourceNotFoundException("Demande non trouvée: " + idDemande));

        // Vérifier que le fichier n'est pas vide
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Le fichier ne peut pas être vide");
        }

        // Vérifier la taille du fichier
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("La taille du fichier dépasse la limite de " + (MAX_FILE_SIZE / 1024 / 1024) + " MB");
        }

        // Vérifier l'extension
        String extension = getFileExtension(file.getOriginalFilename());
        if (!isExtensionAllowed(extension)) {
            throw new BusinessException("Format de fichier non autorisé. Formats acceptés: " + String.join(", ", ALLOWED_EXTENSIONS));
        }

        // Trouver ou créer le lien demande_piece
        DemandePiece demandePiece = demandePieceRepository.findByDemandeIdAndPieceId(idDemande, idPiece)
                .orElseThrow(() -> new BusinessException("Pièce non trouvée pour cette demande"));

        // Vérifier que la pièce a été fournie (complétée)
        if (demandePiece.getFourni() == null || !demandePiece.getFourni()) {
            throw new BusinessException("Cette pièce n'a pas encore été fournie. Veuillez la compléter d'abord.");
        }

        try {
            // Supprimer l'ancien fichier s'il existe
            if (demandePiece.getCheminFichier() != null) {
                try {
                    Path oldFilePath = Paths.get(demandePiece.getCheminFichier());
                    if (Files.exists(oldFilePath)) {
                        Files.delete(oldFilePath);
                        log.info("Ancien fichier supprimé - Demande: {}, Pièce: {}", idDemande, idPiece);
                    }
                } catch (IOException e) {
                    log.warn("Erreur lors de la suppression de l'ancien fichier - Demande: {}, Pièce: {}", idDemande, idPiece, e);
                }
            }

        try {
            // Créer le répertoire s'il n'existe pas
            Path uploadDir = Paths.get(uploadDirectory, "demande_" + idDemande);
            Files.createDirectories(uploadDir);

            // Générer un nom de fichier unique
            String fileName = UUID.randomUUID().toString() + "." + extension;
            Path filePath = uploadDir.resolve(fileName);

            // Sauvegarder le fichier
            Files.write(filePath, file.getBytes());

            // Mettre à jour la DemandePiece
            demandePiece.setCheminFichier(filePath.toString());
            demandePiece.setNomFichier(file.getOriginalFilename());
            demandePiece.setDateUpload(LocalDateTime.now());
            demandePieceRepository.save(demandePiece);

            log.info("Fichier uploadé avec succès - Demande: {}, Pièce: {}, Fichier: {}", idDemande, idPiece, fileName);

        } catch (IOException e) {
            log.error("Erreur lors de l'upload du fichier", e);
            throw new BusinessException("Erreur lors de l'upload du fichier: " + e.getMessage());
        }
    }

    /**
     * Valide que toutes les pièces obligatoires ont été scannées et change le statut de la demande
     *
     * @param idDemande l'ID de la demande
     * @throws BusinessException si des pièces obligatoires ne sont pas scannées
     * @throws ResourceNotFoundException si la demande n'existe pas
     */
    public void validerScan(Long idDemande) {
        Demande demande = demandeRepository.findById(idDemande)
                .orElseThrow(() -> new ResourceNotFoundException("Demande non trouvée: " + idDemande));

        // Vérifier que toutes les pièces obligatoires sont scannées
        boolean toutesObligatoiresScannees = demande.getDemandePieces().stream()
                .filter(dp -> dp.getPiece().getObligatoire())
                .allMatch(dp -> dp.getCheminFichier() != null);

        if (!toutesObligatoiresScannees) {
            throw new BusinessException("Toutes les pièces obligatoires doivent être scannées avant de valider");
        }

        // Changer le statut en "SCANNEE"
        StatutDemande statutScannee = statutDemandeRepository.findByLibelle("SCANNEE")
                .orElseThrow(() -> new BusinessException("Statut SCANNEE non trouvé en base de données"));

        demande.setStatutDemande(statutScannee);
        demandeRepository.save(demande);

        log.info("Validation du scan réussie pour la demande {}, statut changé à SCANNEE", idDemande);
    }

    /**
     * Récupère l'extension d'un fichier
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * Vérifie si l'extension est autorisée
     */
    private boolean isExtensionAllowed(String extension) {
        for (String allowed : ALLOWED_EXTENSIONS) {
            if (allowed.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Supprime un fichier scanné (en cas de changement)
     */
    public void supprimerFichier(Long idDemandePiece) {
        DemandePiece demandePiece = demandePieceRepository.findById(idDemandePiece)
                .orElseThrow(() -> new ResourceNotFoundException("Lien demande-pièce non trouvé"));

        if (demandePiece.getCheminFichier() != null) {
            try {
                Path filePath = Paths.get(demandePiece.getCheminFichier());
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                }
            } catch (IOException e) {
                log.warn("Erreur lors de la suppression du fichier", e);
            }
        }

        demandePiece.setCheminFichier(null);
        demandePiece.setNomFichier(null);
        demandePiece.setDateUpload(null);
        demandePieceRepository.save(demandePiece);
    }

    /**
     * Marque une pièce comme fournie (complétée)
     * Permet à l'utilisateur de valider qu'il a fourni/complété une pièce
     *
     * @param idDemande l'ID de la demande
     * @param idPiece l'ID de la pièce
     * @throws ResourceNotFoundException si la demande ou la pièce n'existe pas
     */
    public void marquerPieceCommeFournie(Long idDemande, Long idPiece) {
        // Vérifier que la demande existe
        Demande demande = demandeRepository.findById(idDemande)
                .orElseThrow(() -> new ResourceNotFoundException("Demande non trouvée: " + idDemande));

        // Trouver le lien demande_piece
        DemandePiece demandePiece = demandePieceRepository.findByDemandeIdAndPieceId(idDemande, idPiece)
                .orElseThrow(() -> new ResourceNotFoundException("Pièce non trouvée pour cette demande"));

        // Marquer comme fournie
        demandePiece.setFourni(true);
        demandePieceRepository.save(demandePiece);
        demandePieceRepository.flush();

        log.info("Pièce marquée comme fournie - Demande: {}, Pièce: {}", idDemande, idPiece);
    }
}
