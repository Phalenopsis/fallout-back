package eu.nicosworld.falloutback.domain.note;

import eu.nicosworld.falloutback.authentication.UserRepository;
import eu.nicosworld.falloutback.authentication.model.User;
import eu.nicosworld.falloutback.exception.ResourceNotFoundException;
import eu.nicosworld.falloutback.exception.UnauthorizedAccessException;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.DomainUser;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.campaign.Campaign;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.campaign.CampaignCharacter;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.character.Character;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.note.Note;
import eu.nicosworld.falloutback.infrastructure.persistence.entity.note.NoteShare;
import eu.nicosworld.falloutback.infrastructure.persistence.repository.DomainUserRepository;
import eu.nicosworld.falloutback.infrastructure.persistence.repository.campaign.CampaignCharacterRepository;
import eu.nicosworld.falloutback.infrastructure.persistence.repository.campaign.CampaignRepository;
import eu.nicosworld.falloutback.infrastructure.persistence.repository.character.CharacterRepository;
import eu.nicosworld.falloutback.infrastructure.persistence.repository.note.NoteRepository;
import eu.nicosworld.falloutback.infrastructure.persistence.repository.note.NoteShareRepository;
import eu.nicosworld.falloutback.infrastructure.web.dto.note.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final NoteShareRepository noteShareRepository;
    private final DomainUserRepository domainUserRepository;
    private final CampaignRepository campaignRepository;
    private final CampaignCharacterRepository campaignCharacterRepository;
    private final CharacterRepository characterRepository;
    private final UserRepository userRepository;

    public NoteService(
        NoteRepository noteRepository,
        NoteShareRepository noteShareRepository,
        DomainUserRepository domainUserRepository,
        CampaignRepository campaignRepository,
        CampaignCharacterRepository campaignCharacterRepository,
        CharacterRepository characterRepository,
        UserRepository userRepository
    ) {
        this.noteRepository = noteRepository;
        this.noteShareRepository = noteShareRepository;
        this.domainUserRepository = domainUserRepository;
        this.campaignRepository = campaignRepository;
        this.campaignCharacterRepository = campaignCharacterRepository;
        this.characterRepository = characterRepository;
        this.userRepository = userRepository;
    }


    // ============================================================
    // UTILISATEUR
    // ============================================================

    private DomainUser getDomainUser(UserDetails userDetails) {
        User authUser = userRepository.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable pour l'email : " + userDetails.getUsername()));

        return domainUserRepository.findById(authUser.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Profil utilisateur introuvable"));
    }

    // ============================================================
    // CRÉATION
    // ============================================================

    @Transactional
    public NoteResponseDto createCharacterNote(
        UserDetails userDetails,
        Long characterId,
        CreateNoteDto dto
    ) {
        DomainUser user = getDomainUser(userDetails);
        Character character = getCharacter(characterId);

        checkCharacterOwner(character, user);

        Note note = new Note(
            dto.title(),
            dto.content(),
            dto.type(),
            null,
            character,
            dto.directory()
        );

        return toResponseDto(noteRepository.save(note));
    }


    @Transactional
    public NoteResponseDto createCampaignNote(
        UserDetails userDetails,
        Long campaignId,
        CreateNoteDto dto
    ) {
        DomainUser user = getDomainUser(userDetails);
        Campaign campaign = getCampaign(campaignId);

        checkCampaignGameMaster(campaign, user);

        Note note = new Note(
            dto.title(),
            dto.content(),
            dto.type(),
            campaign,
            null
        );

        return toResponseDto(noteRepository.save(note));
    }


    // ============================================================
    // VISUALISATION
    // ============================================================

    @Transactional
    public NoteResponseDto getCharacterNote(
        UserDetails userDetails,
        Long characterId,
        Long noteId
    ) {
        DomainUser user = getDomainUser(userDetails);
        Character character = getCharacter(characterId);

        checkCharacterOwner(character, user);

        Note note = getNote(noteId);

        NoteShare share = findCharacterShare(note, character);

        if (isOwnerOfCharacterNote(note, character)) {
            return toResponseDto(note);
        }

        if (share != null) {
            markAsRead(share);
            return toResponseDto(note, share);
        }

        throw new UnauthorizedAccessException(
            "Vous n'avez pas accès à cette note."
        );
    }


    @Transactional
    public NoteResponseDto getCampaignNote(
        UserDetails userDetails,
        Long campaignId,
        Long noteId
    ) {
        DomainUser user = getDomainUser(userDetails);
        Campaign campaign = getCampaign(campaignId);

        checkCampaignGameMaster(campaign, user);

        Note note = getNote(noteId);

        if (isOwnerOfCampaignNote(note, campaign)) {
            return toResponseDto(note);
        }

        NoteShare share = findCampaignShare(note, campaign);

        if (share != null) {
            markAsRead(share);
            return toResponseDto(note, share);
        }

        throw new UnauthorizedAccessException(
            "Vous n'avez pas accès à cette note."
        );
    }


    // ============================================================
    // LISTES
    // ============================================================

    @Transactional(readOnly = true)
    public List<NoteSummaryDto> getCharacterNotes(
        UserDetails userDetails,
        Long characterId,
        NoteType type
    ) {
        DomainUser user = getDomainUser(userDetails);
        Character character = getCharacter(characterId);

        checkCharacterOwner(character, user);

        if(type != null) {
            return noteRepository
                .findAllAccessibleByCharacterAndType(character, type)
                .stream()
                .map(note -> toSummaryDto(note, character))
                .toList();
        }

        return noteRepository.findAllAccessibleByCharacter(character).stream().map(note -> toSummaryDto(note, character)).toList();
    }


    @Transactional(readOnly = true)
    public List<NoteSummaryDto> getCampaignNotes(
        UserDetails userDetails,
        Long campaignId,
        NoteType type
    ) {
        DomainUser user = getDomainUser(userDetails);
        Campaign campaign = getCampaign(campaignId);

        checkCampaignGameMaster(campaign, user);

        if(type != null) {
            return noteRepository
                .findAllAccessibleByCampaignAndType(campaign, type)
                .stream()
                .map(note -> toSummaryDto(note, campaign))
                .toList();
        }
        return noteRepository
            .findAllAccessibleByCampaign(campaign)
            .stream()
            .map(note -> toSummaryDto(note, campaign))
            .toList();
    }


    // ============================================================
    // UPDATE
    // ============================================================

    @Transactional
    public NoteResponseDto updateCharacterNote(
        UserDetails userDetails,
        Long characterId,
        Long noteId,
        UpdateNoteDto dto
    ) {
        DomainUser user = getDomainUser(userDetails);
        Character character = getCharacter(characterId);

        checkCharacterOwner(character, user);

        Note note = getNote(noteId);

        checkCharacterNoteOwner(note, character);

        note.setTitle(dto.title());
        note.setContent(dto.content());
        note.setDirectory(dto.directory());

        if (dto.type() != null) {
            note.setType(dto.type());
        }

        return toResponseDto(noteRepository.save(note));
    }


    @Transactional
    public NoteResponseDto updateCampaignNote(
        UserDetails userDetails,
        Long campaignId,
        Long noteId,
        UpdateNoteDto dto
    ) {
        DomainUser user = getDomainUser(userDetails);
        Campaign campaign = getCampaign(campaignId);

        checkCampaignGameMaster(campaign, user);

        Note note = getNote(noteId);

        checkCampaignNoteOwner(note, campaign);

        note.setTitle(dto.title());
        note.setContent(dto.content());

        if (dto.type() != null) {
            note.setType(dto.type());
        }

        return toResponseDto(noteRepository.save(note));
    }


    // ============================================================
    // PARTAGE
    // ============================================================

    @Transactional
    public NoteResponseDto shareCampaignNoteWithCharacter(
        UserDetails userDetails,
        Long campaignId,
        Long noteId,
        Long characterId
    ) {
        DomainUser user = getDomainUser(userDetails);

        Campaign campaign = getCampaign(campaignId);
        checkCampaignGameMaster(campaign, user);

        Note note = getNote(noteId);
        checkCampaignNoteOwner(note, campaign);

        Character character = getCharacter(characterId);

        checkCharacterBelongsToCampaign(character, campaign);

        if (findCharacterShare(note, character) != null) {
            return toResponseDto(note);
        }

        NoteShare share = new NoteShare(
            note,
            null,
            character
        );

        note.addShare(share);

        noteShareRepository.save(share);

        return toResponseDto(note);
    }


    @Transactional
    public NoteResponseDto shareCharacterNoteWithCharacter(
        UserDetails userDetails,
        Long characterId,
        Long noteId,
        Long targetCharacterId
    ) {
        DomainUser user = getDomainUser(userDetails);

        Character character = getCharacter(characterId);
        checkCharacterOwner(character, user);

        Note note = getNote(noteId);
        checkCharacterNoteOwner(note, character);

        Character targetCharacter = getCharacter(targetCharacterId);

        checkCharactersCanShare(character, targetCharacter);

        if (findCharacterShare(note, targetCharacter) != null) {
            return toResponseDto(note);
        }

        NoteShare share = new NoteShare(
            note,
            null,
            targetCharacter
        );

        note.addShare(share);

        noteShareRepository.save(share);

        return toResponseDto(note);
    }


    @Transactional
    public NoteResponseDto shareCharacterNoteWithCampaign(
        UserDetails userDetails,
        Long characterId,
        Long noteId,
        Long campaignId
    ) {
        DomainUser user = getDomainUser(userDetails);

        Character character = getCharacter(characterId);
        checkCharacterOwner(character, user);

        Note note = getNote(noteId);
        checkCharacterNoteOwner(note, character);

        Campaign campaign = getCampaign(campaignId);

        checkCharacterBelongsToCampaign(character, campaign);

        if (findCampaignShare(note, campaign) != null) {
            return toResponseDto(note);
        }

        NoteShare share = new NoteShare(
            note,
            campaign,
            null
        );

        note.addShare(share);

        noteShareRepository.save(share);

        return toResponseDto(note);
    }


    // ============================================================
    // SUPPRESSION DES PARTAGES
    // ============================================================

    @Transactional
    public void unshareCharacterNoteFromCharacter(
        UserDetails userDetails,
        Long characterId,
        Long noteId,
        Long targetCharacterId
    ) {
        DomainUser user = getDomainUser(userDetails);

        Character character = getCharacter(characterId);
        checkCharacterOwner(character, user);

        Note note = getNote(noteId);
        checkCharacterNoteOwner(note, character);

        NoteShare share = findCharacterShare(note, getCharacter(targetCharacterId));

        if (share == null) {
            throw new ResourceNotFoundException(
                "Partage introuvable."
            );
        }

        note.removeShare(share);
        noteShareRepository.delete(share);
    }


    @Transactional
    public void unshareCharacterNoteFromCampaign(
        UserDetails userDetails,
        Long characterId,
        Long noteId,
        Long campaignId
    ) {
        DomainUser user = getDomainUser(userDetails);

        Character character = getCharacter(characterId);
        checkCharacterOwner(character, user);

        Note note = getNote(noteId);
        checkCharacterNoteOwner(note, character);

        Campaign campaign = getCampaign(campaignId);

        NoteShare share = findCampaignShare(note, campaign);

        if (share == null) {
            throw new ResourceNotFoundException(
                "Partage introuvable."
            );
        }

        note.removeShare(share);
        noteShareRepository.delete(share);
    }


    @Transactional
    public void unshareCampaignNoteFromCharacter(
        UserDetails userDetails,
        Long campaignId,
        Long noteId,
        Long characterId
    ) {
        DomainUser user = getDomainUser(userDetails);

        Campaign campaign = getCampaign(campaignId);
        checkCampaignGameMaster(campaign, user);

        Note note = getNote(noteId);
        checkCampaignNoteOwner(note, campaign);

        Character character = getCharacter(characterId);

        NoteShare share = findCharacterShare(note, character);

        if (share == null) {
            throw new ResourceNotFoundException(
                "Partage introuvable."
            );
        }

        note.removeShare(share);
        noteShareRepository.delete(share);
    }


    // ============================================================
    // COPIE
    // ============================================================

    @Transactional
    public NoteResponseDto copyCharacterNote(
        UserDetails userDetails,
        Long characterId,
        Long noteId
    ) {
        DomainUser user = getDomainUser(userDetails);

        Character targetCharacter = getCharacter(characterId);
        checkCharacterOwner(targetCharacter, user);

        Note original = getAccessibleNoteForCharacter(
            noteId,
            targetCharacter
        );

        Note copy = new Note(
            "[Copie] " + original.getTitle(),
            original.getContent(),
            original.getType(),
            null,
            targetCharacter
        );

        copy.setDirectory(original.getDirectory());

        return toResponseDto(
            noteRepository.save(copy)
        );
    }


    @Transactional
    public NoteResponseDto copyCampaignNote(
        UserDetails userDetails,
        Long campaignId,
        Long noteId
    ) {
        DomainUser user = getDomainUser(userDetails);

        Campaign targetCampaign = getCampaign(campaignId);
        checkCampaignGameMaster(targetCampaign, user);

        Note original = getAccessibleNoteForCampaign(
            noteId,
            targetCampaign
        );

        Note copy = new Note(
            "[Copie] " + original.getTitle(),
            original.getContent(),
            original.getType(),
            targetCampaign,
            null
        );

        copy.setDirectory(original.getDirectory());

        return toResponseDto(
            noteRepository.save(copy)
        );
    }


    // ============================================================
    // DIRECTORY
    // ============================================================

    @Transactional
    public NoteResponseDto moveCharacterNote(
        UserDetails userDetails,
        Long characterId,
        Long noteId,
        String directory
    ) {
        DomainUser user = getDomainUser(userDetails);

        Character character = getCharacter(characterId);
        checkCharacterOwner(character, user);

        Note note = getNote(noteId);

        if (isOwnerOfCharacterNote(note, character)) {
            note.setDirectory(normalizeDirectory(directory));

            return toResponseDto(
                noteRepository.save(note)
            );
        }

        NoteShare share = findCharacterShare(note, character);

        if (share == null) {
            throw new UnauthorizedAccessException(
                "Vous n'avez pas accès à cette note."
            );
        }

        Note copy = new Note(
            "[Copie] " + note.getTitle(),
            note.getContent(),
            note.getType(),
            null,
            character
        );

        copy.setDirectory(normalizeDirectory(directory));

        return toResponseDto(
            noteRepository.save(copy)
        );
    }


    @Transactional
    public NoteResponseDto moveCampaignNote(
        UserDetails userDetails,
        Long campaignId,
        Long noteId,
        String directory
    ) {
        DomainUser user = getDomainUser(userDetails);

        Campaign campaign = getCampaign(campaignId);
        checkCampaignGameMaster(campaign, user);

        Note note = getNote(noteId);

        if (isOwnerOfCampaignNote(note, campaign)) {
            note.setDirectory(normalizeDirectory(directory));

            return toResponseDto(
                noteRepository.save(note)
            );
        }

        NoteShare share = findCampaignShare(note, campaign);

        if (share == null) {
            throw new UnauthorizedAccessException(
                "Vous n'avez pas accès à cette note."
            );
        }

        Note copy = new Note(
            "[Copie] " + note.getTitle(),
            note.getContent(),
            note.getType(),
            campaign,
            null
        );

        copy.setDirectory(normalizeDirectory(directory));

        return toResponseDto(
            noteRepository.save(copy)
        );
    }


    // ============================================================
    // SUPPRESSION
    // ============================================================

    @Transactional
    public void deleteCharacterNote(
        UserDetails userDetails,
        Long characterId,
        Long noteId
    ) {
        DomainUser user = getDomainUser(userDetails);

        Character character = getCharacter(characterId);
        checkCharacterOwner(character, user);

        Note note = getNote(noteId);

        if (isOwnerOfCharacterNote(note, character)) {
            noteRepository.delete(note);
            return;
        }

        NoteShare share = findCharacterShare(note, character);

        if (share == null) {
            throw new UnauthorizedAccessException(
                "Vous n'avez pas accès à cette note."
            );
        }

        note.removeShare(share);
        noteShareRepository.delete(share);
    }


    @Transactional
    public void deleteCampaignNote(
        UserDetails userDetails,
        Long campaignId,
        Long noteId
    ) {
        DomainUser user = getDomainUser(userDetails);

        Campaign campaign = getCampaign(campaignId);
        checkCampaignGameMaster(campaign, user);

        Note note = getNote(noteId);

        if (isOwnerOfCampaignNote(note, campaign)) {
            noteRepository.delete(note);
            return;
        }

        NoteShare share = findCampaignShare(note, campaign);

        if (share == null) {
            throw new UnauthorizedAccessException(
                "Vous n'avez pas accès à cette note."
            );
        }

        note.removeShare(share);
        noteShareRepository.delete(share);
    }


    // ============================================================
    // ACCÈS AUX RESSOURCES
    // ============================================================

    private Character getCharacter(Long characterId) {
        return characterRepository.findById(characterId)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Personnage introuvable avec l'ID : " + characterId
                )
            );
    }


    private Campaign getCampaign(Long campaignId) {
        return campaignRepository.findById(campaignId)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Campagne introuvable avec l'ID : " + campaignId
                )
            );
    }


    private Note getNote(Long noteId) {
        return noteRepository.findById(noteId)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Note introuvable avec l'ID : " + noteId
                )
            );
    }


    // ============================================================
    // CONTRÔLES D'AUTORISATION
    // ============================================================

    private void checkCharacterOwner(
        Character character,
        DomainUser user
    ) {
        if (!character.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException(
                "Vous n'êtes pas propriétaire de ce personnage."
            );
        }
    }


    private void checkCampaignGameMaster(
        Campaign campaign,
        DomainUser user
    ) {
        if (!campaign.getGameMaster().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException(
                "Vous n'êtes pas le MJ de cette campagne."
            );
        }
    }


    private void checkCharacterNoteOwner(
        Note note,
        Character character
    ) {
        if (!isOwnerOfCharacterNote(note, character)) {
            throw new UnauthorizedAccessException(
                "Cette note n'appartient pas à ce personnage."
            );
        }
    }


    private void checkCampaignNoteOwner(
        Note note,
        Campaign campaign
    ) {
        if (!isOwnerOfCampaignNote(note, campaign)) {
            throw new UnauthorizedAccessException(
                "Cette note n'appartient pas à cette campagne."
            );
        }
    }


    private boolean isOwnerOfCharacterNote(
        Note note,
        Character character
    ) {
        return note.getCharacter() != null
            && note.getCharacter().getId().equals(character.getId());
    }


    private boolean isOwnerOfCampaignNote(
        Note note,
        Campaign campaign
    ) {
        return note.getCampaign() != null
            && note.getCampaign().getId().equals(campaign.getId());
    }


    // ============================================================
    // CONTRÔLES CAMPAGNE / CHARACTERS
    // ============================================================

    private void checkCharacterBelongsToCampaign(
        Character character,
        Campaign campaign
    ) {
        if (!campaignCharacterRepository
            .existsByCampaign_IdAndCharacter_Id(
                campaign.getId(),
                character.getId()
            )) {

            throw new IllegalArgumentException(
                "Ce personnage ne fait pas partie de cette campagne."
            );
        }
    }


    private void checkCharactersCanShare(
        Character source,
        Character target
    ) {
        Campaign sourceCampaign =
            campaignCharacterRepository
                .findByCharacter(source)
                .map(CampaignCharacter::getCampaign)
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "Le personnage source n'appartient à aucune campagne."
                    )
                );

        checkCharacterBelongsToCampaign(
            target,
            sourceCampaign
        );
    }


    // ============================================================
    // PARTAGES
    // ============================================================

    private NoteShare findCharacterShare(
        Note note,
        Character character
    ) {
        return note.getShares()
            .stream()
            .filter(share ->
                share.getCharacter() != null
                    && share.getCharacter().getId().equals(character.getId())
            )
            .findFirst()
            .orElse(null);
    }


    private NoteShare findCampaignShare(
        Note note,
        Campaign campaign
    ) {
        return note.getShares()
            .stream()
            .filter(share ->
                share.getCampaign() != null
                    && share.getCampaign().getId().equals(campaign.getId())
            )
            .findFirst()
            .orElse(null);
    }


    private void markAsRead(NoteShare share) {
        if (!share.isRead()) {
            share.setRead(true);
            noteShareRepository.save(share);
        }
    }


    // ============================================================
    // NOTES ACCESSIBLES
    // ============================================================

    private Note getAccessibleNoteForCharacter(
        Long noteId,
        Character character
    ) {
        Note note = getNote(noteId);

        if (isOwnerOfCharacterNote(note, character)) {
            return note;
        }

        if (findCharacterShare(note, character) != null) {
            return note;
        }

        throw new UnauthorizedAccessException(
            "Vous n'avez pas accès à cette note."
        );
    }


    private Note getAccessibleNoteForCampaign(
        Long noteId,
        Campaign campaign
    ) {
        Note note = getNote(noteId);

        if (isOwnerOfCampaignNote(note, campaign)) {
            return note;
        }

        if (findCampaignShare(note, campaign) != null) {
            return note;
        }

        throw new UnauthorizedAccessException(
            "Vous n'avez pas accès à cette note."
        );
    }


    // ============================================================
    // DTO
    // ============================================================

    private NoteResponseDto toResponseDto(Note note) {
        return toResponseDto(note, null);
    }


    private NoteResponseDto toResponseDto(
        Note note,
        NoteShare currentShare
    ) {
        boolean owner = currentShare == null;

        List<NoteShareTargetDto> sharedWith = owner
            ? note.getShares()
            .stream()
            .map(this::toShareTargetDto)
            .toList()
            : List.of();

        return new NoteResponseDto(
            note.getId(),
            note.getTitle(),
            note.getContent(),
            note.getType(),
            Objects.nonNull(note.getCampaign()) ? note.getCampaign().getId() : null,
            Objects.nonNull(note.getCharacter()) ? note.getCharacter().getId() : null,
            note.getDirectory(),
            owner,
            sharedWith,
            currentShare != null && currentShare.isRead(),
            note.getCreatedAt(),
            note.getUpdatedAt()
        );
    }


    private NoteSummaryDto toSummaryDto(
        Note note,
        Character character
    ) {
        NoteShare share = findCharacterShare(note, character);

        return new NoteSummaryDto(
            note.getId(),
            note.getTitle(),
            note.getType(),
            share == null ? note.getDirectory() : "shared",
            share == null,
            share != null && share.isRead()
        );
    }


    private NoteSummaryDto toSummaryDto(
        Note note,
        Campaign campaign
    ) {
        NoteShare share = findCampaignShare(note, campaign);

        return new NoteSummaryDto(
            note.getId(),
            note.getTitle(),
            note.getType(),
            share == null ? note.getDirectory() : "shared",
            share == null,
            share != null && share.isRead()
        );
    }


    private NoteShareTargetDto toShareTargetDto(
        NoteShare share
    ) {
        if (share.getCharacter() != null) {
            Character character = share.getCharacter();

            return new NoteShareTargetDto(
                ShareTargetType.CHARACTER,
                character.getId(),
                character.getName()
            );
        }

        Campaign campaign = share.getCampaign();

        return new NoteShareTargetDto(
            ShareTargetType.CAMPAIGN,
            campaign.getId(),
            campaign.getName()
        );
    }


    // ============================================================
    // DIRECTORY
    // ============================================================

    private String normalizeDirectory(String directory) {
        if (directory == null || directory.isBlank()) {
            return "";
        }

        String normalized = directory.trim();

        while (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }

        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }

        return normalized;
    }
}