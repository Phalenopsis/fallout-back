package eu.nicosworld.falloutback.infrastructure.event;

import eu.nicosworld.falloutback.authentication.model.User;
import org.springframework.context.ApplicationEvent;

public class UserCreatedEvent extends ApplicationEvent {

    private final User user;

    public UserCreatedEvent(Object source, User user) {
        super(source);
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
