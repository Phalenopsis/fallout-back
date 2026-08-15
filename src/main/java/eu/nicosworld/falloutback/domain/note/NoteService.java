package eu.nicosworld.falloutback.domain.note;

import eu.nicosworld.falloutback.authentication.UserRepository;
import eu.nicosworld.falloutback.authentication.model.User;
import eu.nicosworld.falloutback.exception.ResourceNotFoundException;
import eu.nicosworld.falloutback.exception.UnauthorizedAccessException;
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
import eu.nicosworld.falloutback.infrastructure.web.dto.note.NoteSummaryDto;
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

    // Exemple dans NoteService.java

    @Transactional(readOnly = true)
    public NoteResponseDto getNoteById(String userEmail, Long noteId) {
        DomainUser user = getDomainUserByEmail(userEmail);

        // On cherche d'abord si la note existe en BDD
        Note note = noteRepository.findById(noteId)
            .orElseThrow(() -> new ResourceNotFoundException("Note introuvable avec l'ID : " + noteId));

        // Si elle existe, on vérifie si l'utilisateur y a accès (Auteur ou Partagée)
        boolean isAuthor = note.getAuthor().getId().equals(user.getId());
        boolean isShared = note.getShares().stream().anyMatch(s -> s.getSharedWith().getId().equals(user.getId()));

        if (!isAuthor && !isShared) {
            throw new UnauthorizedAccessException("Accès refusé : vous n'avez pas les droits pour consulter cette note.");
        }

        return toDto(note);
    }

    private Note getNoteIfAuthor(Long noteId, Long userId) {
        Note note = noteRepository.findById(noteId)
            .orElseThrow(() -> new ResourceNotFoundException("Note introuvable avec l'ID : " + noteId));

        if (!note.getAuthor().getId().equals(userId)) {
            throw new UnauthorizedAccessException("Accès refusé : vous n'êtes pas l'auteur de cette note.");
        }
        return note;
    }

    @Transactional(readOnly = true)
    public List<NoteSummaryDto> getCharacterNotesSummary(String userEmail, Long characterId, NoteType type) {
        DomainUser user = getDomainUserByEmail(userEmail);
        List<Note> notes = (type != null) ?
            noteRepository.findAllByCharacterAndTypeAndUser(characterId, type, user) :
            noteRepository.findAllByCharacterAndUser(characterId, user);

        return notes.stream()
            .map(n -> new NoteSummaryDto(n.getId(), n.getTitle(), n.getType()))
            .toList();
    }

    @Transactional(readOnly = true)
    public List<NoteSummaryDto> getCampaignNotesSummary(String userEmail, Long campaignId, NoteType type) {
        DomainUser user = getDomainUserByEmail(userEmail);
        List<Note> notes = (type != null) ?
            noteRepository.findAllByCampaignAndTypeAndUser(campaignId, type, user) :
            noteRepository.findAllByCampaignAndUser(campaignId, user);

        return notes.stream()
            .map(n -> new NoteSummaryDto(n.getId(), n.getTitle(), n.getType()))
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