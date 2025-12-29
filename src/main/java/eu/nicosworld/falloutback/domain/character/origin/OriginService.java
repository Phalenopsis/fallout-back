package eu.nicosworld.falloutback.domain.character.origin;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class OriginService {

    private List<Origin> origins;

    @PostConstruct
    public void init() {
        try (InputStream is = getClass().getResourceAsStream("/origins.json")) {
            ObjectMapper mapper = new ObjectMapper();
            origins = List.of(mapper.readValue(is, Origin[].class));
        } catch (IOException e) {
            throw new IllegalStateException("Impossible de charger les origines", e);
        }
    }

    public List<Origin> getAllOrigins() {
        return origins;
    }

    public Origin getOriginByName(String name) {
        return origins.stream()
                .filter(o -> o.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}

