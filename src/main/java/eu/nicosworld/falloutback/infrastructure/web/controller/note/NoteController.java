package eu.nicosworld.falloutback.infrastructure.web.controller.note;

import eu.nicosworld.falloutback.domain.note.NoteService;
import eu.nicosworld.falloutback.infrastructure.web.dto.note.CreateNoteDto;
import eu.nicosworld.falloutback.infrastructure.web.dto.note.NoteResponseDto;
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

    @GetMapping
    public ResponseEntity<List<NoteResponseDto>> getMyNotes(
        @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(noteService.getMyNotes(userDetails.getUsername()));
    }

    @GetMapping("/campaign/{campaignId}")
    public ResponseEntity<List<NoteResponseDto>> getCampaignNotes(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long campaignId) {
        return ResponseEntity.ok(noteService.getCampaignNotes(userDetails.getUsername(), campaignId));
    }

    @GetMapping("/character/{characterId}")
    public ResponseEntity<List<NoteResponseDto>> getCharacterNotes(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long characterId) {
        return ResponseEntity.ok(noteService.getCharacterNotes(userDetails.getUsername(), characterId));
    }
}