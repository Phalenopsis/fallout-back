package eu.nicosworld.falloutback.infrastructure.web.dto;

import eu.nicosworld.falloutback.infrastructure.persistence.entity.AuthenticatedUser;

public record AuthenticatedUserDto(String username) {
    public static AuthenticatedUserDto mapFromEntity(AuthenticatedUser user) {
        return new AuthenticatedUserDto(user.getUser().getUsername());
    }
}
