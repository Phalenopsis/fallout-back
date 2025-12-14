package eu.nicosworld.falloutback.infrastructure.persistence.repository.character;

import eu.nicosworld.falloutback.infrastructure.persistence.entity.character.Character;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CharacterRepository extends JpaRepository<Character, Long> {
}
