package eu.nicosworld.falloutback.domain.campaign;

import eu.nicosworld.falloutback.AbstractCampaignAndCharacterE2ETest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class CampaignE2ETest extends AbstractCampaignAndCharacterE2ETest {

    @Test
    void shouldCreateCampaignInviteCharacterAndAccept() {
        // 1. Inscription du MJ et du Joueur
        String dmEmail = "mj@test.com";
        String dmPassword = "password123";

        String playerEmail = "player@test.com";
        String playerPassword = "123password!";
        registerUser(dmEmail, dmPassword);
        registerUser(playerEmail, playerPassword);

        String dmToken = loginAndGetToken(dmEmail, dmPassword);
        String playerToken = loginAndGetToken(playerEmail, playerPassword);

        String campaignName = "Fallout: Boston Wasteland";

        // 2. Établir l'amitié entre MJ et Joueur
        int friendshipId = setFriendship(dmToken, playerEmail);

        acceptFriendship(playerToken, friendshipId);

        int characterId = createCharacter(playerToken, "Bob");

        int campaignId = createCampaign(dmToken, campaignName);

        inviteCharacterToCampaign(dmToken, campaignId, characterId);

        // 6. Le joueur récupère ses invitations en attente
        int invitationId = getPendingInvitation(playerToken);

        // 7. Le joueur accepte l'invitation
        acceptCampaignToInvitation(playerToken, invitationId);

        // 8. Le joueur vérifie que la campagne apparaît dans sa liste
        given()
            .header("Authorization", "Bearer " + playerToken)
            .when()
            .get("/api/campaigns/player")
            .then()
            .statusCode(200)
            .body("size()", equalTo(1))
            .body("[0].name", equalTo(campaignName));
    }
}