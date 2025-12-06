package eu.nicosworld.authentication.authentication;

import eu.nicosworld.authentication.AbstractE2ETest;
import eu.nicosworld.authentication.authentication.model.UserLoginDTO;
import eu.nicosworld.authentication.authentication.model.UserRegistrationDTO;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class AuthControllerE2ETest extends AbstractE2ETest {

    // ---------------------------
    // Helper DRY pour créer/login un utilisateur avec assertions
    // ---------------------------
    static class UserTestHelper {

        static void registerUser(String email, String password) {
            given()
                    .contentType(ContentType.JSON)
                    .body(new UserRegistrationDTO() {{
                        setEmail(email);
                        setPassword(password);
                    }})
                    .when()
                    .post("/auth/register")
                    .then()
                    .statusCode(201);
        }

        static Response loginUser(String email, String password) {
            UserLoginDTO loginDto = new UserLoginDTO();
            loginDto.setEmail(email);
            loginDto.setPassword(password);

            return given()
                    .contentType(ContentType.JSON)
                    .body(loginDto)
                    .when()
                    .post("/auth/login")
                    .then()
                    .statusCode(200)
                    .extract()
                    .response();
        }

        static String registerAndLoginGetRefreshCookie(String email, String password) {
            registerUser(email, password);
            Response loginResponse = loginUser(email, password);
            return loginResponse.getHeader("Set-Cookie");
        }

        static void assertRegisterFails(String email, String password, int expectedStatus, String expectedError) {
            given()
                    .contentType(ContentType.JSON)
                    .body(new UserRegistrationDTO() {{
                        setEmail(email);
                        setPassword(password);
                    }})
                    .when()
                    .post("/auth/register")
                    .then()
                    .statusCode(expectedStatus)
                    .contentType(ContentType.JSON)
                    .body("error", equalTo(expectedError));
        }

        static void assertLoginFails(String email, String password, int expectedStatus, String expectedError) {
            UserLoginDTO loginDto = new UserLoginDTO();
            loginDto.setEmail(email);
            loginDto.setPassword(password);

            given()
                    .contentType(ContentType.JSON)
                    .body(loginDto)
                    .when()
                    .post("/auth/login")
                    .then()
                    .statusCode(expectedStatus)
                    .contentType(ContentType.JSON)
                    .body("error", equalTo(expectedError));
        }
    }

    // ---------------------------
    // Register Tests
    // ---------------------------
    @Nested
    @DisplayName("Register Tests")
    class RegisterTests {

        @Test
        @DisplayName("POST /auth/register - crée un nouvel utilisateur")
        void register_shouldCreateUser() {
            UserTestHelper.registerUser("newuser@example.com", "password123");
        }

        @Test
        @DisplayName("POST /auth/register - échoue si email déjà utilisé")
        void register_shouldFailIfEmailAlreadyUsed() {
            String email = "existing@example.com";
            String password = "password123";

            UserTestHelper.registerUser(email, password);

            // Test paramétrable
            UserTestHelper.assertRegisterFails(email, password, 400, "Cet email est déjà utilisé.");
        }
    }

    // ---------------------------
    // Login Tests
    // ---------------------------
    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @Test
        @DisplayName("POST /auth/login - renvoie accessToken et refreshToken")
        void login_shouldReturnAccessTokenAndRefreshCookie() {
            String email = "loginuser@example.com";
            String password = "password123";

            String refreshCookie = UserTestHelper.registerAndLoginGetRefreshCookie(email, password);

            // Vérifier que cookie contient refreshToken
            assert refreshCookie.contains("refreshToken=");
        }

        @Test
        @DisplayName("POST /auth/login - échoue si mauvais mot de passe")
        void login_shouldFailWithBadCredentials() {
            String email = "user@example.com";
            String password = "password123";

            UserTestHelper.registerUser(email, password);

            UserTestHelper.assertLoginFails(email, "wrongpassword", 401, "Email ou mot de passe incorrect.");
        }

        @Test
        @DisplayName("POST /auth/login - échoue si email inexistant")
        void login_shouldFailWithUnknownEmail() {
            UserTestHelper.assertLoginFails("unknown@example.com", "any", 401, "Email ou mot de passe incorrect.");
        }
    }

    // ---------------------------
    // Refresh Token Tests
    // ---------------------------
    @Nested
    @DisplayName("Refresh Token Tests")
    class RefreshTests {

        @Test
        @DisplayName("POST /auth/refresh - avec refresh token valide")
        void refresh_shouldReturnNewAccessToken() {
            String cookie = UserTestHelper.registerAndLoginGetRefreshCookie("refreshuser@example.com", "password123");

            given()
                    .contentType(ContentType.JSON)
                    .header("Cookie", cookie)
                    .when()
                    .post("/auth/refresh")
                    .then()
                    .statusCode(200)
                    .body("accessToken", notNullValue());
        }

        @Test
        @DisplayName("POST /auth/refresh - sans cookie renvoie 401")
        void refresh_withoutCookie_shouldReturnUnauthorized() {
            given()
                    .contentType(ContentType.JSON)
                    .when()
                    .post("/auth/refresh")
                    .then()
                    .statusCode(401);
        }

        @Test
        @DisplayName("POST /auth/refresh - avec cookie invalide renvoie 401")
        void refresh_withInvalidCookie_shouldReturnUnauthorized() {
            given()
                    .contentType(ContentType.JSON)
                    .header("Cookie", "refreshToken=invalid-token")
                    .when()
                    .post("/auth/refresh")
                    .then()
                    .statusCode(401);
        }
    }
}
