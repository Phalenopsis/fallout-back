package eu.nicosworld.falloutback.infrastructure.persistence.repository;

import eu.nicosworld.falloutback.infrastructure.persistence.entity.DomainUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DomainUserRepository extends JpaRepository<DomainUser, Long> {}
