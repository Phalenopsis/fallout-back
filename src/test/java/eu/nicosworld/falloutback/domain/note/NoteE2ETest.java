package eu.nicosworld.falloutback.domain.note;

import eu.nicosworld.falloutback.AbstractAuthE2ETest;
import eu.nicosworld.falloutback.infrastructure.web.dto.campaign.CreateCampaignDto;
import eu.nicosworld.falloutback.infrastructure.web.dto.note.CreateNoteDto;
import eu.nicosworld.falloutback.infrastructure.web.dto.note.UpdateNoteDto;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

class NoteE2ETest extends AbstractAuthE2ETest {

    @Test
    void shouldCreateShareAndCopyNoteWithType() {
        // 1. Inscription et Connexion du MJ et du Joueur
        registerUser("mj@test.com", "password123");
        registerUser("player@test.com", "password123");

        String tokenGm = loginAndGetToken("mj@test.com", "password123");
        String tokenPlayer = loginAndGetToken("player@test.com", "password123");

        // 2. Création d'une campagne par le MJ
        int campaignId = given()
            .header("Authorization", "Bearer " + tokenGm)
            .contentType(ContentType.JSON)
            .body(new CreateCampaignDto("Operation Anchorage"))
            .when()
            .post("/api/campaigns")
            .then()
            .statusCode(200)
            .extract().path("id");

        // 3. Le MJ crée une note de type LOCATION et la partage avec le joueur
        CreateNoteDto createNoteDto = new CreateNoteDto(
            "Indices sur la cache d'armes",
            "Chercher derrière la station Red Rocket.",
            NoteType.LOCATION,
            (long) campaignId,
            null,
            List.of("player@test.com")
        );

        int originalNoteId = given()
            .header("Authorization", "Bearer " + tokenGm)
            .contentType(ContentType.JSON)
            .body(createNoteDto)
            .when()
            .post("/api/notes")
            .then()
            .statusCode(200)
            .body("type", equalTo("LOCATION"))
            .body("sharedWithEmails[0]", equalTo("player@test.com"))
            .extract().path("id");

        // 4. Le joueur consulte les notes de la campagne
        given()
            .header("Authorization", "Bearer " + tokenPlayer)
            .when()
            .get("/api/notes/campaign/" + campaignId)
            .then()
            .statusCode(200)
            .body("size()", equalTo(1))
            .body("[0].type", equalTo("LOCATION"))
            .body("[0].title", equalTo("Indices sur la cache d'armes"));

        // 5. Tentative de modification non autorisée par le joueur -> Refusé
        given()
            .header("Authorization", "Bearer " + tokenPlayer)
            .contentType(ContentType.JSON)
            .body(new UpdateNoteDto("Titre piraté", "Hacked", NoteType.LOCATION, List.of()))
            .when()
            .put("/api/notes/" + originalNoteId)
            .then()
            .statusCode(403);

        // 6. Le joueur fait une COPIE de la note
        int copiedNoteId = given()
            .header("Authorization", "Bearer " + tokenPlayer)
            .when()
            .post("/api/notes/" + originalNoteId + "/copy")
            .then()
            .statusCode(200)
            .body("title", containsString("[Copie]"))
            .body("type", equalTo("LOCATION"))
            .body("authorEmail", equalTo("player@test.com"))
            .extract().path("id");

        // 7. Le joueur modifie sa copie (changement de type vers QUEST)
        given()
            .header("Authorization", "Bearer " + tokenPlayer)
            .contentType(ContentType.JSON)
            .body(new UpdateNoteDto("Ma quête perso", "Vérifier pièges", NoteType.QUEST, List.of()))
            .when()
            .put("/api/notes/" + copiedNoteId)
            .then()
            .statusCode(200)
            .body("title", equalTo("Ma quête perso"))
            .body("type", equalTo("QUEST"));
    }
}