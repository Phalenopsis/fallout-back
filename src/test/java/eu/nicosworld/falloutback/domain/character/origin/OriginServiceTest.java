package eu.nicosworld.falloutback.domain.character.origin;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OriginServiceTest {

    @Autowired
    private OriginService originService;

    @Test
    void shouldLoadOriginsFromJson() {
        List<Origin> origines = originService.getAllOrigins();

        assertNotNull(origines);
        assertFalse(origines.isEmpty());

        Origin atom = origines.stream()
                .filter(o -> o.getName().equals("ChildOfAtom"))
                .findFirst()
                .orElseThrow();

        assertEquals("Enfant d'Atome", atom.getNom());
        assertNotNull(atom.getHistoire());
        assertTrue(atom.getHistoire().size() > 5);

        assertTrue(atom.getTrait().contains("radiationSponge"));
    }
}
