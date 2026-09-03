package eu.nicosworld.falloutback.infrastructure.persistence.entity.note;

import eu.nicosworld.falloutback.infrastructure.persistence.entity.campaign.Campaign;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.character.Character;
import jakarta.persistence.*;

@Entity
@Table(name = "note_shares")
public class NoteShare {
    public static NoteShare forCharacter(Note note, Character character) {
        return new NoteShare(note, null, character);
    }

    public static NoteShare forCampaign(Note note, Campaign campaign) {
        return new NoteShare(note, campaign, null);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "note_id", nullable = false)
    private Note note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id")
    private Character character;

    @Column(name = "is_read", nullable = false)
    private boolean read = false;

    public NoteShare() {}

    public NoteShare(
        Note note,
        Campaign campaign,
        Character character
    ) {
        if (campaign == null && character == null) {
            throw new IllegalArgumentException(
                "Un partage doit cibler une campagne ou un personnage."
            );
        }

        if (campaign != null && character != null) {
            throw new IllegalArgumentException(
                "Un partage ne peut pas cibler une campagne et un personnage."
            );
        }

        this.note = note;
        this.campaign = campaign;
        this.character = character;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Note getNote() {
        return note;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public Character getCharacter() {
        return character;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}