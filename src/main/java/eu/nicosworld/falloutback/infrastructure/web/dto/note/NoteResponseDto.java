package eu.nicosworld.falloutback.infrastructure.web.dto.note;

import eu.nicosworld.falloutback.domain.note.NoteType;

import java.time.LocalDateTime;
import java.util.List;

public record NoteResponseDto(
    Long id,
    String title,
    String content,
    NoteType type,

    Long campaignId,
    Long characterId,

    String directory,

    boolean ownerNote,

    List<NoteShareTargetDto> sharedWith,

    Boolean read,

    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}