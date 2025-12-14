package eu.nicosworld.falloutback.infrastructure.persistence.entity.character;

import eu.nicosworld.falloutback.infrastructure.persistence.entity.DomainUser;
import eu.nicosworld.falloutback.infrastructure.web.dto.character.CharacterDto;
import jakarta.persistence.*;

@Entity
@Table(name = "characters")
public class Character {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="user_id")
    private DomainUser user;

    private String name;

    @OneToOne(mappedBy = "character", cascade = CascadeType.ALL, optional = false)
    private Special special;

    public Character() {

    }

    public Character(CharacterDto characterDto, DomainUser domainUser) {
        user = domainUser;
        name = characterDto.name();
    }

    /*
     *
     * GETTER / SETTER
     *
     * */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DomainUser getUser() {
        return user;
    }

    public void setUser(DomainUser user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Special getSpecial() {
        return special;
    }

    public void setSpecial(Special special) {
        this.special = special;
        if (special.getCharacter() != this) {
            special.setCharacter(this);
        }
    }
}
