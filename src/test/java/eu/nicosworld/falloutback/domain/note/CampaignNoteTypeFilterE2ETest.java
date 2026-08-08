package eu.nicosworld.falloutback.domain.note;

import eu.nicosworld.falloutback.AbstractAuthE2ETest;
import eu.nicosworld.falloutback.infrastructure.web.dto.campaign.CreateCampaignDto;
import eu.nicosworld.falloutback.infrastructure.web.dto.note.CreateNoteDto;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class CampaignNoteTypeFilterE2ETest extends AbstractAuthE2ETest {

    @Test
    void shouldFetchCampaignNotesByTypeAndGetDetailsForSharedUser() {
        // 1. Enregistrement du MJ et du Joueur
        String gmEmail = "gm@vaulttec.com";
        String playerEmail = "sole_survivor@vault111.com";
        String password = "SecurePassword123!";

        registerUser(gmEmail, password);
        registerUser(playerEmail, password);

        String gmToken = loginAndGetToken(gmEmail, password);
        String playerToken = loginAndGetToken(playerEmail, password);

        // 2. Le MJ crée une campagne
        int campaignId = given()
            .header("Authorization", "Bearer " + gmToken)
            .contentType(ContentType.JSON)
            .body(new CreateCampaignDto("Terres Désolées de Boston"))
            .when()
            .post("/api/campaigns")
            .then()
            .statusCode(200)
            .extract().path("id");

        // 3. Le MJ crée deux notes de campagne et les partage avec le joueur
        // Note 1: Lieu (LOCATION)
        CreateNoteDto locationNoteDto = new CreateNoteDto(
            "Superduper Mart",
            "Grand magasin infesté de Goules sauvages au nord de Lexington.",
            NoteType.LOCATION,
            (long) campaignId,
            null,
            List.of(playerEmail)
        );

        // Note 2: Personnage non-joueur (NPC)
        CreateNoteDto npcNoteDto = new CreateNoteDto(
            "Preston Garvey",
            "Leader des Minutemen bloqué au Musée de la Liberté.",
            NoteType.NPC,
            (long) campaignId,
            null,
            List.of(playerEmail)
        );

        int locationNoteId = given()
            .header("Authorization", "Bearer " + gmToken)
            .contentType(ContentType.JSON)
            .body(locationNoteDto)
            .when()
            .post("/api/notes")
            .then()
            .statusCode(200)
            .extract().path("id");

        given()
            .header("Authorization", "Bearer " + gmToken)
            .contentType(ContentType.JSON)
            .body(npcNoteDto)
            .when()
            .post("/api/notes")
            .then()
            .statusCode(200);

        // 4. Le JOUEUR ouvre l'onglet 'Lieux' (LOCATION) de la campagne sur son Pip-Boy
        given()
            .header("Authorization", "Bearer " + playerToken)
            .queryParam("type", "LOCATION")
            .when()
            .get("/api/notes/campaign/" + campaignId)
            .then()
            .statusCode(200)
            .body("size()", equalTo(1)) // Seul le lieu est récupéré
            .body("[0].id", equalTo(locationNoteId))
            .body("[0].title", equalTo("Superduper Mart"))
            .body("[0].type", equalTo("LOCATION"))
            .body("[0].content", nullValue()); // Léger : pas de contenu envoyé

        // 5. Le JOUEUR clique sur le lieu pour afficher la fiche complète
        given()
            .header("Authorization", "Bearer " + playerToken)
            .when()
            .get("/api/notes/" + locationNoteId)
            .then()
            .statusCode(200)
            .body("id", equalTo(locationNoteId))
            .body("title", equalTo("Superduper Mart"))
            .body("type", equalTo("LOCATION"))
            .body("content", containsString("infesté de Goules sauvages"))
            .body("authorEmail", equalTo(gmEmail));
    }
}