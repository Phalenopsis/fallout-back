package eu.nicosworld.falloutback.infrastructure.persistence.repository.note;

import eu.nicosworld.falloutback.infrastructure.persistence.entity.note.NoteShare;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteShareRepository extends JpaRepository<NoteShare, Long> {
}
