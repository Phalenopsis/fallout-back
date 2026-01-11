package eu.nicosworld.falloutback.infrastructure.web.dto.character;

import eu.nicosworld.falloutback.domain.character.CreationStatus;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.character.Character;

public record CharacterDto(Long id,
                           String name,
                           String originName,
                           SpecialDto special,
                           CreationStatus creationStatus,
                           SkillsDto skills
) {
    public static CharacterDto mapFromEntity(Character character) {
        return new CharacterDto(
                character.getId(),
                character.getName(),
                character.getOriginName(),
                SpecialDto.mapFromEntity(character.getSpecial()),
                character.getCreationStatus(),
                SkillsDto.mapFromEntity(character.getSkills())
        );
    }
}
