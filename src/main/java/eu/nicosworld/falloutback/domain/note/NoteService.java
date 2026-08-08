package eu.nicosworld.falloutback.domain.note;

import eu.nicosworld.falloutback.authentication.UserRepository;
import eu.nicosworld.falloutback.authentication.model.User;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.DomainUser;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.campaign.Campaign;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.character.Character;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.note.Note;
import eu.nicosworld.falloutback.infrastructure.persistence.repository.DomainUserRepository;
import eu.nicosworld.falloutback.infrastructure.persistence.repository.campaign.CampaignRepository;
import eu.nicosworld.falloutback.infrastructure.persistence.repository.character.CharacterRepository;
import eu.nicosworld.falloutback.infrastructure.persistence.repository.note.NoteRepository;
import eu.nicosworld.falloutback.infrastructure.web.dto.note.CreateNoteDto;
import eu.nicosworld.falloutback.infrastructure.web.dto.note.NoteResponseDto;
import eu.nicosworld.falloutback.infrastructure.web.dto.note.UpdateNoteDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final DomainUserRepository domainUserRepository;
    private final UserRepository userRepository;
    private final CampaignRepository campaignRepository;
    private final CharacterRepository characterRepository;

    public NoteService(NoteRepository noteRepository,
                       DomainUserRepository domainUserRepository,
                       UserRepository userRepository,
                       CampaignRepository campaignRepository,
                       CharacterRepository characterRepository) {
        this.noteRepository = noteRepository;
        this.domainUserRepository = domainUserRepository;
        this.userRepository = userRepository;
        this.campaignRepository = campaignRepository;
        this.characterRepository = characterRepository;
    }

    @Transactional
    public NoteResponseDto createNote(String authorEmail, CreateNoteDto dto) {
        if (dto.campaignId() == null && dto.characterId() == null) {
            throw new IllegalArgumentException("Une note doit être rattachée à une campagne ou à un personnage.");
        }

        DomainUser author = getDomainUserByEmail(authorEmail);

        Campaign campaign = dto.campaignId() != null ?
            campaignRepository.findById(dto.campaignId()).orElseThrow(() -> new IllegalArgumentException("Campagne introuvable")) : null;

        Character character = dto.characterId() != null ?
            characterRepository.findById(dto.characterId()).orElseThrow(() -> new IllegalArgumentException("Personnage introuvable")) : null;

        NoteType type = dto.type() != null ? dto.type() : NoteType.FREE_NOTE;

        Note note = new Note(dto.title(), dto.content(), type, author, campaign, character);

        if (dto.shareWithEmails() != null) {
            for (String email : dto.shareWithEmails()) {
                DomainUser targetUser = getDomainUserByEmail(email);
                note.addShare(targetUser);
            }
        }

        return toDto(noteRepository.save(note));
    }

    @Transactional
    public NoteResponseDto updateNote(String userEmail, Long noteId, UpdateNoteDto dto) {
        DomainUser user = getDomainUserByEmail(userEmail);
        Note note = getNoteIfAuthor(noteId, user.getId());

        note.setTitle(dto.title());
        note.setContent(dto.content());
        if (dto.type() != null) {
            note.setType(dto.type());
        }

        if (dto.shareWithEmails() != null) {
            note.clearShares();
            for (String email : dto.shareWithEmails()) {
                DomainUser targetUser = getDomainUserByEmail(email);
                note.addShare(targetUser);
            }
        }

        return toDto(noteRepository.save(note));
    }

    @Transactional
    public void deleteNote(String userEmail, Long noteId) {
        DomainUser user = getDomainUserByEmail(userEmail);
        Note note = getNoteIfAuthor(noteId, user.getId());
        noteRepository.delete(note);
    }

    @Transactional
    public NoteResponseDto copyNote(String userEmail, Long noteId) {
        DomainUser user = getDomainUserByEmail(userEmail);
        Note originalNote = noteRepository.findById(noteId)
            .orElseThrow(() -> new IllegalArgumentException("Note introuvable"));

        boolean isAuthor = originalNote.getAuthor().getId().equals(user.getId());
        boolean isShared = originalNote.getShares().stream().anyMatch(s -> s.getSharedWith().getId().equals(user.getId()));

        if (!isAuthor && !isShared) {
            throw new IllegalStateException("Vous n'avez pas accès à cette note pour la copier.");
        }

        Note copy = new Note(
            "[Copie] " + originalNote.getTitle(),
            originalNote.getContent(),
            originalNote.getType(),
            user,
            originalNote.getCampaign(),
            originalNote.getCharacter()
        );

        return toDto(noteRepository.save(copy));
    }

    @Transactional(readOnly = true)
    public List<NoteResponseDto> getMyNotes(String userEmail) {
        DomainUser user = getDomainUserByEmail(userEmail);
        return noteRepository.findAllAccessibleByUser(user).stream()
            .map(this::toDto)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<NoteResponseDto> getCampaignNotes(String userEmail, Long campaignId) {
        DomainUser user = getDomainUserByEmail(userEmail);
        return noteRepository.findAllByCampaignAndUser(campaignId, user).stream()
            .map(this::toDto)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<NoteResponseDto> getCharacterNotes(String userEmail, Long characterId) {
        DomainUser user = getDomainUserByEmail(userEmail);
        return noteRepository.findAllByCharacterAndUser(characterId, user).stream()
            .map(this::toDto)
            .toList();
    }

    private Note getNoteIfAuthor(Long noteId, Long userId) {
        Note note = noteRepository.findById(noteId)
            .orElseThrow(() -> new IllegalArgumentException("Note introuvable"));

        if (!note.getAuthor().getId().equals(userId)) {
            throw new IllegalStateException("Seul l'auteur peut modifier ou supprimer cette note.");
        }
        return note;
    }

    private DomainUser getDomainUserByEmail(String email) {
        User authUser = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable pour l'email : " + email));

        return domainUserRepository.findById(authUser.getId())
            .orElseThrow(() -> new IllegalArgumentException("Profil utilisateur introuvable"));
    }

    private NoteResponseDto toDto(Note note) {
        List<String> sharedEmails = note.getShares().stream()
            .map(s -> s.getSharedWith().getUser().getEmail())
            .toList();

        return new NoteResponseDto(
            note.getId(),
            note.getTitle(),
            note.getContent(),
            note.getType(),
            note.getAuthor().getId(),
            note.getAuthor().getUser().getEmail(),
            note.getCampaign() != null ? note.getCampaign().getId() : null,
            note.getCharacter() != null ? note.getCharacter().getId() : null,
            sharedEmails,
            note.getCreatedAt(),
            note.getUpdatedAt()
        );
    }
}