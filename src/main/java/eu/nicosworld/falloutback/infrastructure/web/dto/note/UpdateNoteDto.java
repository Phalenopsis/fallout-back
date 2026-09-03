package eu.nicosworld.falloutback.infrastructure.web.dto.note;

import eu.nicosworld.falloutback.domain.note.NoteType;
import java.util.List;

public record UpdateNoteDto(
    String title,
    String content,
    NoteType type,
    List<NoteShareTargetDto> shareTargets,
    String directory
) {}