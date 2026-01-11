package eu.nicosworld.falloutback.infrastructure.persistence.entity.character;

import eu.nicosworld.falloutback.domain.character.CreationStatus;
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

    @OneToOne(mappedBy = "character", cascade = CascadeType.ALL, optional = false)
    private Skills skills;

    private String originName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CreationStatus creationStatus = CreationStatus.DRAFT;

    public Character() {

    }

    public Character(CharacterDto characterDto, DomainUser domainUser) {
        user = domainUser;
        name = characterDto.name();
        originName = characterDto.originName();
        creationStatus = characterDto.creationStatus();
        if (characterDto.special() != null) {
            this.special = new Special(characterDto.special());
        } else {
            this.special = new Special();
        }
        if (characterDto.skills() != null) {
            this.skills = new Skills(characterDto.skills());
        } else {
            this.skills = new Skills();
        }
    }

    public void update(CharacterDto characterDto) {
        if (characterDto.name() != null) {
            this.name = characterDto.name();
        }
        if (characterDto.originName() != null) {
            this.originName = characterDto.originName();
        }
        if (characterDto.creationStatus() != null) {
            this.creationStatus = characterDto.creationStatus();
        }
        if (characterDto.special() != null) {
            if (this.special == null) {
                this.special = new Special(characterDto.special());
            } else {
                this.special.update(characterDto.special());
            }
        }
        if(characterDto.skills() != null) {
            if (this.skills == null) {
                this.skills = new Skills(characterDto.skills());
            } else {
                this.skills.update(characterDto.skills());
            }
        }
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

    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }

    public CreationStatus getCreationStatus() {
        return creationStatus;
    }

    public void setCreationStatus(CreationStatus creationStatus) {
        this.creationStatus = creationStatus;
    }

    public Skills getSkills() {
        return skills;
    }

    public void setSkills(Skills skills) {
        this.skills = skills;
        if(skills.getCharacter() != this) {
            skills.setCharacter(this);
        }
    }
}
