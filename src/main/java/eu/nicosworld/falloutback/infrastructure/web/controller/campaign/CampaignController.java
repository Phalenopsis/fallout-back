package eu.nicosworld.falloutback.infrastructure.web.controller.campaign;

import eu.nicosworld.falloutback.domain.campaign.CampaignService;
import eu.nicosworld.falloutback.infrastructure.web.dto.campaign.CampaignCharacterDto;
import eu.nicosworld.falloutback.infrastructure.web.dto.campaign.CampaignResponseDto;
import eu.nicosworld.falloutback.infrastructure.web.dto.campaign.CreateCampaignDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/campaigns")
public class CampaignController {

    private final CampaignService campaignService;

    public CampaignController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @PostMapping
    public ResponseEntity<CampaignResponseDto> createCampaign(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestBody CreateCampaignDto dto) {
        return ResponseEntity.ok(campaignService.createCampaign(userDetails.getUsername(), dto));
    }

    @PostMapping("/{campaignId}/invite/{characterId}")
    public ResponseEntity<CampaignResponseDto> inviteCharacter(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long campaignId,
        @PathVariable Long characterId) {
        return ResponseEntity.ok(campaignService.inviteCharacter(userDetails.getUsername(), campaignId, characterId));
    }

    @PostMapping("/invitations/{campaignCharacterId}/accept")
    public ResponseEntity<CampaignCharacterDto> acceptInvitation(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long campaignCharacterId) {
        return ResponseEntity.ok(campaignService.respondToInvitation(userDetails.getUsername(), campaignCharacterId, true));
    }

    @PostMapping("/invitations/{campaignCharacterId}/decline")
    public ResponseEntity<CampaignCharacterDto> declineInvitation(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long campaignCharacterId) {
        return ResponseEntity.ok(campaignService.respondToInvitation(userDetails.getUsername(), campaignCharacterId, false));
    }

    @GetMapping("/gm")
    public ResponseEntity<List<CampaignResponseDto>> getGmCampaigns(
        @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(campaignService.getGmCampaigns(userDetails.getUsername()));
    }

    @GetMapping("/player")
    public ResponseEntity<List<CampaignResponseDto>> getPlayerCampaigns(
        @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(campaignService.getPlayerCampaigns(userDetails.getUsername()));
    }

    @GetMapping("/invitations/pending")
    public ResponseEntity<List<CampaignCharacterDto>> getPendingInvitations(
        @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(campaignService.getPendingInvitations(userDetails.getUsername()));
    }
}