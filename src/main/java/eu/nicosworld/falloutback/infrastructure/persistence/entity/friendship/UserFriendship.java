package eu.nicosworld.falloutback.infrastructure.persistence.entity.friendship;

import eu.nicosworld.falloutback.domain.invitation.InvitationStatus;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.DomainUser;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_friendship")
public class UserFriendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "requester_id")
    private DomainUser requester;

    @ManyToOne(optional = false)
    @JoinColumn(name = "addressee_id")
    private DomainUser addressee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvitationStatus status = InvitationStatus.PENDING;

    private LocalDateTime createdAt = LocalDateTime.now();

    public UserFriendship() {}

    public UserFriendship(DomainUser requester, DomainUser addressee) {
        this.requester = requester;
        this.addressee = addressee;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DomainUser getRequester() {
        return requester;
    }

    public void setRequester(DomainUser requester) {
        this.requester = requester;
    }

    public DomainUser getAddressee() {
        return addressee;
    }

    public void setAddressee(DomainUser addressee) {
        this.addressee = addressee;
    }

    public InvitationStatus getStatus() {
        return status;
    }

    public void setStatus(InvitationStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}