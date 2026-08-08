package eu.nicosworld.falloutback.domain.campaign;

import eu.nicosworld.falloutback.AbstractAuthE2ETest;
import eu.nicosworld.falloutback.domain.character.CreationStatus;
import eu.nicosworld.falloutback.infrastructure.web.dto.campaign.CreateCampaignDto;
import eu.nicosworld.falloutback.infrastructure.web.dto.character.CharacterDto;
import eu.nicosworld.falloutback.infrastructure.web.dto.friendship.FriendRequestDto;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class CampaignE2ETest extends AbstractAuthE2ETest {

    @Test
    void shouldCreateCampaignInviteCharacterAndAccept() {
        // 1. Inscription du MJ et du Joueur
        registerUser("mj@test.com", "password123");
        registerUser("player@test.com", "password123");

        String tokenGm = loginAndGetToken("mj@test.com", "password123");
        String tokenPlayer = loginAndGetToken("player@test.com", "password123");

        // 2. Établir l'amitié entre MJ et Joueur
        int friendshipId = given()
            .header("Authorization", "Bearer " + tokenGm)
            .contentType(ContentType.JSON)
            .body(new FriendRequestDto("player@test.com"))
            .when()
            .post("/api/friends/request")
            .then()
            .statusCode(200)
            .extract().path("friendshipId");

        given()
            .header("Authorization", "Bearer " + tokenPlayer)
            .when()
            .post("/api/friends/request/" + friendshipId + "/accept")
            .then()
            .statusCode(200);

        // 3. Le joueur crée un personnage (route "/character" & constructeur DTO exact)
        CharacterDto characterReq = new CharacterDto(
            null,
            "Vault Dweller Bob",
            "Vault Dweller",
            null, // special
            CreationStatus.COMPLETED,
            null  // skills
        );

        int characterId = given()
            .header("Authorization", "Bearer " + tokenPlayer)
            .contentType(ContentType.JSON)
            .body(characterReq)
            .when()
            .post("/api/characters")
            .then()
            .statusCode(200)
            .extract().path("id");

        // 4. Le MJ crée une campagne
        int campaignId = given()
            .header("Authorization", "Bearer " + tokenGm)
            .contentType(ContentType.JSON)
            .body(new CreateCampaignDto("Fallout: Boston Wasteland"))
            .when()
            .post("/api/campaigns")
            .then()
            .statusCode(200)
            .body("name", equalTo("Fallout: Boston Wasteland"))
            .extract().path("id");

        // 5. Le MJ invite le personnage du joueur
        given()
            .header("Authorization", "Bearer " + tokenGm)
            .when()
            .post("/api/campaigns/" + campaignId + "/invite/" + characterId)
            .then()
            .statusCode(200)
            .body("members.size()", equalTo(1))
            .body("members[0].status", equalTo("PENDING"));

        // 6. Le joueur récupère ses invitations en attente
        int campaignCharacterId = given()
            .header("Authorization", "Bearer " + tokenPlayer)
            .when()
            .get("/api/campaigns/invitations/pending")
            .then()
            .statusCode(200)
            .body("size()", equalTo(1))
            .extract().path("[0].campaignCharacterId");

        // 7. Le joueur accepte l'invitation
        given()
            .header("Authorization", "Bearer " + tokenPlayer)
            .when()
            .post("/api/campaigns/invitations/" + campaignCharacterId + "/accept")
            .then()
            .statusCode(200)
            .body("status", equalTo("ACCEPTED"));

        // 8. Le joueur vérifie que la campagne apparaît dans sa liste
        given()
            .header("Authorization", "Bearer " + tokenPlayer)
            .when()
            .get("/api/campaigns/player")
            .then()
            .statusCode(200)
            .body("size()", equalTo(1))
            .body("[0].name", equalTo("Fallout: Boston Wasteland"));
    }
}