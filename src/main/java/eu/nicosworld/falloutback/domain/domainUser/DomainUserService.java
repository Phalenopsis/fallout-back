package eu.nicosworld.falloutback.domain.domainUser;

import eu.nicosworld.falloutback.authentication.UserRepository;
import eu.nicosworld.falloutback.authentication.model.User;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.DomainUser;
import eu.nicosworld.falloutback.infrastructure.persistence.repository.DomainUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class DomainUserService {
    private final DomainUserRepository domainUserRepository;
    private final UserRepository userRepository;

    public DomainUserService(
            DomainUserRepository domainUserRepository, UserRepository userRepository) {
        this.domainUserRepository = domainUserRepository;
        this.userRepository = userRepository;
    }

    public DomainUser findByUser(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        return domainUserRepository.findById(user.getId()).orElseThrow();
    }
}
