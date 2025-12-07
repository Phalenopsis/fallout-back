package eu.nicosworld.falloutback.infrastructure.listener;

import eu.nicosworld.falloutback.authentication.model.User;
import eu.nicosworld.falloutback.infrastructure.event.UserCreatedEvent;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.AuthenticatedUser;
import eu.nicosworld.falloutback.infrastructure.persistence.repository.AuthenticatedUserRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUserCreator {

    private final AuthenticatedUserRepository authenticatedUserRepository;

    public AuthenticatedUserCreator(AuthenticatedUserRepository authenticatedUserRepository) {
        this.authenticatedUserRepository = authenticatedUserRepository;
    }

    @EventListener
    public void handleUserCreated(UserCreatedEvent event) {
        User user = event.getUser();
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(user);
        authenticatedUserRepository.save(authenticatedUser);
    }
}
