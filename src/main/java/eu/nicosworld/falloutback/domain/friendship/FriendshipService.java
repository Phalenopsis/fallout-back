package eu.nicosworld.falloutback.domain.friendship;

import eu.nicosworld.falloutback.authentication.UserRepository;
import eu.nicosworld.falloutback.authentication.model.User;
import eu.nicosworld.falloutback.domain.invitation.InvitationStatus;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.DomainUser;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.friendship.UserFriendship;
import eu.nicosworld.falloutback.infrastructure.persistence.repository.DomainUserRepository;
import eu.nicosworld.falloutback.infrastructure.persistence.repository.friendship.UserFriendshipRepository;
import eu.nicosworld.falloutback.infrastructure.web.dto.friendship.FriendshipResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FriendshipService {

    private final UserFriendshipRepository friendshipRepository;
    private final DomainUserRepository domainUserRepository;
    private final UserRepository userRepository;

    public FriendshipService(UserFriendshipRepository friendshipRepository,
                             DomainUserRepository domainUserRepository,
                             UserRepository userRepository) {
        this.friendshipRepository = friendshipRepository;
        this.domainUserRepository = domainUserRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public FriendshipResponseDto sendFriendRequest(String currentEmail, String targetEmail) {
        DomainUser currentUser = getDomainUserByEmail(currentEmail);
        DomainUser targetUser = getDomainUserByEmail(targetEmail);

        if (currentUser.getId().equals(targetUser.getId())) {
            throw new IllegalArgumentException("Vous ne pouvez pas vous ajouter vous-même en ami.");
        }

        friendshipRepository.findFriendshipBetween(currentUser, targetUser).ifPresent(f -> {
            throw new IllegalStateException("Une demande ou une amitié existe déjà entre ces utilisateurs.");
        });

        UserFriendship friendship = new UserFriendship(currentUser, targetUser);
        UserFriendship saved = friendshipRepository.save(friendship);

        return toDto(saved, currentUser.getId());
    }

    @Transactional
    public FriendshipResponseDto respondToFriendRequest(String currentEmail, Long friendshipId, boolean accept) {
        DomainUser currentUser = getDomainUserByEmail(currentEmail);

        UserFriendship friendship = friendshipRepository.findById(friendshipId)
            .orElseThrow(() -> new IllegalArgumentException("Demande d'ami introuvable"));

        if (!friendship.getAddressee().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("Vous n'êtes pas le destinataire de cette demande.");
        }

        friendship.setStatus(accept ? InvitationStatus.ACCEPTED : InvitationStatus.DECLINED);
        return toDto(friendshipRepository.save(friendship), currentUser.getId());
    }

    @Transactional(readOnly = true)
    public List<FriendshipResponseDto> getPendingRequests(String currentEmail) {
        DomainUser currentUser = getDomainUserByEmail(currentEmail);

        return friendshipRepository.findByAddresseeAndStatus(currentUser, InvitationStatus.PENDING)
            .stream()
            .map(f -> toDto(f, currentUser.getId()))
            .toList();
    }

    @Transactional(readOnly = true)
    public List<FriendshipResponseDto> getFriendsList(String currentEmail) {
        DomainUser currentUser = getDomainUserByEmail(currentEmail);

        return friendshipRepository.findAllAcceptedFriendships(currentUser)
            .stream()
            .map(f -> toDto(f, currentUser.getId()))
            .toList();
    }

    private DomainUser getDomainUserByEmail(String email) {
        User authUser = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable pour l'email : " + email));

        return domainUserRepository.findById(authUser.getId())
            .orElseThrow(() -> new IllegalArgumentException("Profil utilisateur introuvable"));
    }

    private FriendshipResponseDto toDto(UserFriendship friendship, Long currentUserId) {
        boolean isRequester = friendship.getRequester().getId().equals(currentUserId);
        DomainUser friend = isRequester ? friendship.getAddressee() : friendship.getRequester();

        return new FriendshipResponseDto(
            friendship.getId(),
            friend.getId(),
            friend.getUser().getEmail(),
            friendship.getStatus(),
            isRequester
        );
    }
}