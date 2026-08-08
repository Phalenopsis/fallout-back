package eu.nicosworld.falloutback.infrastructure.web.controller.note;

import eu.nicosworld.falloutback.domain.note.NoteService;
import eu.nicosworld.falloutback.domain.note.NoteType;
import eu.nicosworld.falloutback.infrastructure.web.dto.note.CreateNoteDto;
import eu.nicosworld.falloutback.infrastructure.web.dto.note.NoteResponseDto;
import eu.nicosworld.falloutback.infrastructure.web.dto.note.NoteSummaryDto;
import eu.nicosworld.falloutback.infrastructure.web.dto.note.UpdateNoteDto;
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

    @PostMapping
    public ResponseEntity<NoteResponseDto> createNote(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestBody CreateNoteDto dto) {
        return ResponseEntity.ok(noteService.createNote(userDetails.getUsername(), dto));
    }

    // GET /api/notes/{id} -> Récupère UNE note complète quand on clique dessus
    @GetMapping("/{id}")
    public ResponseEntity<NoteResponseDto> getNoteById(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long id) {
        return ResponseEntity.ok(noteService.getNoteById(userDetails.getUsername(), id));
    }

    // GET /api/notes/character/{characterId}?type=QUEST
    // Si type est omis, retourne TOUTES les notes du personnage.
    // Retourne un DTO léger (id, title, type) pour la liste de l'onglet.
    @GetMapping("/character/{characterId}")
    public ResponseEntity<List<NoteSummaryDto>> getCharacterNotes(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long characterId,
        @RequestParam(required = false) NoteType type) {
        return ResponseEntity.ok(noteService.getCharacterNotesSummary(userDetails.getUsername(), characterId, type));
    }

    // GET /api/notes/campaign/{campaignId}?type=LOCATION
    @GetMapping("/campaign/{campaignId}")
    public ResponseEntity<List<NoteSummaryDto>> getCampaignNotes(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long campaignId,
        @RequestParam(required = false) NoteType type) {
        return ResponseEntity.ok(noteService.getCampaignNotesSummary(userDetails.getUsername(), campaignId, type));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteResponseDto> updateNote(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long id,
        @RequestBody UpdateNoteDto dto) {
        return ResponseEntity.ok(noteService.updateNote(userDetails.getUsername(), id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long id) {
        noteService.deleteNote(userDetails.getUsername(), id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/copy")
    public ResponseEntity<NoteResponseDto> copyNote(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long id) {
        return ResponseEntity.ok(noteService.copyNote(userDetails.getUsername(), id));
    }
}