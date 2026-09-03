package eu.nicosworld.falloutback.infrastructure.persistence.repository.note;

import eu.nicosworld.falloutback.domain.note.NoteType;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.campaign.Campaign;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.character.Character;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.note.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    @Query("""
    SELECT n
    FROM Note n
    WHERE n.type = :type
      AND (
          n.character = :character
          OR EXISTS (
              SELECT s
              FROM NoteShare s
              WHERE s.note = n
                AND s.character = :character
          )
      )
    """)
    List<Note> findAllAccessibleByCharacterAndType(
        @Param("character") Character character,
        @Param("type") NoteType type
    );

    @Query("""
    SELECT n
    FROM Note n
    WHERE (
          n.character = :character
          OR EXISTS (
              SELECT s
              FROM NoteShare s
              WHERE s.note = n
                AND s.character = :character
          )
      )
    """)
    List<Note> findAllAccessibleByCharacter(
        @Param("character") Character character
    );

    @Query("""
    SELECT n
    FROM Note n
    WHERE n.type = :type
      AND (
          n.campaign = :campaign
          OR EXISTS (
              SELECT s
              FROM NoteShare s
              WHERE s.note = n
                AND s.campaign = :campaign
          )
      )
    """)
    List<Note> findAllAccessibleByCampaignAndType(
        @Param("campaign") Campaign campaign,
        @Param("type") NoteType type
    );

    @Query("""
    SELECT n
    FROM Note n
    WHERE (
          n.campaign = :campaign
          OR EXISTS (
              SELECT s
              FROM NoteShare s
              WHERE s.note = n
                AND s.campaign = :campaign
          )
      )
    """)
    List<Note> findAllAccessibleByCampaign(
        @Param("campaign") Campaign campaign
    );
}