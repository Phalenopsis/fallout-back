package eu.nicosworld.falloutback.infrastructure.web.controller.character;

import eu.nicosworld.falloutback.domain.character.CharacterService;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.character.Character;
import eu.nicosworld.falloutback.infrastructure.web.dto.character.CharacterDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("character")
public class CharacterController {

    private final CharacterService characterService;

    CharacterController(
            CharacterService characterService
    ) {
        this.characterService = characterService;
    }

    @PostMapping
    public CharacterDto saveCharacter(@AuthenticationPrincipal UserDetails userDetails,
                                      @RequestBody CharacterDto characterDto
    ) {
        Character character = characterService.save(characterDto, userDetails);
        return CharacterDto.mapFromEntity(character);
    }
}
