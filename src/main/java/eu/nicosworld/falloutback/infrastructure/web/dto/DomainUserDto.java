package eu.nicosworld.falloutback.infrastructure.web.dto;

import eu.nicosworld.falloutback.infrastructure.persistence.entity.DomainUser;
import eu.nicosworld.falloutback.infrastructure.web.dto.character.CharacterDto;

import java.util.List;

public record DomainUserDto(Long id,
                            String username,
                            List<CharacterDto> characters) {
    public static DomainUserDto mapFromEntity(DomainUser user) {
        return new DomainUserDto(
                user.getId(),
                user.getUser().getUsername(),
                user.getCharacterList().stream().map(CharacterDto::mapFromEntity).toList()
        );
    }
}
