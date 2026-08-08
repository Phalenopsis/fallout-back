package eu.nicosworld.falloutback.infrastructure.persistence.repository.campaign;

import eu.nicosworld.falloutback.infrastructure.persistence.entity.DomainUser;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.campaign.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    // Campagnes créées par le MJ
    List<Campaign> findByGameMaster(DomainUser gameMaster);

    // Campagnes rejointes par au moins un personnage accepté du joueur
    @Query("SELECT DISTINCT c FROM Campaign c " +
        "JOIN c.members m " +
        "WHERE m.character.user = :player AND m.status = 'ACCEPTED'")
    List<Campaign> findAllJoinedByPlayer(@Param("player") DomainUser player);
}