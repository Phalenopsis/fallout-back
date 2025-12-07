package eu.nicosworld.falloutback.infrastructure.persistence.entity;

import eu.nicosworld.falloutback.authentication.model.User;
import jakarta.persistence.*;

@Entity
public class AuthenticatedUser {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    private User user;

    public AuthenticatedUser() {}

    public AuthenticatedUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}

