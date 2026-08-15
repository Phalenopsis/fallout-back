package eu.nicosworld.falloutback.domain.note;

import eu.nicosworld.falloutback.AbstractAuthE2ETest;
import eu.nicosworld.falloutback.domain.character.CreationStatus;
import eu.nicosworld.falloutback.infrastructure.web.dto.character.CharacterDto;
import eu.nicosworld.falloutback.infrastructure.web.dto.note.CreateNoteDto;
import eu.nicosworld.falloutback.infrastructure.web.dto.note.UpdateNoteDto;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class NoteSecurityE2ETest extends AbstractAuthE2ETest {

    @Test
    void shouldReturn403And404OnUnauthorizedOrMissingNoteAccess() {
        // 1. Inscription de deux joueurs distincts
        registerUser("player1@vault.com", "password123!");
        registerUser("player2@vault.com", "password123!");

        String tokenPlayer1 = loginAndGetToken("player1@vault.com", "password123!");
        String tokenPlayer2 = loginAndGetToken("player2@vault.com", "password123!");

        CharacterDto characterReq = new CharacterDto(
            null,
            "Vault Dweller Bob",
            "Vault Dweller",
            null, // special
            CreationStatus.COMPLETED,
            null  // skills
        );

        // 2. Player 1 crée un vrai personnage pour y attacher sa note
        int characterId = given()
            .header("Authorization", "Bearer " + tokenPlayer1)
            .contentType(ContentType.JSON)
            .body(characterReq)
            .when()
            .post("/api/characters")
            .then()
            .statusCode(200)
            .extract().path("id");

        // 3. Player 1 crée une note personnelle liée à son personnage
        CreateNoteDto dto = new CreateNoteDto(
            "Secret de Player 1",
            "Emplacement de mon trésor caché.",
            NoteType.FREE_NOTE,
            null,
            (long) characterId, // ID valide
            null
        );

        int secretNoteId = given()
            .header("Authorization", "Bearer " + tokenPlayer1)
            .contentType(ContentType.JSON)
            .body(dto)
            .when()
            .post("/api/notes")
            .then()
            .statusCode(200)
            .extract().path("id");

        // 4. Player 2 essaie d'accéder directement à la note de Player 1 via l'URL -> 403 FORBIDDEN
        given()
            .header("Authorization", "Bearer " + tokenPlayer2)
            .when()
            .get("/api/notes/" + secretNoteId)
            .then()
            .statusCode(403)
            .body("error", equalTo("Accès refusé : vous n'avez pas les droits pour consulter cette note."));

        // 5. Player 2 essaie d'éditer la note de Player 1 -> 403 FORBIDDEN
        given()
            .header("Authorization", "Bearer " + tokenPlayer2)
            .contentType(ContentType.JSON)
            .body(new UpdateNoteDto("Piratage", "Contenu effacé", NoteType.FREE_NOTE, List.of()))
            .when()
            .put("/api/notes/" + secretNoteId)
            .then()
            .statusCode(403)
            .body("error", equalTo("Accès refusé : vous n'êtes pas l'auteur de cette note."));

        // 6. Un joueur demande une note inexistante (ID 99999) -> 404 NOT FOUND
        given()
            .header("Authorization", "Bearer " + tokenPlayer1)
            .when()
            .get("/api/notes/99999")
            .then()
            .statusCode(404)
            .body("error", equalTo("Note introuvable avec l'ID : 99999"));
    }
}