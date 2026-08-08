package eu.nicosworld.falloutback.domain.campaign;

import eu.nicosworld.falloutback.authentication.UserRepository;
import eu.nicosworld.falloutback.authentication.model.User;
import eu.nicosworld.falloutback.domain.invitation.InvitationStatus;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.DomainUser;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.campaign.Campaign;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.campaign.CampaignCharacter;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.character.Character;
import eu.nicosworld.falloutback.infrastructure.persistence.repository.DomainUserRepository;
import eu.nicosworld.falloutback.infrastructure.persistence.repository.campaign.CampaignCharacterRepository;
import eu.nicosworld.falloutback.infrastructure.persistence.repository.campaign.CampaignRepository;
import eu.nicosworld.falloutback.infrastructure.persistence.repository.character.CharacterRepository;
import eu.nicosworld.falloutback.infrastructure.persistence.repository.friendship.UserFriendshipRepository;
import eu.nicosworld.falloutback.infrastructure.web.dto.campaign.CampaignCharacterDto;
import eu.nicosworld.falloutback.infrastructure.web.dto.campaign.CampaignResponseDto;
import eu.nicosworld.falloutback.infrastructure.web.dto.campaign.CreateCampaignDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final CampaignCharacterRepository campaignCharacterRepository;
    private final CharacterRepository characterRepository;
    private final DomainUserRepository domainUserRepository;
    private final UserRepository userRepository;
    private final UserFriendshipRepository friendshipRepository;

    public CampaignService(CampaignRepository campaignRepository,
                           CampaignCharacterRepository campaignCharacterRepository,
                           CharacterRepository characterRepository,
                           DomainUserRepository domainUserRepository,
                           UserRepository userRepository,
                           UserFriendshipRepository friendshipRepository) {
        this.campaignRepository = campaignRepository;
        this.campaignCharacterRepository = campaignCharacterRepository;
        this.characterRepository = characterRepository;
        this.domainUserRepository = domainUserRepository;
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
    }

    @Transactional
    public CampaignResponseDto createCampaign(String gmEmail, CreateCampaignDto dto) {
        DomainUser gm = getDomainUserByEmail(gmEmail);
        Campaign campaign = new Campaign(dto.name(), gm);
        return toDto(campaignRepository.save(campaign));
    }

    @Transactional
    public CampaignResponseDto inviteCharacter(String gmEmail, Long campaignId, Long characterId) {
        DomainUser gm = getDomainUserByEmail(gmEmail);

        Campaign campaign = campaignRepository.findById(campaignId)
            .orElseThrow(() -> new IllegalArgumentException("Campagne introuvable"));

        if (!campaign.getGameMaster().getId().equals(gm.getId())) {
            throw new IllegalStateException("Seul le MJ de la campagne peut inviter des personnages.");
        }

        Character character = characterRepository.findById(characterId)
            .orElseThrow(() -> new IllegalArgumentException("Personnage introuvable"));

        DomainUser player = character.getUser();

        // Vérification 1 : Le MJ et le joueur doivent être amis
        boolean areFriends = friendshipRepository.findFriendshipBetween(gm, player)
            .map(f -> f.getStatus() == InvitationStatus.ACCEPTED)
            .orElse(false);

        if (!areFriends) {
            throw new IllegalStateException("Vous devez être ami avec le joueur pour inviter son personnage.");
        }

        // Vérification 2 : Pas d'invitation en double
        campaignCharacterRepository.findByCampaignAndCharacter(campaign, character).ifPresent(cc -> {
            throw new IllegalStateException("Ce personnage a déjà été invité dans cette campagne.");
        });

        CampaignCharacter member = new CampaignCharacter(campaign, character);
        campaign.addCharacter(member);

        return toDto(campaignRepository.save(campaign));
    }

    @Transactional
    public CampaignCharacterDto respondToInvitation(String playerEmail, Long campaignCharacterId, boolean accept) {
        DomainUser player = getDomainUserByEmail(playerEmail);

        CampaignCharacter invitation = campaignCharacterRepository.findById(campaignCharacterId)
            .orElseThrow(() -> new IllegalArgumentException("Invitation introuvable"));

        // Sécurité : Seul le propriétaire du personnage peut répondre
        if (!invitation.getCharacter().getUser().getId().equals(player.getId())) {
            throw new IllegalStateException("Vous n'êtes pas le propriétaire de ce personnage.");
        }

        invitation.setStatus(accept ? InvitationStatus.ACCEPTED : InvitationStatus.DECLINED);
        CampaignCharacter saved = campaignCharacterRepository.save(invitation);

        return toMemberDto(saved);
    }

    @Transactional(readOnly = true)
    public List<CampaignResponseDto> getGmCampaigns(String gmEmail) {
        DomainUser gm = getDomainUserByEmail(gmEmail);
        return campaignRepository.findByGameMaster(gm).stream()
            .map(this::toDto)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<CampaignResponseDto> getPlayerCampaigns(String playerEmail) {
        DomainUser player = getDomainUserByEmail(playerEmail);
        return campaignRepository.findAllJoinedByPlayer(player).stream()
            .map(this::toDto)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<CampaignCharacterDto> getPendingInvitations(String playerEmail) {
        DomainUser player = getDomainUserByEmail(playerEmail);
        return campaignCharacterRepository.findPendingInvitationsForPlayer(player).stream()
            .map(this::toMemberDto)
            .toList();
    }

    private DomainUser getDomainUserByEmail(String email) {
        User authUser = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable pour l'email : " + email));

        return domainUserRepository.findById(authUser.getId())
            .orElseThrow(() -> new IllegalArgumentException("Profil utilisateur introuvable"));
    }

    private CampaignResponseDto toDto(Campaign campaign) {
        List<CampaignCharacterDto> memberDtos = campaign.getMembers().stream()
            .map(this::toMemberDto)
            .toList();

        return new CampaignResponseDto(
            campaign.getId(),
            campaign.getName(),
            campaign.getGameMaster().getId(),
            campaign.getGameMaster().getUser().getEmail(),
            memberDtos
        );
    }

    private CampaignCharacterDto toMemberDto(CampaignCharacter cc) {
        return new CampaignCharacterDto(
            cc.getId(),
            cc.getCharacter().getId(),
            cc.getCharacter().getName(),
            cc.getCharacter().getUser().getUser().getEmail(),
            cc.getStatus()
        );
    }
}