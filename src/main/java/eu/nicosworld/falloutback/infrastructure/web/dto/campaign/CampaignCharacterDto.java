package eu.nicosworld.falloutback.infrastructure.web.dto.campaign;

import eu.nicosworld.falloutback.domain.invitation.InvitationStatus;

public record CampaignCharacterDto(
    Long campaignCharacterId,
    Long characterId,
    String characterName,
    String ownerEmail,
    InvitationStatus status
) {}