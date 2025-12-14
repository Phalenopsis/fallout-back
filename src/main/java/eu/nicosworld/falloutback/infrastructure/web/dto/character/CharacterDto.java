package eu.nicosworld.falloutback.infrastructure.web.dto.character;

import eu.nicosworld.falloutback.infrastructure.persistence.entity.character.Character;

public record CharacterDto(Long id,
                           String name,
                           SpecialDto special) {
    public static CharacterDto mapFromEntity(Character character) {
        return new CharacterDto(
                character.getId(),
                character.getName(),
                SpecialDto.mapFromEntity(character.getSpecial())
        );
    }
}
