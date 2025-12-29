package eu.nicosworld.falloutback.domain.character.origin;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class TraitService {
    private List<TraitDescription> traits;

    @PostConstruct
    public void init() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = getClass().getResourceAsStream("/traits.json");
        traits = Arrays.asList(mapper.readValue(is, TraitDescription[].class));
    }

    public List<TraitDescription> getTraits() {
        return traits;
    }

    public TraitDescription getTraitByName(String name) {
        return traits.stream()
                .filter(t -> t.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
