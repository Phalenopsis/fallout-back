package eu.nicosworld.falloutback.infrastructure.persistence.repository.namegenerator;

import eu.nicosworld.falloutback.infrastructure.persistence.entity.namegenerator.LastName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LastNameRepository extends JpaRepository<LastName, Long> {

    @Query(value = "SELECT l.name FROM last_name l ORDER BY RAND() LIMIT :count", nativeQuery = true)
    List<String> findRandomLastNames(@Param("count") int count);
}
