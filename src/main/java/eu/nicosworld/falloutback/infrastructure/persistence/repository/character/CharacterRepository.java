package eu.nicosworld.falloutback.infrastructure.persistence.repository.character;

import eu.nicosworld.falloutback.infrastructure.persistence.entity.character.Character;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CharacterRepository extends JpaRepository<Character, Long> {
    @Query("""
        SELECT c FROM Character c 
        WHERE c.user.id = :friendId 
        AND c.creationStatus = eu.nicosworld.falloutback.domain.character.CreationStatus.COMPLETED
        AND EXISTS (
            SELECT 1 FROM UserFriendship f 
            WHERE f.status = eu.nicosworld.falloutback.domain.invitation.InvitationStatus.ACCEPTED 
            AND (
                (f.requester.id = :userId AND f.addressee.id = :friendId) 
                OR 
                (f.requester.id = :friendId AND f.addressee.id = :userId)
            )
        )
        AND NOT EXISTS (
            SELECT 1 FROM CampaignCharacter cc 
            WHERE cc.character.id = c.id 
            AND cc.status IN (
                eu.nicosworld.falloutback.domain.invitation.InvitationStatus.PENDING, 
                eu.nicosworld.falloutback.domain.invitation.InvitationStatus.ACCEPTED
            )
        )
    """)
    List<Character> findAvailableCharactersForFriendAndCampaign(
        @Param("userId") Long userId,
        @Param("friendId") Long friendId
    );
}
