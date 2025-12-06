package eu.nicosworld.authentication.authentication;


import com.fasterxml.jackson.databind.ObjectMapper;
import eu.nicosworld.authentication.config.security.JwtService;
import eu.nicosworld.authentication.authentication.model.UserLoginDTO;
import eu.nicosworld.authentication.authentication.model.UserRegistrationDTO;
import eu.nicosworld.authentication.authentication.model.User;
import eu.nicosworld.authentication.exception.EmailAlreadyUsed;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AuthenticationService authenticationService;

    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setup() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRoles(Set.of("ROLE_USER"));
    }

    @Test
    @DisplayName("POST /auth/register - doit créer un nouvel utilisateur")
    void register_shouldCreateUser() throws Exception {
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setEmail("newuser@example.com");
        dto.setPassword("password123");

        Mockito.when(userService.registerUser(anyString(), anyString(), anySet()))
                .thenReturn(testUser);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("POST /auth/register - doit échouer si l'email est déjà utilisé")
    void register_shouldFail_whenEmailAlreadyUsed() throws Exception {
        // Arrange
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setEmail("used@example.com");
        dto.setPassword("password123");

        // Simule un cas où le service lève l'exception
        Mockito.when(userService.registerUser(anyString(), anyString(), anySet()))
                .thenThrow(new EmailAlreadyUsed());

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Cet email est déjà utilisé."));
    }


    @Test
    @DisplayName("POST /auth/login - doit renvoyer un accessToken et un cookie refreshToken")
    void login_shouldReturnAccessTokenAndCookie() throws Exception {
        UserLoginDTO dto = new UserLoginDTO();
        dto.setEmail("test@example.com");
        dto.setPassword("password123");

        Mockito.when(authenticationService.authenticate(anyString(), anyString()))
                .thenReturn("access-token-123");

        Mockito.when(userService.loadUserByUsername(anyString()))
                .thenReturn(testUser);

        Mockito.when(jwtService.generateRefreshToken(any(User.class)))
                .thenReturn("refresh-token-xyz");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().exists("Set-Cookie"))
                .andExpect(jsonPath("$.accessToken").value("access-token-123"));
    }

    @Test
    @DisplayName("POST /auth/refresh - doit renvoyer un nouveau accessToken si le refreshToken est valide")
    void refresh_shouldReturnNewAccessToken() throws Exception {
        Cookie cookie = new Cookie("refreshToken", "refresh-token-xyz");
        Claims claims = new DefaultClaims();
        claims.setSubject("test@example.com");

        String refreshToken = "refresh-token-xyz";

        Mockito.when(jwtService.validateJwtToken("refresh-token-xyz"))
                .thenReturn(true);

        Mockito.when(jwtService.extractClaims(refreshToken)).thenReturn(claims);

        Mockito.when(userService.loadUserByUsername("test@example.com"))
                .thenReturn(testUser);

        Mockito.when(jwtService.generateToken(any(User.class)))
                .thenReturn("new-access-token");

        Mockito.when(jwtService.generateRefreshToken(any(User.class)))
                .thenReturn("new-refresh-token");

        mockMvc.perform(post("/auth/refresh").cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(header().exists("Set-Cookie"))
                .andExpect(jsonPath("$.accessToken").value("new-access-token"));
    }

    @Test
    @DisplayName("POST /auth/refresh - doit renvoyer 401 si le cookie est manquant")
    void refresh_shouldReturnUnauthorizedIfNoCookie() throws Exception {
        mockMvc.perform(post("/auth/refresh"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /auth/refresh - doit renvoyer 401 si le token est invalide")
    void refresh_shouldReturnUnauthorizedIfInvalidToken() throws Exception {
        Cookie cookie = new Cookie("refreshToken", "invalid");

        Mockito.when(jwtService.validateJwtToken("invalid"))
                .thenReturn(false);

        mockMvc.perform(post("/auth/refresh").cookie(cookie))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /auth/logout - doit supprimer le cookie refreshToken")
    void logout_shouldDeleteCookie() throws Exception {
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(header().string("Set-Cookie", containsString("Max-Age=0")));
    }
}
