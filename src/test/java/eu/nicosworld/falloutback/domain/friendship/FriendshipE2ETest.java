package eu.nicosworld.falloutback.domain.friendship;

import eu.nicosworld.falloutback.AbstractAuthE2ETest;
import eu.nicosworld.falloutback.infrastructure.web.dto.friendship.FriendRequestDto;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class FriendshipE2ETest extends AbstractAuthE2ETest {

    @Test
    void shouldSendAndAcceptFriendRequestSuccessfully() {
        // 1. Inscription (201 CREATED)
        registerUser("userA@test.com", "password123");
        registerUser("userB@test.com", "password123");

        // 2. Connexion
        String tokenA = loginAndGetToken("userA@test.com", "password123");
        String tokenB = loginAndGetToken("userB@test.com", "password123");

        // 3. Demande d'ami de UserA vers UserB
        int friendshipId = given()
            .header("Authorization", "Bearer " + tokenA)
            .contentType(ContentType.JSON)
            .body(new FriendRequestDto("userB@test.com"))
            .when()
            .post("/api/friends/request")
            .then()
            .statusCode(200)
            .body("friendUsername", equalTo("userB@test.com"))
            .body("status", equalTo("PENDING"))
            .extract()
            .path("friendshipId");

        // 4. UserB consulte ses demandes reçues
        given()
            .header("Authorization", "Bearer " + tokenB)
            .when()
            .get("/api/friends/pending")
            .then()
            .statusCode(200)
            .body("size()", equalTo(1))
            .body("[0].friendUsername", equalTo("userA@test.com"))
            .body("[0].isRequester", equalTo(false));

        // 5. UserB accepte
        given()
            .header("Authorization", "Bearer " + tokenB)
            .when()
            .post("/api/friends/request/" + friendshipId + "/accept")
            .then()
            .statusCode(200)
            .body("status", equalTo("ACCEPTED"));

        // 6. Vérification liste d'amis réciproque
        given()
            .header("Authorization", "Bearer " + tokenA)
            .when()
            .get("/api/friends")
            .then()
            .statusCode(200)
            .body("size()", equalTo(1))
            .body("[0].friendUsername", equalTo("userB@test.com"));

        given()
            .header("Authorization", "Bearer " + tokenB)
            .when()
            .get("/api/friends")
            .then()
            .statusCode(200)
            .body("size()", equalTo(1))
            .body("[0].friendUsername", equalTo("userA@test.com"));
    }
}