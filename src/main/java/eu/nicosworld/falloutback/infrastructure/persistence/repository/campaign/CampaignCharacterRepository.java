package eu.nicosworld.falloutback.infrastructure.persistence.repository.campaign;

import eu.nicosworld.falloutback.infrastructure.persistence.entity.DomainUser;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.campaign.Campaign;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.campaign.CampaignCharacter;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.character.Character;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CampaignCharacterRepository extends JpaRepository<CampaignCharacter, Long> {

    Optional<CampaignCharacter> findByCampaignAndCharacter(Campaign campaign, Character character);

    // Invitations de campagne en attente pour un joueur
    @Query("SELECT cc FROM CampaignCharacter cc " +
        "WHERE cc.character.user = :player AND cc.status = 'PENDING'")
    List<CampaignCharacter> findPendingInvitationsForPlayer(@Param("player") DomainUser player);

    @Query("SELECT COUNT(cc) > 0 FROM CampaignCharacter cc WHERE cc.character.id = :characterId AND cc.campaign.gameMaster.id = :gmId")
    boolean existsByCharacterIdAndCampaignGameMasterId(@Param("characterId") Long characterId, @Param("gmId") Long gmId);

    @Query("SELECT cc FROM CampaignCharacter cc " +
        "WHERE cc.campaign.id = :campaignId AND cc.status = 'ACCEPTED'")
    List<CampaignCharacter> findCharacters(Long campaignId);

    boolean existsByCampaign_IdAndCharacter_Id(
        Long campaignId,
        Long characterId
    );

    Optional<CampaignCharacter> findByCharacter(Character character);
}