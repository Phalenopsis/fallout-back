package eu.nicosworld.falloutback.infrastructure.persistence.entity.character;

import eu.nicosworld.falloutback.infrastructure.web.dto.character.SpecialDto;
import jakarta.persistence.*;

@Entity
public class Special {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Character character;

    private int strength;
    private int perception;
    private int endurance;
    private int charisma;
    private int intelligence;
    private int agility;
    private int luck;

    public Special() {

    }

    public Special(SpecialDto dto) {
        strength = dto.strength();
        perception = dto.perception();
        endurance = dto.endurance();
        charisma = dto.charisma();
        intelligence = dto.intelligence();
        agility = dto.agility();
        luck = dto.luck();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Character getCharacter() {
        return character;
    }

    public void setCharacter(Character character) {
        this.character = character;
        if (character.getSpecial() != this) {
            character.setSpecial(this);
        }
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getPerception() {
        return perception;
    }

    public void setPerception(int perception) {
        this.perception = perception;
    }

    public int getEndurance() {
        return endurance;
    }

    public void setEndurance(int endurance) {
        this.endurance = endurance;
    }

    public int getCharisma() {
        return charisma;
    }

    public void setCharisma(int charisma) {
        this.charisma = charisma;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
    }

    public int getAgility() {
        return agility;
    }

    public void setAgility(int agility) {
        this.agility = agility;
    }

    public int getLuck() {
        return luck;
    }

    public void setLuck(int luck) {
        this.luck = luck;
    }
}
