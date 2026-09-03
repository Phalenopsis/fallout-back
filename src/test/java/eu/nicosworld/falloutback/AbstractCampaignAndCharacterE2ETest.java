package eu.nicosworld.falloutback;

import eu.nicosworld.falloutback.domain.character.CreationStatus;
import eu.nicosworld.falloutback.infrastructure.web.dto.campaign.CampaignResponseDto;
import eu.nicosworld.falloutback.infrastructure.web.dto.campaign.CreateCampaignDto;
import eu.nicosworld.falloutback.infrastructure.web.dto.character.CharacterDto;
import eu.nicosworld.falloutback.infrastructure.web.dto.friendship.FriendRequestDto;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

public class AbstractCampaignAndCharacterE2ETest extends AbstractAuthE2ETest {

    /**
     * Ask for a friendship between 2 players
     * @param tokenPlayer1 access token
     * @param player2Email request friend username
     * @return friendship id
     */
    protected int setFriendship(String tokenPlayer1, String player2Email) {
        return given()
            .header("Authorization", "Bearer " + tokenPlayer1)
            .contentType(ContentType.JSON)
            .body(new FriendRequestDto(player2Email))
            .when()
            .post("/api/friends/request")
            .then()
            .statusCode(201)
            .extract().path("friendshipId");
    }

    /**
     * Accept a friendship request
     * @param tokenPlayer access token
     * @param friendshipId  friendship id to accept
     */
    protected void acceptFriendship(String tokenPlayer, int friendshipId) {
        given()
            .header("Authorization", "Bearer " + tokenPlayer)
            .when()
            .post("/api/friends/request/" + friendshipId + "/accept")
            .then()
            .statusCode(200);
    }

    /**
     * create a character
     * @param tokenPlayer access token
     * @return character id
     */
    protected int createCharacter(String tokenPlayer, String characterName) {
        CharacterDto characterReq = new CharacterDto(
            null,
            characterName,
            "Vault Dweller",
            null, // special
            CreationStatus.COMPLETED,
            null  // skills
        );

        return given()
            .header("Authorization", "Bearer " + tokenPlayer)
            .contentType(ContentType.JSON)
            .body(characterReq)
            .when()
            .post("/api/characters")
            .then()
            .statusCode(200)
            .extract().path("id");
    }

    /**
     *
     * @param tokenGm access token
     * @return campaign id
     */
    protected int createCampaign(String tokenGm, String campaignName) {
        return given()
            .header("Authorization", "Bearer " + tokenGm)
            .contentType(ContentType.JSON)
            .body(new CreateCampaignDto(campaignName))
            .when()
            .post("/api/campaigns")
            .then()
            .statusCode(200)
            .body("name", equalTo(campaignName))
            .extract().path("id");
    }

    /**
     * Invite un personnage dans la campagne.
     * On vérifie que le personnage invité se trouve bien dans la liste des membres avec le statut PENDING.
     */
    protected void inviteCharacterToCampaign(String tokenGm, int campaignId, int characterId) {
        given()
            .header("Authorization", "Bearer " + tokenGm)
            .when()
            .post("/api/campaigns/" + campaignId + "/invite/" + characterId)
            .then()
            .statusCode(200)
            // Vérifie qu'il existe un membre avec le characterId et le statut PENDING
            .body("members.find { it.characterId == " + characterId + " }.status", equalTo("PENDING"));
    }

    /**
     * Récupère l'invitation en attente d'un joueur pour une campagne spécifique.
     */
    protected int getPendingInvitation(String tokenPlayer, int campaignId) {
        return given()
            .header("Authorization", "Bearer " + tokenPlayer)
            .when()
            .get("/api/campaigns/invitations/pending")
            .then()
            .statusCode(200)
            // Trouve l'invitation correspondant à la campagne ciblée
            .body("size()", greaterThan(0))
            .extract()
            .path("find { it.campaignId == " + campaignId + " }.campaignCharacterId");
    }

    /**
     * <b>With only ONE invitation otherwise it must fail</b>
     * @param tokenPlayer access token
     * @return
     */
    protected int getPendingInvitation(String tokenPlayer) {
        return  given()
            .header("Authorization", "Bearer " + tokenPlayer)
            .when()
            .get("/api/campaigns/invitations/pending")
            .then()
            .statusCode(200)
            .body("size()", equalTo(1))
            .extract().path("[0].campaignCharacterId");
    }

    /**
     * Récupère l'id d'invitation pour un joueur et une campagne précise.
     */
    protected int getPendingInvitationForCampaign(String tokenPlayer, int campaignId) {
        return given()
            .header("Authorization", "Bearer " + tokenPlayer)
            .when()
            .get("/api/campaigns/invitations/pending")
            .then()
            .statusCode(200)
            .extract()
            .path("find { it.campaignId == " + campaignId + " }.campaignCharacterId");
    }

    /**
     * accept campaign invitation
     * @param tokenPlayer access token
     * @param campaignCharacterId invitation id
     */
    protected void acceptCampaignToInvitation(String tokenPlayer, int campaignCharacterId) {
        given()
            .header("Authorization", "Bearer " + tokenPlayer)
            .when()
            .post("/api/campaigns/invitations/" + campaignCharacterId + "/accept")
            .then()
            .statusCode(200)
            .body("status", equalTo("ACCEPTED"));
    }

    protected CampaignResponseDto getCharacterCampaign(String tokenPlayer, long characterId) {
        return given()
            .header("Authorization", "Bearer " + tokenPlayer)
            .when()
            .get("/api/characters/" + characterId + "/campaign")
            .then()
            .statusCode(200)
            .extract().as(CampaignResponseDto.class);
    }

}
