package eu.nicosworld.falloutback;

import eu.nicosworld.falloutback.domain.note.NoteType;
import eu.nicosworld.falloutback.infrastructure.web.dto.note.*;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;

import java.util.List;

import static io.restassured.RestAssured.given;

public abstract class AbstractNoteE2ETest extends AbstractCampaignAndCharacterE2ETest {

    // ============================================================
    // 1. CRÉATION
    // ============================================================

    protected NoteResponseDto createCharacterNote(String tokenPlayer, long characterId, CreateNoteDto dto) {
        return given()
            .header("Authorization", "Bearer " + tokenPlayer)
            .contentType(ContentType.JSON)
            .body(dto)
            .when()
            .post("/api/notes/character/" + characterId)
            .then()
            .statusCode(200)
            .extract().as(NoteResponseDto.class);
    }

    protected NoteResponseDto createCampaignNote(String tokenGm, long campaignId, CreateNoteDto dto) {
        return given()
            .header("Authorization", "Bearer " + tokenGm)
            .contentType(ContentType.JSON)
            .body(dto)
            .when()
            .post("/api/notes/campaign/" + campaignId)
            .then()
            .statusCode(200)
            .extract().as(NoteResponseDto.class);
    }

    // ============================================================
    // 2. VISUALISATION (GET)
    // ============================================================

    protected NoteResponseDto getCharacterNote(String tokenPlayer, long characterId, long noteId) {
        return given()
            .header("Authorization", "Bearer " + tokenPlayer)
            .when()
            .get("/api/notes/character/" + characterId + "/" + noteId)
            .then()
            .statusCode(200)
            .extract().as(NoteResponseDto.class);
    }

    protected NoteResponseDto getCampaignNote(String tokenGm, long campaignId, long noteId) {
        return given()
            .header("Authorization", "Bearer " + tokenGm)
            .when()
            .get("/api/notes/campaign/" + campaignId + "/" + noteId)
            .then()
            .statusCode(200)
            .extract().as(NoteResponseDto.class);
    }

    protected void getCharacterNoteUnauthorized(String tokenPlayer, long characterId, long noteId, int expectedStatusCode) {
        given()
            .header("Authorization", "Bearer " + tokenPlayer)
            .when()
            .get("/api/notes/character/" + characterId + "/" + noteId)
            .then()
            .statusCode(expectedStatusCode);
    }

    // ============================================================
    // 3. LISTES
    // ============================================================

    protected List<NoteSummaryDto> getCharacterNotes(String tokenPlayer, long characterId) {
        return given()
            .header("Authorization", "Bearer " + tokenPlayer)
            .when()
            .get("/api/notes/character/" + characterId + "/all")
            .then()
            .statusCode(200)
            .extract().as(new TypeRef<List<NoteSummaryDto>>() {});
    }

    protected List<NoteSummaryDto> getCampaignNotes(String tokenGm, long campaignId) {
        return given()
            .header("Authorization", "Bearer " + tokenGm)
            .when()
            .get("/api/notes/campaign/" + campaignId + "/all")
            .then()
            .statusCode(200)
            .extract().as(new TypeRef<List<NoteSummaryDto>>() {});
    }

    // ============================================================
    // 4. MODIFICATION
    // ============================================================

    protected NoteResponseDto updateCharacterNote(String tokenPlayer, long characterId, long noteId, UpdateNoteDto dto) {
        return given()
            .header("Authorization", "Bearer " + tokenPlayer)
            .contentType(ContentType.JSON)
            .body(dto)
            .when()
            .put("/api/notes/character/" + characterId + "/" + noteId)
            .then()
            .statusCode(200)
            .extract().as(NoteResponseDto.class);
    }

    protected NoteResponseDto updateCampaignNote(String tokenGm, long campaignId, long noteId, UpdateNoteDto dto) {
        return given()
            .header("Authorization", "Bearer " + tokenGm)
            .contentType(ContentType.JSON)
            .body(dto)
            .when()
            .put("/api/notes/campaign/" + campaignId + "/" + noteId)
            .then()
            .statusCode(200)
            .extract().as(NoteResponseDto.class);
    }

    protected void updateCharacterNoteForbidden(String tokenPlayer, long characterId, long noteId, UpdateNoteDto dto) {
        given()
            .header("Authorization", "Bearer " + tokenPlayer)
            .contentType(ContentType.JSON)
            .body(dto)
            .when()
            .put("/api/notes/character/" + characterId + "/" + noteId)
            .then()
            .statusCode(403);
    }

