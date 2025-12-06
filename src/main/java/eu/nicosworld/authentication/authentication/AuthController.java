package eu.nicosworld.authentication.authentication;

import eu.nicosworld.authentication.authentication.model.User;
import eu.nicosworld.authentication.authentication.model.UserLoginDTO;
import eu.nicosworld.authentication.authentication.model.UserRegistrationDTO;
import eu.nicosworld.authentication.authentication.model.UserResponseDTO;
import eu.nicosworld.authentication.config.security.JwtService;

import eu.nicosworld.authentication.exception.EmailAlreadyUsed;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    public AuthController(UserService userService,
                          AuthenticationService authenticationService,
                          JwtService jwtService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody UserRegistrationDTO userRegistrationDTO) throws EmailAlreadyUsed {
        User registeredUser = userService.registerUser(
                userRegistrationDTO.getEmail(),
                userRegistrationDTO.getPassword(),
                Set.of("ROLE_USER")
        );

        UserResponseDTO responseDTO = new UserResponseDTO(
                registeredUser.getId(),
                registeredUser.getEmail()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> authenticate(@RequestBody UserLoginDTO userLoginDTO) {
        String accessToken = authenticationService.authenticate(
                userLoginDTO.getEmail(),
                userLoginDTO.getPassword()
        );

        User user = userService.loadUserByUsername(userLoginDTO.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user);

        ResponseCookie cookie = getHttpOnlyCookie(refreshToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("accessToken", accessToken, "user", user.getEmail()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String refreshToken = Arrays.stream(cookies)
                .filter(c -> "refreshToken".equals(c.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);

        if (refreshToken == null || !jwtService.validateJwtToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = jwtService.extractClaims(refreshToken).getSubject();
        User user = userService.loadUserByUsername(username);

        String newRefreshToken = jwtService.generateRefreshToken(user);
        ResponseCookie cookie = getHttpOnlyCookie(newRefreshToken);

        String newAccessToken = jwtService.generateToken(user);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("accessToken", newAccessToken));
    }

    private static ResponseCookie getHttpOnlyCookie(String newRefreshToken) {
        return getHttpOnlyCookie(newRefreshToken, Duration.ofDays(7));
    }

    private static ResponseCookie getHttpOnlyCookie(String newRefreshToken, Duration maxAge) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", newRefreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(maxAge)
                .build();
        return cookie;
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie deleteCookie = getEmptyHttpOnlyCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();
    }

    private static ResponseCookie getEmptyHttpOnlyCookie() {
        return getHttpOnlyCookie("", Duration.ofDays(0));
    }
}
