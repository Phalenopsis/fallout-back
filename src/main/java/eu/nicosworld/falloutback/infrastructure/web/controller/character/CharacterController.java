package eu.nicosworld.falloutback.infrastructure.web.controller.character;

import eu.nicosworld.falloutback.domain.campaign.CampaignService;
import eu.nicosworld.falloutback.domain.character.CharacterService;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.character.Character;
import eu.nicosworld.falloutback.infrastructure.web.dto.campaign.CampaignResponseDto;
import eu.nicosworld.falloutback.infrastructure.web.dto.character.CharacterDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/characters")
public class CharacterController {

    private final CharacterService characterService;
    private final CampaignService campaignService;

    CharacterController(
            CharacterService characterService,
            CampaignService campaignService
    ) {
        this.characterService = characterService;
        this.campaignService = campaignService;
    }

    @GetMapping("/friend/{friendId}/available")
    public ResponseEntity<List<CharacterDto>> getAvailableCharactersForFriend(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long friendId) {

        List<CharacterDto> availableChars = characterService.findAvailableCharactersForUserForCampaign(friendId, userDetails);
        return ResponseEntity.ok(availableChars);
    }

    @GetMapping("/{characterId}/campaign")
    public ResponseEntity<CampaignResponseDto> getPlayerCampaign(@AuthenticationPrincipal UserDetails userDetails,
                                                                 @PathVariable Long characterId) {
        CampaignResponseDto campaign = campaignService.getCharacterCampaign(userDetails, characterId);
        return ResponseEntity.ok(campaign);
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
        Character character = characterService.findByIdForUserOrGM(id, userDetails);
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