    // ============================================================
    // 5. PARTAGE
    // ============================================================

    protected NoteResponseDto shareCampaignNoteWithCharacter(String tokenGm, long campaignId, long noteId, long targetCharacterId) {
        return given()
            .header("Authorization", "Bearer " + tokenGm)
            .when()
            .post("/api/notes/campaign/" + campaignId + "/" + noteId + "/share/character/" + targetCharacterId)
            .then()
            .statusCode(200)
            .extract().as(NoteResponseDto.class);
    }

    protected void shareCampaignNoteWithCharacterFails(String tokenGm, long campaignId, long noteId, long targetCharacterId, int expectedStatusCode) {
        given()
            .header("Authorization", "Bearer " + tokenGm)
            .when()
            .post("/api/notes/campaign/" + campaignId + "/" + noteId + "/share/character/" + targetCharacterId)
            .then()
            .statusCode(expectedStatusCode);
    }

    protected NoteResponseDto shareCharacterNoteWithCharacter(String tokenPlayer, long characterId, long noteId, long targetCharacterId) {
        return given()
            .header("Authorization", "Bearer " + tokenPlayer)
            .when()
            .post("/api/notes/character/" + characterId + "/" + noteId + "/share/character/" + targetCharacterId)
            .then()
            .statusCode(200)
            .extract().as(NoteResponseDto.class);
    }

    protected void shareCharacterNoteWithCharacterFails(String tokenPlayer, long characterId, long noteId, long targetCharacterId, int expectedStatusCode) {
        given()
            .header("Authorization", "Bearer " + tokenPlayer)
            .when()
            .post("/api/notes/character/" + characterId + "/" + noteId + "/share/character/" + targetCharacterId)
            .then()
            .statusCode(expectedStatusCode);
    }

    protected NoteResponseDto shareCharacterNoteWithCampaign(String tokenPlayer, long characterId, long noteId, long campaignId) {
        return given()
            .header("Authorization", "Bearer " + tokenPlayer)
            .when()
            .post("/api/notes/character/" + characterId + "/" + noteId + "/share/campaign/" + campaignId)
            .then()
            .statusCode(200)
            .extract().as(NoteResponseDto.class);
    }

    protected void shareCharacterNoteWithCampaignFails(String tokenPlayer, long characterId, long noteId, long campaignId, int expectedStatusCode) {
        given()
            .header("Authorization", "Bearer " + tokenPlayer)
            .when()
            .post("/api/notes/character/" + characterId + "/" + noteId + "/share/campaign/" + campaignId)
            .then()
            .statusCode(expectedStatusCode);
    }

    // ============================================================
    // 6. SUPPRESSION DU PARTAGE (UNSHARE)
    // ============================================================

    protected void unshareCharacterNoteFromCharacter(String tokenPlayer, long characterId, long noteId, long targetCharacterId) {
        given()
            .header("Authorization", "Bearer " + tokenPlayer)
            .when()
            .delete("/api/notes/character/" + characterId + "/" + noteId + "/share/character/" + targetCharacterId)
            .then()
            .statusCode(204);
    }

    protected void unshareCharacterNoteFromCampaign(String tokenPlayer, long characterId, long noteId, long campaignId) {
        given()
            .header("Authorization", "Bearer " + tokenPlayer)
            .when()
            .delete("/api/notes/character/" + characterId + "/" + noteId + "/share/campaign/" + campaignId)
            .then()
            .statusCode(204);
    }

    protected void unshareCampaignNoteFromCharacter(String tokenGm, long campaignId, long noteId, long characterId) {
        given()
            .header("Authorization", "Bearer " + tokenGm)
            .when()
            .delete("/api/notes/campaign/" + campaignId + "/" + noteId + "/share/character/" + characterId)
            .then()
            .statusCode(204);
    }

    // ============================================================
    // 7. COPIE
    // ============================================================

    protected NoteResponseDto copyCharacterNote(String tokenPlayer, long characterId, long noteId) {
        return given()
            .header("Authorization", "Bearer " + tokenPlayer)
            .when()
            .post("/api/notes/character/" + characterId + "/" + noteId + "/copy")
            .then()
            .statusCode(200)
            .extract().as(NoteResponseDto.class);
    }

