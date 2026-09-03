package eu.nicosworld.falloutback.infrastructure.web.controller.note;

import eu.nicosworld.falloutback.domain.note.NoteService;
import eu.nicosworld.falloutback.domain.note.NoteType;
import eu.nicosworld.falloutback.infrastructure.web.dto.note.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }


    // ============================================================
    // 1. CRÉATION
    // ============================================================

    /**
     * Création d'une note appartenant à un character.
     */
    @PostMapping("/character/{characterId}")
    public ResponseEntity<NoteResponseDto> createCharacterNote(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long characterId,
        @RequestBody CreateNoteDto dto
    ) {
        return ResponseEntity.ok(
            noteService.createCharacterNote(
                userDetails,
                characterId,
                dto
            )
        );
    }

    /**
     * Création d'une note appartenant à une campagne.
     * La note appartient donc au MJ de cette campagne.
     */
    @PostMapping("/campaign/{campaignId}")
    public ResponseEntity<NoteResponseDto> createCampaignNote(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long campaignId,
        @RequestBody CreateNoteDto dto
    ) {
        return ResponseEntity.ok(
            noteService.createCampaignNote(
                userDetails,
                campaignId,
                dto
            )
        );
    }


    // ============================================================
    // 2. VISUALISATION D'UNE NOTE
    // ============================================================

    /**
     * Récupère une note appartenant à un character,
     * ou partagée avec ce character.
     *
     * Si la note est partagée et qu'elle est affichée directement,
     * le service devra également marquer le partage comme "lu".
     */
    @GetMapping("/character/{characterId}/{noteId}")
    public ResponseEntity<NoteResponseDto> getCharacterNote(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long characterId,
        @PathVariable Long noteId
    ) {
        return ResponseEntity.ok(
            noteService.getCharacterNote(
                userDetails,
                characterId,
                noteId
            )
        );
    }

    /**
     * Récupère une note appartenant à une campagne,
     * ou partagée avec cette campagne.
     */
    @GetMapping("/campaign/{campaignId}/{noteId}")
    public ResponseEntity<NoteResponseDto> getCampaignNote(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long campaignId,
        @PathVariable Long noteId
    ) {
        return ResponseEntity.ok(
            noteService.getCampaignNote(
                userDetails,
                campaignId,
                noteId
            )
        );
    }


    // ============================================================
    // 3. LISTE DES NOTES
    // ============================================================

    /**
     * Liste des notes accessibles par un character
     */
    @GetMapping("/character/{characterId}/all")
    public ResponseEntity<List<NoteSummaryDto>> getAllCharacterNotes(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long characterId
    ) {
        return ResponseEntity.ok(
            noteService.getCharacterNotes(
                userDetails,
                characterId,
                null
            )
        );
    }

    /**
     * Liste des notes accessibles par une campagne.
     */
    @GetMapping("/campaign/{campaignId}/all")
    public ResponseEntity<List<NoteSummaryDto>> getCampaignNotes(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long campaignId
    ) {
        return ResponseEntity.ok(
            noteService.getCampaignNotes(
                userDetails,
                campaignId,
                null
            )
        );
    }


    // ============================================================
    // 4. MODIFICATION
    // ============================================================

    /**
     * Modification d'une note appartenant à un character.
     *
     * Une note partagée ne peut pas être modifiée par le destinataire.
     */
    @PutMapping("/character/{characterId}/{noteId}")
    public ResponseEntity<NoteResponseDto> updateCharacterNote(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long characterId,
        @PathVariable Long noteId,
        @RequestBody UpdateNoteDto dto
    ) {
        return ResponseEntity.ok(
            noteService.updateCharacterNote(
                userDetails,
                characterId,
                noteId,
                dto
            )
        );
    }

    /**
     * Modification d'une note appartenant à une campagne.
     */
    @PutMapping("/campaign/{campaignId}/{noteId}")
    public ResponseEntity<NoteResponseDto> updateCampaignNote(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long campaignId,
        @PathVariable Long noteId,
        @RequestBody UpdateNoteDto dto
    ) {
        return ResponseEntity.ok(
            noteService.updateCampaignNote(
                userDetails,
                campaignId,
                noteId,
                dto
            )
        );
    }


    // ============================================================
    // 5. PARTAGE
    // ============================================================

    /**
     * Partage d'une note de campagne vers un character.
     *
     * Le MJ partage sa note avec un character précis.
     */
    @PostMapping("/campaign/{campaignId}/{noteId}/share/character/{characterId}")
    public ResponseEntity<NoteResponseDto> shareCampaignNoteWithCharacter(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long campaignId,
        @PathVariable Long noteId,
        @PathVariable Long characterId
    ) {
        return ResponseEntity.ok(
            noteService.shareCampaignNoteWithCharacter(
                userDetails,
                campaignId,
                noteId,
                characterId
            )
        );
    }

    /**
     * Partage d'une note de character avec un autre character.
     */
    @PostMapping("/character/{characterId}/{noteId}/share/character/{targetCharacterId}")
    public ResponseEntity<NoteResponseDto> shareCharacterNoteWithCharacter(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long characterId,
        @PathVariable Long noteId,
        @PathVariable Long targetCharacterId
    ) {
        return ResponseEntity.ok(
            noteService.shareCharacterNoteWithCharacter(
                userDetails,
                characterId,
                noteId,
                targetCharacterId
            )
        );
    }

    /**
     * Partage d'une note de character avec la campagne.
     *
     * Dans ton modèle, cela signifie :
     *     le MJ de la campagne peut lire la note.
     *
     * Cela ne la partage PAS automatiquement avec les autres characters.
     */
    @PostMapping("/character/{characterId}/{noteId}/share/campaign/{campaignId}")
    public ResponseEntity<NoteResponseDto> shareCharacterNoteWithCampaign(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long characterId,
        @PathVariable Long noteId,
        @PathVariable Long campaignId
    ) {
        return ResponseEntity.ok(
            noteService.shareCharacterNoteWithCampaign(
                userDetails,
                characterId,
                noteId,
                campaignId
            )
        );
    }


    // ============================================================
    // 6. SUPPRESSION D'UN PARTAGE
    // ============================================================

    /**
     * Supprime le partage d'une note de character avec un autre character.
     */
    @DeleteMapping("/character/{characterId}/{noteId}/share/character/{targetCharacterId}")
    public ResponseEntity<Void> unshareCharacterNoteFromCharacter(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long characterId,
        @PathVariable Long noteId,
        @PathVariable Long targetCharacterId
    ) {
        noteService.unshareCharacterNoteFromCharacter(
            userDetails,
            characterId,
            noteId,
            targetCharacterId
        );

        return ResponseEntity.noContent().build();
    }

    /**
     * Supprime le partage d'une note de character avec une campagne.
     */
    @DeleteMapping("/character/{characterId}/{noteId}/share/campaign/{campaignId}")
    public ResponseEntity<Void> unshareCharacterNoteFromCampaign(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long characterId,
        @PathVariable Long noteId,
        @PathVariable Long campaignId
    ) {
        noteService.unshareCharacterNoteFromCampaign(
            userDetails,
            characterId,
            noteId,
            campaignId
        );

        return ResponseEntity.noContent().build();
    }

    /**
     * Supprime le partage d'une note de campagne avec un character.
     */
    @DeleteMapping("/campaign/{campaignId}/{noteId}/share/character/{characterId}")
    public ResponseEntity<Void> unshareCampaignNoteFromCharacter(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long campaignId,
        @PathVariable Long noteId,
        @PathVariable Long characterId
    ) {
        noteService.unshareCampaignNoteFromCharacter(
            userDetails,
            campaignId,
            noteId,
            characterId
        );

        return ResponseEntity.noContent().build();
    }


    // ============================================================
    // 7. COPIE
    // ============================================================

    /**
     * Copie d'une note accessible depuis un character.
     *
     * La copie appartient au character qui effectue la copie.
     */
    @PostMapping("/character/{characterId}/{noteId}/copy")
    public ResponseEntity<NoteResponseDto> copyCharacterNote(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long characterId,
        @PathVariable Long noteId
    ) {
        return ResponseEntity.ok(
            noteService.copyCharacterNote(
                userDetails,
                characterId,
                noteId
            )
        );
    }

    /**
     * Copie d'une note accessible depuis une campagne.
     *
     * La copie appartient à la campagne.
     */
    @PostMapping("/campaign/{campaignId}/{noteId}/copy")
    public ResponseEntity<NoteResponseDto> copyCampaignNote(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long campaignId,
        @PathVariable Long noteId
    ) {
        return ResponseEntity.ok(
            noteService.copyCampaignNote(
                userDetails,
                campaignId,
                noteId
            )
        );
    }


    // ============================================================
    // 8. DIRECTORY
    // ============================================================

    /**
     * Déplace une note dans l'arborescence du propriétaire.
     *
     * Si la note est une note propriétaire :
     *     le directory de la note est modifié.
     *
     * Si la note est une note partagée :
     *     le déplacement doit provoquer une copie,
     *     conformément au comportement défini côté métier.
     */
    @PutMapping("/character/{characterId}/{noteId}/directory")
    public ResponseEntity<NoteResponseDto> moveCharacterNote(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long characterId,
        @PathVariable Long noteId,
        @RequestBody UpdateNoteDirectoryDto dto
    ) {
        return ResponseEntity.ok(
            noteService.moveCharacterNote(
                userDetails,
                characterId,
                noteId,
                dto.directory()
            )
        );
    }

    @PutMapping("/campaign/{campaignId}/{noteId}/directory")
    public ResponseEntity<NoteResponseDto> moveCampaignNote(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long campaignId,
        @PathVariable Long noteId,
        @RequestBody UpdateNoteDirectoryDto dto
    ) {
        return ResponseEntity.ok(
            noteService.moveCampaignNote(
                userDetails,
                campaignId,
                noteId,
                dto.directory()
            )
        );
    }


    // ============================================================
    // 9. SUPPRESSION D'UNE NOTE
    // ============================================================

    /**
     * Suppression d'une note appartenant à un character.
     *
     * Si l'utilisateur supprime une note depuis son dossier "shared",
     * ce n'est pas la note qui est supprimée :
     * c'est son partage qui est supprimé.
     */
    @DeleteMapping("/character/{characterId}/{noteId}")
    public ResponseEntity<Void> deleteCharacterNote(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long characterId,
        @PathVariable Long noteId
    ) {
        noteService.deleteCharacterNote(
            userDetails,
            characterId,
            noteId
        );

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/campaign/{campaignId}/{noteId}")
    public ResponseEntity<Void> deleteCampaignNote(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long campaignId,
        @PathVariable Long noteId
    ) {
        noteService.deleteCampaignNote(
            userDetails,
            campaignId,
            noteId
        );

        return ResponseEntity.noContent().build();
    }
}