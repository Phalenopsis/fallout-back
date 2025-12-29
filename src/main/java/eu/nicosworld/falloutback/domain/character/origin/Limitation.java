package eu.nicosworld.falloutback.domain.character.origin;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.HashMap;
import java.util.Map;

public class Limitation {

    private Map<String, Integer> values = new HashMap<>();

    @JsonAnySetter
    public void add(String key, Integer value) {
        values.put(key, value);
    }

    @JsonAnyGetter
    public Map<String, Integer> getValues() {
        return values;
    }
}