    protected NoteResponseDto copyCampaignNote(String tokenGm, long campaignId, long noteId) {
        return given()
            .header("Authorization", "Bearer " + tokenGm)
            .when()
            .post("/api/notes/campaign/" + campaignId + "/" + noteId + "/copy")
            .then()
            .statusCode(200)
            .extract().as(NoteResponseDto.class);
    }

    // ============================================================
    // 8. DIRECTORY
    // ============================================================

    protected NoteResponseDto moveCharacterNote(String tokenPlayer, long characterId, long noteId, String newDirectory) {
        return given()
            .header("Authorization", "Bearer " + tokenPlayer)
            .contentType(ContentType.JSON)
            .body(new UpdateNoteDirectoryDto(newDirectory))
            .when()
            .put("/api/notes/character/" + characterId + "/" + noteId + "/directory")
            .then()
            .statusCode(200)
            .extract().as(NoteResponseDto.class);
    }

    protected NoteResponseDto moveCampaignNote(String tokenGm, long campaignId, long noteId, String newDirectory) {
        return given()
            .header("Authorization", "Bearer " + tokenGm)
            .contentType(ContentType.JSON)
            .body(new UpdateNoteDirectoryDto(newDirectory))
            .when()
            .put("/api/notes/campaign/" + campaignId + "/" + noteId + "/directory")
            .then()
            .statusCode(200)
            .extract().as(NoteResponseDto.class);
    }

    // ============================================================
    // 9. SUPPRESSION DE NOTE
    // ============================================================

    protected void deleteCharacterNote(String tokenPlayer, long characterId, long noteId) {
        given()
            .header("Authorization", "Bearer " + tokenPlayer)
            .when()
            .delete("/api/notes/character/" + characterId + "/" + noteId)
            .then()
            .statusCode(204);
    }

    protected void deleteCampaignNote(String tokenGm, long campaignId, long noteId) {
        given()
            .header("Authorization", "Bearer " + tokenGm)
            .when()
            .delete("/api/notes/campaign/" + campaignId + "/" + noteId)
            .then()
            .statusCode(204);
    }

    // ============================================================
    // HELPER DE CONFIGURATION COMPLÈTE
    // ============================================================

    public record SetupData(
        String tokenGm,
        int campaignId,
        String tokenPlayer1,
        int character1Id,
        String tokenPlayer2,
        int character2Id
    ) {}

    /**
     * Crée un univers de test complet : 1 MJ, 1 Campagne, 2 Joueurs avec 1 Character chacun,
     * tous membres de la même campagne.
     */
    protected SetupData createFullCampaignSetup(String suffix) {
        // Registrations & Logins
        String gmEmail = "gm_" + suffix + "@test.com";
        String p1Email = "player1_" + suffix + "@test.com";
        String p2Email = "player2_" + suffix + "@test.com";
        String pass = "Password123!";

        registerUser(gmEmail, pass);
        registerUser(p1Email, pass);
        registerUser(p2Email, pass);

        String tokenGm = loginAndGetToken(gmEmail, pass);
        String tokenP1 = loginAndGetToken(p1Email, pass);
        String tokenP2 = loginAndGetToken(p2Email, pass);

        // Amitiés MJ <-> Joueurs (si requis pour invitation)
        int f1 = setFriendship(tokenP1, gmEmail);
        acceptFriendship(tokenGm, f1);

        int f2 = setFriendship(tokenP2, gmEmail);
        acceptFriendship(tokenGm, f2);

        // Création Campagne & Characters
        int campaignId = createCampaign(tokenGm, "Campaign " + suffix);
        int c1Id = createCharacter(tokenP1, "VaultDweller1_" + suffix);
        int c2Id = createCharacter(tokenP2, "VaultDweller2_" + suffix);

        // Invitations dans la campagne
        inviteCharacterToCampaign(tokenGm, campaignId, c1Id);
        int inv1Id = getPendingInvitation(tokenP1, campaignId);
        acceptCampaignToInvitation(tokenP1, inv1Id);

        inviteCharacterToCampaign(tokenGm, campaignId, c2Id);
        int inv2Id = getPendingInvitation(tokenP2, campaignId);
        acceptCampaignToInvitation(tokenP2, inv2Id);

        return new SetupData(tokenGm, campaignId, tokenP1, c1Id, tokenP2, c2Id);
    }
}