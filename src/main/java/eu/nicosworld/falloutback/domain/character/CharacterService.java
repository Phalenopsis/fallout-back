package eu.nicosworld.falloutback.domain.character;

import eu.nicosworld.falloutback.domain.domainUser.DomainUserService;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.DomainUser;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.character.Character;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.character.Special;
import eu.nicosworld.falloutback.infrastructure.persistence.repository.character.CharacterRepository;
import eu.nicosworld.falloutback.infrastructure.web.dto.character.CharacterDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CharacterService {
    private final CharacterRepository characterRepository;
    private final DomainUserService domainUserService;

    public CharacterService(CharacterRepository characterRepository,
                            DomainUserService domainUserService
    ) {
        this.characterRepository = characterRepository;
        this.domainUserService = domainUserService;
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
}
