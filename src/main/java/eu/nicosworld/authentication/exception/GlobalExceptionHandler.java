package eu.nicosworld.authentication.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of("error", "Email ou mot de passe incorrect."));
    }

    @ExceptionHandler(EmailAlreadyUsed.class)
    public ResponseEntity<Map<String, String>> handleBadCredentials(EmailAlreadyUsed ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of("error", ex.getMessage()));
    }
}
