package eu.nicosworld.falloutback.infrastructure.web.dto.note;

import eu.nicosworld.falloutback.domain.note.NoteType;
import java.time.LocalDateTime;
import java.util.List;

public record NoteResponseDto(
    Long id,
    String title,
    String content,
    NoteType type,
    Long authorId,
    String authorEmail,
    Long campaignId,
    Long characterId,
    List<String> sharedWithEmails,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}