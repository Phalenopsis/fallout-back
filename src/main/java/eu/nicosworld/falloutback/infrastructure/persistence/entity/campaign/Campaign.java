package eu.nicosworld.falloutback.infrastructure.persistence.entity.campaign;

import eu.nicosworld.falloutback.infrastructure.persistence.entity.DomainUser;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "campaign")
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "game_master_id")
    private DomainUser gameMaster;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CampaignCharacter> members = new ArrayList<>();

    public Campaign() {}

    public Campaign(String name, DomainUser gameMaster) {
        this.name = name;
        this.gameMaster = gameMaster;
    }

    public void addCharacter(CampaignCharacter member) {
        members.add(member);
        member.setCampaign(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DomainUser getGameMaster() {
        return gameMaster;
    }

    public void setGameMaster(DomainUser gameMaster) {
        this.gameMaster = gameMaster;
    }

    public List<CampaignCharacter> getMembers() {
        return members;
    }

    public void setMembers(List<CampaignCharacter> members) {
        this.members = members;
    }
}