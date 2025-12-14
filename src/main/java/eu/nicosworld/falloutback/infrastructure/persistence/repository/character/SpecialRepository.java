package eu.nicosworld.falloutback.infrastructure.persistence.repository.character;

import eu.nicosworld.falloutback.infrastructure.persistence.entity.character.Special;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecialRepository extends JpaRepository<Special, Long> {
}
