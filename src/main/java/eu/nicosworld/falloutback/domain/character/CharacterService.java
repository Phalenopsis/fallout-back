package eu.nicosworld.falloutback.domain.character;

import eu.nicosworld.falloutback.domain.domainUser.DomainUserService;
import eu.nicosworld.falloutback.exception.ResourceNotFoundException;
import eu.nicosworld.falloutback.exception.UnauthorizedAccessException;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.DomainUser;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.character.Character;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.character.Skills;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.character.Special;
import eu.nicosworld.falloutback.infrastructure.persistence.repository.campaign.CampaignCharacterRepository;
import eu.nicosworld.falloutback.infrastructure.persistence.repository.character.CharacterRepository;
import eu.nicosworld.falloutback.infrastructure.web.dto.character.CharacterDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class CharacterService {
    private final CharacterRepository characterRepository;
    private final DomainUserService domainUserService;
    private final CampaignCharacterRepository campaignCharacterRepository;

    public CharacterService(CharacterRepository characterRepository,
                            DomainUserService domainUserService,
                            CampaignCharacterRepository campaignCharacterRepository
    ) {
        this.characterRepository = characterRepository;
        this.domainUserService = domainUserService;
        this.campaignCharacterRepository = campaignCharacterRepository;
    }

    public Character save(CharacterDto characterDto, UserDetails userDetails) {
        DomainUser domainUser = domainUserService.findByUser(userDetails);

        Character character = new Character(characterDto, domainUser);
        Special special;
        if(Objects.isNull(characterDto.special())) {
            special = new Special();
        } else {
            special = new Special(characterDto.special());
        }

        character.setSpecial(special); // 🔥 lien bidirectionnel

        Skills skills;
        if(Objects.isNull(characterDto.skills())) {
            skills = new Skills();
        } else {
            skills = new Skills(characterDto.skills());
        }

        character.setSkills(skills);

        return characterRepository.save(character); // cascade save Special
    }

    public Character findByIdForUser(Long id, UserDetails userDetails) {
        DomainUser domainUser = domainUserService.findByUser(userDetails);

        Character character = characterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Character not found"));

        if (!character.getUser().getId().equals(domainUser.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        return character;
    }

    public Character update(Long id, CharacterDto characterDto, UserDetails userDetails) {
        DomainUser domainUser = domainUserService.findByUser(userDetails);

        Character character = characterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Character not found"));

        if (!character.getUser().getId().equals(domainUser.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        character.update(characterDto);

        return characterRepository.save(character);
    }

    public List<CharacterDto> findAvailableCharactersForUserForCampaign(Long friendId, UserDetails userDetails) {
            DomainUser currentUser = domainUserService.findByUser(userDetails);

            // Récupération des personnages éligibles directement depuis la BDD
            List<Character> availableCharacters = characterRepository.findAvailableCharactersForFriendAndCampaign(
                currentUser.getId(),
                friendId
            );

            // Mapping vers le DTO
            return availableCharacters.stream()
                .map(CharacterDto::mapFromEntity)
                .toList();
    }

    public Character findByIdForUserOrGM(Long characterId, UserDetails userDetails) {
        DomainUser currentUser = domainUserService.findByUser(userDetails);

        Character character = characterRepository.findById(characterId)
            .orElseThrow(() -> new ResourceNotFoundException("Personnage introuvable"));

        // 1. Si le personnage appartient à l'utilisateur courant, c'est OK
        if (character.getUser().getId().equals(currentUser.getId())) {
            return character;
        }

        // 2. Sinon, vérifier si l'utilisateur courant est MJ d'une campagne qui contient ce personnage
        boolean isGMOfCharacterCampaign = campaignCharacterRepository.existsByCharacterIdAndCampaignGameMasterId(
            characterId,
            currentUser.getId()
        );

        if (!isGMOfCharacterCampaign) {
            throw new UnauthorizedAccessException("Vous n'avez pas l'autorisation de consulter ce personnage.");
        }

        return character;
    }
}
