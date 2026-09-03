package eu.nicosworld.falloutback.infrastructure.persistence.entity.note;

import eu.nicosworld.falloutback.domain.note.NoteType;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.DomainUser;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.campaign.Campaign;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.character.Character;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "notes")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String directory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NoteType type = NoteType.FREE_NOTE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id")
    private Character character;

    @OneToMany(
        mappedBy = "note",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<NoteShare> shares = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Note() {}

    public Note(
        String title,
        String content,
        NoteType type,
        Campaign campaign,
        Character character
    ) {
        this(title, content, type, campaign, character, null);
    }

    public Note(
        String title,
        String content,
        NoteType type,
        Campaign campaign,
        Character character,
        String directory
    ) {
        if (campaign == null && character == null) {
            throw new IllegalArgumentException(
                "Une note doit être rattachée à une campagne ou à un personnage."
            );
        }

        if (campaign != null && character != null) {
            throw new IllegalArgumentException(
                "Une note ne peut pas être rattachée à une campagne et à un personnage."
            );
        }

        this.title = title;
        this.content = content;
        this.type = type != null ? type : NoteType.FREE_NOTE;
        this.campaign = campaign;
        this.character = character;
        this.directory = directory;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public NoteType getType() {
        return type;
    }

    public void setType(NoteType type) {
        this.type = type;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public Character getCharacter() {
        return character;
    }

    public List<NoteShare> getShares() {
        return shares;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void shareWithCharacter(Character character) {
        shares.add(NoteShare.forCharacter(this, character));
    }

    public void shareWithCampaign(Campaign campaign) {
        shares.add(NoteShare.forCampaign(this, campaign));
    }

    public void clearShares() {
        shares.clear();
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public void addShare(NoteShare share) {
        shares.add(share);
    }

    public void removeShare(NoteShare share) {
        shares.remove(share);
    }
}