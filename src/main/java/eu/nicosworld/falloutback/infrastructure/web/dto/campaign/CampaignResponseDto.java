package eu.nicosworld.falloutback.infrastructure.web.dto.campaign;

import java.util.List;

public record CampaignResponseDto(
    Long id,
    String name,
    Long gameMasterId,
    String gameMasterEmail,
    List<CampaignCharacterDto> members
) {}