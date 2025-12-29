package eu.nicosworld.falloutback.infrastructure.web.dto.character;

import eu.nicosworld.falloutback.domain.character.CreationStatus;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.character.Character;

import java.util.Objects;

public record CharacterPreviewDto(Long id,
                                  String name,
                                  CreationStatus creationStatus) {
    public static CharacterPreviewDto mapFromEntity(Character character) {
        return new CharacterPreviewDto(
                character.getId(),
                character.getName(),
                character.getCreationStatus()
        );
    }
}
