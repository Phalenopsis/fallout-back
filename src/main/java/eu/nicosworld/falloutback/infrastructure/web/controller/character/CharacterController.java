package eu.nicosworld.falloutback.infrastructure.web.controller.character;

import eu.nicosworld.falloutback.domain.character.CharacterService;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.character.Character;
import eu.nicosworld.falloutback.infrastructure.web.dto.character.CharacterDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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
        System.out.println(characterDto.originName());
        Character character = characterService.save(characterDto, userDetails);
        return CharacterDto.mapFromEntity(character);
    }

    @GetMapping("/{id}")
    public CharacterDto getCharacter(@AuthenticationPrincipal UserDetails userDetails,
                                     @PathVariable Long id) {
        Character character = characterService.findByIdForUser(id, userDetails);
        return CharacterDto.mapFromEntity(character);
    }

    @PutMapping("/{id}")
    public CharacterDto updateCharacter(@AuthenticationPrincipal UserDetails userDetails,
                                        @PathVariable Long id,
                                        @RequestBody CharacterDto characterDto) {
        Character updatedCharacter = characterService.update(id, characterDto, userDetails);
        return CharacterDto.mapFromEntity(updatedCharacter);
    }
}
