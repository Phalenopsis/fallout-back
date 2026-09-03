package eu.nicosworld.falloutback.infrastructure.web.dto.note;

import eu.nicosworld.falloutback.domain.note.NoteType;

public record NoteSummaryDto(
    Long id,
    String title,
    NoteType type,
    String directory,
    boolean ownerNote,
    boolean read
) {}