package eu.nicosworld.falloutback.domain.character.origin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
class OriginServiceTest {

    private OriginService originService;

    @BeforeEach
    void setUp() {
        // Instanciation directe sans Spring (adapte si OriginService a des dépendances comme ObjectMapper)
        originService = new OriginService(new ObjectMapper());
        originService.init();
    }

    @Test
    void shouldLoadOriginsFromJson() {
        List<Origin> origines = originService.getAllOrigins();

        assertNotNull(origines);
        assertFalse(origines.isEmpty());

        Origin atom = origines.stream()
            .filter(o -> "ChildOfAtom".equals(o.getName()))
            .findFirst()
            .orElseThrow();

        assertEquals("Enfant d'Atome", atom.getNom());
        assertNotNull(atom.getHistoire());
        assertTrue(atom.getHistoire().size() > 5);

        assertTrue(atom.getTrait().contains("radiationSponge"));
    }
}