package eu.nicosworld.falloutback.infrastructure.web.dto.note;

public record NoteShareTargetDto(
    ShareTargetType type,
    Long id,
    String name
) {}