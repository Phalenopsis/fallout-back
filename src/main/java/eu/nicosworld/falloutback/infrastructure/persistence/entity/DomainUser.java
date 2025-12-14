package eu.nicosworld.falloutback.infrastructure.persistence.entity;

import eu.nicosworld.falloutback.authentication.model.User;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.character.Character;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class DomainUser {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    private User user;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Character> characterList;

    public DomainUser() {}

    /*
    *
    * UTILITY METHODS
    *
    * */

    public void addCharacter(Character c) {
        characterList.add(c);
        c.setUser(this);
    }

    public void removeCharacter(Character c) {
        characterList.remove(c);
        c.setUser(null);
    }

    /*
    *
    * GETTER / SETTER
    *
    * */

    public DomainUser(User user) {
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

    public List<Character> getCharacterList() {
        return characterList;
    }

    public void setCharacterList(List<Character> characterList) {
        this.characterList = characterList;
    }
}

