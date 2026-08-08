package eu.nicosworld.falloutback.infrastructure.persistence.repository.note;

import eu.nicosworld.falloutback.domain.note.NoteType;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.DomainUser;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.note.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    @Query("SELECT DISTINCT n FROM Note n " +
        "LEFT JOIN n.shares s " +
        "WHERE n.id = :id AND (n.author = :user OR s.sharedWith = :user)")
    Optional<Note> findByIdAndUser(@Param("id") Long id, @Param("user") DomainUser user);

    @Query("SELECT DISTINCT n FROM Note n " +
        "LEFT JOIN n.shares s " +
        "WHERE n.character.id = :characterId AND (n.author = :user OR s.sharedWith = :user)")
    List<Note> findAllByCharacterAndUser(@Param("characterId") Long characterId, @Param("user") DomainUser user);

    @Query("SELECT DISTINCT n FROM Note n " +
        "LEFT JOIN n.shares s " +
        "WHERE n.character.id = :characterId AND n.type = :type AND (n.author = :user OR s.sharedWith = :user)")
    List<Note> findAllByCharacterAndTypeAndUser(@Param("characterId") Long characterId, @Param("type") NoteType type, @Param("user") DomainUser user);

    @Query("SELECT DISTINCT n FROM Note n " +
        "LEFT JOIN n.shares s " +
        "WHERE n.campaign.id = :campaignId AND (n.author = :user OR s.sharedWith = :user)")
    List<Note> findAllByCampaignAndUser(@Param("campaignId") Long campaignId, @Param("user") DomainUser user);

    @Query("SELECT DISTINCT n FROM Note n " +
        "LEFT JOIN n.shares s " +
        "WHERE n.campaign.id = :campaignId AND n.type = :type AND (n.author = :user OR s.sharedWith = :user)")
    List<Note> findAllByCampaignAndTypeAndUser(@Param("campaignId") Long campaignId, @Param("type") NoteType type, @Param("user") DomainUser user);
}