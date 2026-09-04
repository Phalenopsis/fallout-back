package eu.nicosworld.falloutback.infrastructure.persistence.repository.namegenerator;

import eu.nicosworld.falloutback.infrastructure.persistence.entity.namegenerator.SuperMutantName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SuperMutantNameRepository extends JpaRepository<SuperMutantName, Long> {

    @Query(value = "SELECT s.name FROM super_mutant_name s ORDER BY RAND() LIMIT :count", nativeQuery = true)
    List<String> findRandomNames(@Param("count") int count);
}
