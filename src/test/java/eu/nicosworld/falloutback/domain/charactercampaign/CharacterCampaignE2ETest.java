package eu.nicosworld.falloutback.domain.charactercampaign;

import eu.nicosworld.falloutback.AbstractCampaignAndCharacterE2ETest;
import eu.nicosworld.falloutback.infrastructure.web.dto.campaign.CampaignCharacterDto;
import eu.nicosworld.falloutback.infrastructure.web.dto.campaign.CampaignResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CharacterCampaignE2ETest extends AbstractCampaignAndCharacterE2ETest {

    @Test
    @DisplayName("Devrait récupérer la campagne d'un personnage avec exactement ses membres et exclure les autres campagnes")
    void shouldGetCharacterCampaignWithExactMembersOnly() {
        // ============================================================
        // 1. SETUP CAMPAGNE A (1 MJ + 3 Joueurs)
        // ============================================================
        String gmAEmail = "gm_campA@test.com";
        String p1Email = "player1_campA@test.com";
        String p2Email = "player2_campA@test.com";
        String p3Email = "player3_campA@test.com";
        String pass = "Password123!";

        registerUser(gmAEmail, pass);
        registerUser(p1Email, pass);
        registerUser(p2Email, pass);
        registerUser(p3Email, pass);

        String tokenGmA = loginAndGetToken(gmAEmail, pass);
        String tokenP1 = loginAndGetToken(p1Email, pass);
        String tokenP2 = loginAndGetToken(p2Email, pass);
        String tokenP3 = loginAndGetToken(p3Email, pass);

        // Amitiés MJ A <-> Joueurs 1, 2 et 3
        acceptFriendship(tokenGmA, setFriendship(tokenP1, gmAEmail));
        acceptFriendship(tokenGmA, setFriendship(tokenP2, gmAEmail));
        acceptFriendship(tokenGmA, setFriendship(tokenP3, gmAEmail));

        // Création Campagne A & Characters A
        int campaignAId = createCampaign(tokenGmA, "Wasteland Campaign A");
        int c1Id = createCharacter(tokenP1, "VaultDweller1");
        int c2Id = createCharacter(tokenP2, "VaultDweller2");
        int c3Id = createCharacter(tokenP3, "VaultDweller3");

        // Invitations & Acceptations Campagne A
        inviteCharacterToCampaign(tokenGmA, campaignAId, c1Id);
        acceptCampaignToInvitation(tokenP1, getPendingInvitation(tokenP1, campaignAId));

        inviteCharacterToCampaign(tokenGmA, campaignAId, c2Id);
        acceptCampaignToInvitation(tokenP2, getPendingInvitation(tokenP2, campaignAId));

        inviteCharacterToCampaign(tokenGmA, campaignAId, c3Id);
        acceptCampaignToInvitation(tokenP3, getPendingInvitation(tokenP3, campaignAId));


        // ============================================================
        // 2. SETUP CAMPAGNE B (1 Autre MJ + 1 Autre Joueur) - Isolation
        // ============================================================
        String gmBEmail = "gm_campB@test.com";
        String p4Email = "player4_campB@test.com";

        registerUser(gmBEmail, pass);
        registerUser(p4Email, pass);

        String tokenGmB = loginAndGetToken(gmBEmail, pass);
        String tokenP4 = loginAndGetToken(p4Email, pass);

        acceptFriendship(tokenGmB, setFriendship(tokenP4, gmBEmail));

        int campaignBId = createCampaign(tokenGmB, "Enclave Campaign B");
        int c4Id = createCharacter(tokenP4, "OutsiderCharacter");

        inviteCharacterToCampaign(tokenGmB, campaignBId, c4Id);
        acceptCampaignToInvitation(tokenP4, getPendingInvitation(tokenP4, campaignBId));


        // ============================================================
        // 3. EXECUTION ET VERIFICATIONS
        // ============================================================
        // Le personnage 1 interroge son appartenance à la campagne
        CampaignResponseDto response = getCharacterCampaign(tokenP1, c1Id);

        // Vérification des données de la campagne A
        assertNotNull(response);
        assertEquals((long) campaignAId, response.id());
        assertEquals("Wasteland Campaign A", response.name());
        assertEquals(gmAEmail, response.gameMasterEmail());

        // Vérification de la liste des membres (doit contenir exactement 3 personnages)
        List<CampaignCharacterDto> members = response.members();
        assertNotNull(members);
        assertEquals(3, members.size());

        // Extraction des IDs de personnages présents dans la réponse
        List<Long> memberCharacterIds = members.stream()
            .map(CampaignCharacterDto::characterId)
            .toList();

        // Affirmation : Les 3 personnages de la campagne A sont présents
        assertTrue(memberCharacterIds.contains((long) c1Id));
        assertTrue(memberCharacterIds.contains((long) c2Id));
        assertTrue(memberCharacterIds.contains((long) c3Id));

        // Affirmation : Le personnage de la campagne B n'est PAS présent
        assertFalse(memberCharacterIds.contains((long) c4Id));

        // Vérification des détails de liaison
        members.forEach(member -> {
            assertEquals((long) campaignAId, member.campaignId());
            assertEquals("Wasteland Campaign A", member.campaignName());
        });
    }
}