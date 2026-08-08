package eu.nicosworld.falloutback.infrastructure.web.dto.note;

import eu.nicosworld.falloutback.domain.note.NoteType;
import java.util.List;

public record CreateNoteDto(
    String title,
    String content,
    NoteType type,
    Long campaignId,
    Long characterId,
    List<String> shareWithEmails
) {}