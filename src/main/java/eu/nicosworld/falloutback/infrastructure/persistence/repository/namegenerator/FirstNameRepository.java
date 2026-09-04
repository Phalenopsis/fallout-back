package eu.nicosworld.falloutback.infrastructure.persistence.repository.namegenerator;

import eu.nicosworld.falloutback.infrastructure.persistence.entity.namegenerator.FirstName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FirstNameRepository extends JpaRepository<FirstName, Long> {

    // Récupère les prénoms pour un genre spécifique + les prénoms neutres
    @Query(value = "SELECT f.name FROM first_name f WHERE f.gender = :gender OR f.gender = 'NEUTRAL' ORDER BY RAND() LIMIT :count", nativeQuery = true)
    List<String> findRandomFirstNamesIncludingNeutral(@Param("gender") String gender, @Param("count") int count);

    // Récupère des prénoms sans distinction de genre (Homme + Femme + Neutre)
    @Query(value = "SELECT f.name FROM first_name f ORDER BY RAND() LIMIT :count", nativeQuery = true)
    List<String> findRandomFirstNamesAnyGender(@Param("count") int count);
}
