package eu.nicosworld.falloutback.infrastructure.persistence.repository;

import eu.nicosworld.falloutback.infrastructure.persistence.entity.AuthenticatedUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthenticatedUserRepository extends JpaRepository<AuthenticatedUser, Long> {}
