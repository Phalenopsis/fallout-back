package eu.nicosworld.falloutback.infrastructure.web.dto.character;

import eu.nicosworld.falloutback.infrastructure.persistence.entity.character.Special;

public record SpecialDto(int strength,
                         int perception,
                         int endurance,
                         int charisma,
                         int intelligence,
                         int agility,
                         int luck) {
    public static SpecialDto mapFromEntity(Special special) {
        return new SpecialDto(
                special.getStrength(),
                special.getPerception(),
                special.getEndurance(),
                special.getCharisma(),
                special.getIntelligence(),
                special.getAgility(),
                special.getLuck()
        );
    }
}
