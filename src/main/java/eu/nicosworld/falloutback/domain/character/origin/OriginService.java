package eu.nicosworld.falloutback.domain.character.origin;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class OriginService {

    private final ObjectMapper objectMapper;
    private List<Origin> origins;

    public OriginService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        ClassPathResource resource = new ClassPathResource("origins.json");

        if (!resource.exists()) {
            throw new IllegalStateException("Le fichier origins.json est introuvable dans src/main/resources !");
        }

        try (InputStream is = resource.getInputStream()) {
            origins = List.of(objectMapper.readValue(is, Origin[].class));
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