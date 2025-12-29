package eu.nicosworld.falloutback.domain.character.origin;

import java.util.List;

public class TraitDescription {
    private String name;
    private String nom;
    private List<String> description;

    public String getName() {
        return name;
    }

    public List<String> getDescription() {
        return description;
    }

    public String getNom() {
        return nom;
    }
}
