package eu.nicosworld.falloutback.infrastructure.persistence.entity.campaign;

import eu.nicosworld.falloutback.domain.invitation.InvitationStatus;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.character.Character;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "campaign_character",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_campaign_character_character",
            columnNames = "character_id"
        )
    }
)
public class CampaignCharacter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

    @ManyToOne(optional = false)
    @JoinColumn(name = "character_id")
    private Character character;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvitationStatus status = InvitationStatus.PENDING;

    private LocalDateTime invitedAt = LocalDateTime.now();

    public CampaignCharacter() {}

    public CampaignCharacter(Campaign campaign, Character character) {
        this.campaign = campaign;
        this.character = character;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    public Character getCharacter() {
        return character;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }

    public InvitationStatus getStatus() {
        return status;
    }

    public void setStatus(InvitationStatus status) {
        this.status = status;
    }

    public LocalDateTime getInvitedAt() {
        return invitedAt;
    }

    public void setInvitedAt(LocalDateTime invitedAt) {
        this.invitedAt = invitedAt;
    }
}