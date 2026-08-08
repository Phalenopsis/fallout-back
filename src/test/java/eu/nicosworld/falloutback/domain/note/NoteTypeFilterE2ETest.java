package eu.nicosworld.falloutback.domain.note;

import eu.nicosworld.falloutback.AbstractAuthE2ETest;
import eu.nicosworld.falloutback.domain.character.CreationStatus;
import eu.nicosworld.falloutback.infrastructure.web.dto.character.CharacterDto;
import eu.nicosworld.falloutback.infrastructure.web.dto.note.CreateNoteDto;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class NoteTypeFilterE2ETest extends AbstractAuthE2ETest {

    @Test
    void shouldFetchCharacterNotesByTypeAndThenGetDetails() {
        // 1. Inscription et Connexion du Joueur
        String email = "vaultdweller@vault111.com";
        String password = "PipboyPassword123!";
        registerUser(email, password);
        String token = loginAndGetToken(email, password);

        CharacterDto characterReq = new CharacterDto(
            null,
            "Vault Dweller Bob",
            "Vault Dweller",
            null, // special
            CreationStatus.COMPLETED,
            null  // skills
        );

        // 2. Création d'un Personnage
        int characterId = given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body(characterReq)
            .when()
            .post("/api/characters") // Adapte l'URL selon ton controller de Personnage
            .then()
            .statusCode(200)
            .extract().path("id");

        // 3. Création de deux notes : une de type NPC et une de type QUEST
        CreateNoteDto npcNoteDto = new CreateNoteDto(
            "Nick Valentine",
            "Détective synthétique basé à Diamond City. Très utile pour enquêter.",
            NoteType.NPC,
            null,
            (long) characterId,
            null
        );

        CreateNoteDto questNoteDto = new CreateNoteDto(
            "Obtenir de l'énergie pour Sanctuary",
            "Installer des générateurs et raccorder le réseau.",
            NoteType.QUEST,
            null,
            (long) characterId,
            null
        );

        int npcNoteId = given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body(npcNoteDto)
            .when()
            .post("/api/notes")
            .then()
            .statusCode(200)
            .extract().path("id");

        given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body(questNoteDto)
            .when()
            .post("/api/notes")
            .then()
            .statusCode(200);

        // 4. Filtrage : Le Pip-Boy demande UNIQUEMENT les notes de type NPC
        given()
            .header("Authorization", "Bearer " + token)
            .queryParam("type", "NPC")
            .when()
            .get("/api/notes/character/" + characterId)
            .then()
            .statusCode(200)
            .body("size()", equalTo(1)) // Seule la note PNJ est retournée
            .body("[0].id", equalTo(npcNoteId))
            .body("[0].title", equalTo("Nick Valentine"))
            .body("[0].type", equalTo("NPC"))
            .body("[0].content", nullValue()); // Vérifie que le DTO léger ne charge pas le gros contenu

        // 5. Sélection : Le Pip-Boy charge le détail complet de la note PNJ
        given()
            .header("Authorization", "Bearer " + token)
            .when()
            .get("/api/notes/" + npcNoteId)
            .then()
            .statusCode(200)
            .body("id", equalTo(npcNoteId))
            .body("title", equalTo("Nick Valentine"))
            .body("type", equalTo("NPC"))
            .body("content", containsString("Détective synthétique basé à Diamond City"));
    }
}