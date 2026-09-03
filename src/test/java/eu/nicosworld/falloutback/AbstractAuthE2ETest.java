package eu.nicosworld.falloutback;

import eu.nicosworld.falloutback.authentication.model.UserLoginDTO;
import eu.nicosworld.falloutback.authentication.model.UserRegistrationDTO;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;

public class AbstractAuthE2ETest extends AbstractE2ETest {
    protected void registerUser(String email, String password) {
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setEmail(email);
        dto.setPassword(password);

        given()
            .contentType(ContentType.JSON)
            .body(dto)
            .when()
            .post("/auth/register")
            .then()
            .statusCode(201);
    }

    protected String loginAndGetToken(String email, String password) {
        UserLoginDTO dto = new UserLoginDTO();
        dto.setEmail(email);
        dto.setPassword(password);

        return given()
            .contentType(ContentType.JSON)
            .body(dto)
            .when()
            .post("/auth/login")
            .then()
            .statusCode(200)
            .extract().path("accessToken");
    }
}
