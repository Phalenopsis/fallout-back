package eu.nicosworld.falloutback.infrastructure.persistence.repository.friendship;

import eu.nicosworld.falloutback.domain.invitation.InvitationStatus;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.DomainUser;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.friendship.UserFriendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFriendshipRepository extends JpaRepository<UserFriendship, Long> {

    @Query("SELECT f FROM UserFriendship f WHERE " +
        "(f.requester = :u1 AND f.addressee = :u2) OR " +
        "(f.requester = :u2 AND f.addressee = :u1)")
    Optional<UserFriendship> findFriendshipBetween(@Param("u1") DomainUser u1, @Param("u2") DomainUser u2);

    // Liste des demandes d'amis reçues en attente
    List<UserFriendship> findByAddresseeAndStatus(DomainUser addressee, InvitationStatus status);

    // Liste des amitiés confirmées (dans un sens ou dans l'autre)
    @Query("SELECT f FROM UserFriendship f WHERE " +
        "(f.requester = :user OR f.addressee = :user) AND f.status = 'ACCEPTED'")
    List<UserFriendship> findAllAcceptedFriendships(@Param("user") DomainUser user);
}