package eu.nicosworld.falloutback.infrastructure.listener;

import eu.nicosworld.falloutback.authentication.model.User;
import eu.nicosworld.falloutback.infrastructure.event.UserCreatedEvent;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.DomainUser;
import eu.nicosworld.falloutback.infrastructure.persistence.repository.DomainUserRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class DomainUserCreator {

    private final DomainUserRepository domainUserRepository;

    public DomainUserCreator(DomainUserRepository domainUserRepository) {
        this.domainUserRepository = domainUserRepository;
    }

    @EventListener
    public void handleUserCreated(UserCreatedEvent event) {
        User user = event.getUser();
        DomainUser domainUser = new DomainUser(user);
        domainUserRepository.save(domainUser);
    }
}
