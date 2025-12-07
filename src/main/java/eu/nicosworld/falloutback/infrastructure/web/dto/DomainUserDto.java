package eu.nicosworld.falloutback.infrastructure.web.dto;

import eu.nicosworld.falloutback.infrastructure.persistence.entity.DomainUser;

public record DomainUserDto(String username) {
    public static DomainUserDto mapFromEntity(DomainUser user) {
        return new DomainUserDto(user.getUser().getUsername());
    }
}
