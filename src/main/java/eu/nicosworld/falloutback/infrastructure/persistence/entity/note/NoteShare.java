package eu.nicosworld.falloutback.infrastructure.persistence.entity.note;

import eu.nicosworld.falloutback.infrastructure.persistence.entity.DomainUser;
import jakarta.persistence.*;

@Entity
@Table(name = "note_shares")
public class NoteShare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id", nullable = false)
    private Note note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shared_with_user_id", nullable = false)
    private DomainUser sharedWith;

    public NoteShare() {}

    public NoteShare(Note note, DomainUser sharedWith) {
        this.note = note;
        this.sharedWith = sharedWith;
    }

    public Long getId() { return id; }
    public Note getNote() { return note; }
    public DomainUser getSharedWith() { return sharedWith; }
}