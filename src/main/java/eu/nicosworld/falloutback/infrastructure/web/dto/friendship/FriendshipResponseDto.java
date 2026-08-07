package eu.nicosworld.falloutback.infrastructure.web.dto.friendship;

import eu.nicosworld.falloutback.domain.invitation.InvitationStatus;

public record FriendshipResponseDto(
    Long friendshipId,
    Long friendUserId,
    String friendUsername,
    InvitationStatus status,
    boolean isRequester
) {}