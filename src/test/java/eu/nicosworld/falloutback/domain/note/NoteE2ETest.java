package eu.nicosworld.falloutback.domain.note;

import eu.nicosworld.falloutback.AbstractNoteE2ETest;
import eu.nicosworld.falloutback.infrastructure.web.dto.note.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NoteE2ETest extends AbstractNoteE2ETest {

    @Test
    @DisplayName("Scénario 1 : Lifecycle d'une note de Character (Owner)")
    void scenario1_characterNoteLifecycle() {
        SetupData setup = createFullCampaignSetup("s1");

        // 1. Création de note par C1
        CreateNoteDto createDto = new CreateNoteDto(
            "Super Mutant Hideout",
            "Located near Diamond City.",
            NoteType.LOCATION,
            null,
            (long) setup.character1Id(),
            Collections.emptyList(),
            "NPC/Vault"
        );
        NoteResponseDto createdNote = createCharacterNote(setup.tokenPlayer1(), setup.character1Id(), createDto);
        assertNotNull(createdNote.id());
        assertEquals("NPC/Vault", createdNote.directory());
        assertTrue(createdNote.ownerNote());

        // 2. Vérification liste
        List<NoteSummaryDto> list = getCharacterNotes(setup.tokenPlayer1(), setup.character1Id());
        assertEquals(1, list.size());
        assertEquals(createdNote.id(), list.get(0).id());

        // 3. Consultation directe
        NoteResponseDto fetchedNote = getCharacterNote(setup.tokenPlayer1(), setup.character1Id(), createdNote.id());
        assertEquals("Super Mutant Hideout", fetchedNote.title());

        // 4. Modification par l'owner
        UpdateNoteDto updateDto = new UpdateNoteDto(
            "Cleared Hideout",
            "No mutants left.",
            NoteType.LOCATION,
            Collections.emptyList(),
            "NPC/Vault"
        );
        NoteResponseDto updatedNote = updateCharacterNote(setup.tokenPlayer1(), setup.character1Id(), createdNote.id(), updateDto);
        assertEquals("Cleared Hideout", updatedNote.title());

        // 5. Déplacement vers un autre directory
        NoteResponseDto movedNote = moveCharacterNote(setup.tokenPlayer1(), setup.character1Id(), createdNote.id(), "NPC/Settlement");
        assertEquals("NPC/Settlement", movedNote.directory());
        assertEquals(createdNote.id(), movedNote.id()); // L'owner modifie le directory de la note originale

        // 6. Suppression
        deleteCharacterNote(setup.tokenPlayer1(), setup.character1Id(), createdNote.id());

        // 7. Vérification disparition
        getCharacterNoteUnauthorized(setup.tokenPlayer1(), setup.character1Id(), createdNote.id(), 404);
    }

    @Test
    @DisplayName("Scénario 2 : Partage MJ -> Player (Campagne vers Character) & Lecture/Immuabilité")
    void scenario2_campaignNoteShareAndReadStatus() {
        SetupData setup = createFullCampaignSetup("s2");

        // Préparation d'un character externe (hors campagne)
        registerUser("p3_s2@test.com", "Password123!");
        String tokenP3 = loginAndGetToken("p3_s2@test.com", "Password123!");
        int c3ExtId = createCharacter(tokenP3, "ExternalCharacter");

        // 1. MJ crée une note de campagne
        CreateNoteDto createDto = new CreateNoteDto(
            "Quête Principale",
            "Trouver la puce d'eau.",
            NoteType.QUEST,
            (long) setup.campaignId(),
            null,
            Collections.emptyList(),
            "Quêtes"
        );
        NoteResponseDto campaignNote = createCampaignNote(setup.tokenGm(), setup.campaignId(), createDto);

        // 2. MJ partage la note avec C1
        shareCampaignNoteWithCharacter(setup.tokenGm(), setup.campaignId(), campaignNote.id(), setup.character1Id());

        // 3. Règle négative : Échec du partage vers C3_Ext (hors campagne)
        shareCampaignNoteWithCharacterFails(setup.tokenGm(), setup.campaignId(), campaignNote.id(), c3ExtId, 400);

        // 4. C1 vérifie sa liste (doit l'avoir dans 'shared' et unread)
        List<NoteSummaryDto> c1Notes = getCharacterNotes(setup.tokenPlayer1(), setup.character1Id());
        assertEquals(1, c1Notes.size());
        assertFalse(c1Notes.get(0).ownerNote());
        assertFalse(c1Notes.get(0).read());
        assertEquals("shared", c1Notes.get(0).directory());

        // 5. C1 lit la note -> Statut passe à read = true
        NoteResponseDto c1ReadNote = getCharacterNote(setup.tokenPlayer1(), setup.character1Id(), campaignNote.id());
        assertTrue(c1ReadNote.read());

        // 6. Immuabilité : C1 tente de modifier la note du MJ -> 403 Forbidden
        UpdateNoteDto forbiddenUpdate = new UpdateNoteDto("Hack", "Hack", NoteType.QUEST, Collections.emptyList(), "shared");
        updateCharacterNoteForbidden(setup.tokenPlayer1(), setup.character1Id(), campaignNote.id(), forbiddenUpdate);
    }

    @Test
    @DisplayName("Scénario 3 : Partage entre Players & Vers la Campagne (Visibilité ciblée)")
    void scenario3_playerToPlayerAndCampaignSharing() {
        SetupData setup = createFullCampaignSetup("s3");

        // 1. C1 crée une note
        CreateNoteDto createDto = new CreateNoteDto(
            "Secret Backstory",
            "Ghouls are friendly.",
            NoteType.BACKGROUND,
            null,
            (long) setup.character1Id(),
            Collections.emptyList(),
            "Personal"
        );
        NoteResponseDto noteC1 = createCharacterNote(setup.tokenPlayer1(), setup.character1Id(), createDto);

        // 2. C1 partage avec la Campagne (MJ)
        shareCharacterNoteWithCampaign(setup.tokenPlayer1(), setup.character1Id(), noteC1.id(), setup.campaignId());

        // 3. Le MJ la voit dans sa liste de campagne
        List<NoteSummaryDto> gmNotes = getCampaignNotes(setup.tokenGm(), setup.campaignId());
        assertEquals(1, gmNotes.size());

        // 4. C2 consulte sa liste -> Ne doit PAS la voir (La campagne n'est pas un groupe implicite pour C2)
        List<NoteSummaryDto> c2NotesBefore = getCharacterNotes(setup.tokenPlayer2(), setup.character2Id());
        assertTrue(c2NotesBefore.isEmpty());

        // 5. C1 partage explicitement avec C2
        shareCharacterNoteWithCharacter(setup.tokenPlayer1(), setup.character1Id(), noteC1.id(), setup.character2Id());

        // 6. C2 consulte sa liste -> La note apparaît
        List<NoteSummaryDto> c2NotesAfter = getCharacterNotes(setup.tokenPlayer2(), setup.character2Id());
        assertEquals(1, c2NotesAfter.size());
    }

    @Test
    @DisplayName("Scénario 4 : Isolation inter-campagnes (Sécurité stricte)")
    void scenario4_crossCampaignIsolation() {
        SetupData setupA = createFullCampaignSetup("s4_A");
        SetupData setupB = createFullCampaignSetup("s4_B");

        // C_A crée une note
        CreateNoteDto createDto = new CreateNoteDto(
            "Plans d'attaque",
            "Plan secret",
            NoteType.NPC,
            null,
            (long) setupA.character1Id(),
            Collections.emptyList(),
            "Secret"
        );
        NoteResponseDto noteCA = createCharacterNote(setupA.tokenPlayer1(), setupA.character1Id(), createDto);

        // Partage impossible vers Character de la table B -> 400 ou 403
        shareCharacterNoteWithCharacterFails(
            setupA.tokenPlayer1(),
            setupA.character1Id(),
            noteCA.id(),
            setupB.character1Id(),
            400
        );

        // Partage impossible vers Campagne B -> 400 ou 403
        shareCharacterNoteWithCampaignFails(
            setupA.tokenPlayer1(),
            setupA.character1Id(),
            noteCA.id(),
            setupB.campaignId(),
            400
        );

        // Accès direct en GET interdit pour C_B
        getCharacterNoteUnauthorized(setupB.tokenPlayer1(), setupB.character1Id(), noteCA.id(), 403);
    }

    @Test
    @DisplayName("Scénario 5 : Déplacement d'une note partagée (Auto-copie vers l'arborescence personnelle)")
    void scenario5_moveSharedNoteTriggersAutoCopy() {
        SetupData setup = createFullCampaignSetup("s5");

        // 1. MJ crée et partage une note
        CreateNoteDto createDto = new CreateNoteDto("Sanctuary", "Safe zone", NoteType.LOCATION, (long) setup.campaignId(), null, Collections.emptyList(), "Locations");
        NoteResponseDto originalNote = createCampaignNote(setup.tokenGm(), setup.campaignId(), createDto);
        shareCampaignNoteWithCharacter(setup.tokenGm(), setup.campaignId(), originalNote.id(), setup.character1Id());

        // 2. C1 déplace la note depuis 'shared' vers son dossier 'MyLocations'
        NoteResponseDto copiedAndMovedNote = moveCharacterNote(setup.tokenPlayer1(), setup.character1Id(), originalNote.id(), "MyLocations");

        // 3. Un nouvel ID est généré pour C1 qui devient propriétaire de sa copie
        assertNotEquals(originalNote.id(), copiedAndMovedNote.id());
        assertEquals("MyLocations", copiedAndMovedNote.directory());
        assertTrue(copiedAndMovedNote.ownerNote());

        // 4. C1 modifie sa copie
        UpdateNoteDto updateDto = new UpdateNoteDto("Sanctuary Ruined", "Destroyed by Raiders", NoteType.LOCATION, Collections.emptyList(), "MyLocations");
        NoteResponseDto updatedCopy = updateCharacterNote(setup.tokenPlayer1(), setup.character1Id(), copiedAndMovedNote.id(), updateDto);
        assertEquals("Sanctuary Ruined", updatedCopy.title());

        // 5. L'originale du MJ reste inchangée
        NoteResponseDto gmOriginal = getCampaignNote(setup.tokenGm(), setup.campaignId(), originalNote.id());
        assertEquals("Sanctuary", gmOriginal.title());
    }

    @Test
    @DisplayName("Scénario 6 : Copie explicite d'une note (Copie partagée + Copie personnelle pour annotations)")
    void scenario6_explicitCopyBehavior() {
        SetupData setup = createFullCampaignSetup("s6");

        // A) Copie par un destinataire
        CreateNoteDto dto1 = new CreateNoteDto("Brahmin Herd", "Trade route", NoteType.FREE_NOTE, null, (long) setup.character1Id(), Collections.emptyList(), "Notes");
        NoteResponseDto noteC1 = createCharacterNote(setup.tokenPlayer1(), setup.character1Id(), dto1);
        shareCharacterNoteWithCharacter(setup.tokenPlayer1(), setup.character1Id(), noteC1.id(), setup.character2Id());

        NoteResponseDto c2Copy = copyCharacterNote(setup.tokenPlayer2(), setup.character2Id(), noteC1.id());
        assertNotEquals(noteC1.id(), c2Copy.id());
        assertTrue(c2Copy.ownerNote());

        // B) Copie personnelle par le propriétaire (ex: MJ duplique sa propre note pour l'annoter en privé)
        CreateNoteDto dtoGm = new CreateNoteDto("Plan du Donjon", "Salles 1 à 5 publiques", NoteType.MAP, (long) setup.campaignId(), null, Collections.emptyList(), "Donjons");
        NoteResponseDto gmNote = createCampaignNote(setup.tokenGm(), setup.campaignId(), dtoGm);

        NoteResponseDto gmPersonalCopy = copyCampaignNote(setup.tokenGm(), setup.campaignId(), gmNote.id());
        assertNotEquals(gmNote.id(), gmPersonalCopy.id());
        assertTrue(gmPersonalCopy.ownerNote());

        // Annotations privées sur la copie
        UpdateNoteDto updatePrivate = new UpdateNoteDto("Plan du Donjon (MJ)", "Salle 3 piège mortel secret!", NoteType.MAP, Collections.emptyList(), "Donjons/Secrets");
        NoteResponseDto updatedPrivate = updateCampaignNote(setup.tokenGm(), setup.campaignId(), gmPersonalCopy.id(), updatePrivate);

        assertEquals("Plan du Donjon (MJ)", updatedPrivate.title());
        // L'originale reste propre pour un futur partage sans spoilers
        NoteResponseDto gmOriginal = getCampaignNote(setup.tokenGm(), setup.campaignId(), gmNote.id());
        assertEquals("Salles 1 à 5 publiques", gmOriginal.content());
    }

    @Test
    @DisplayName("Scénario 7 : Suppression d'un partage vs Suppression de la note originale")
    void scenario7_deleteSharedVsDeleteOriginal() {
        SetupData setup = createFullCampaignSetup("s7");

        // 1. C1 crée et partage avec C2
        CreateNoteDto dto = new CreateNoteDto("Pistolet Laser", "Arme énergie", NoteType.FREE_NOTE, null, (long) setup.character1Id(), Collections.emptyList(), "Items");
        NoteResponseDto noteC1 = createCharacterNote(setup.tokenPlayer1(), setup.character1Id(), dto);
        shareCharacterNoteWithCharacter(setup.tokenPlayer1(), setup.character1Id(), noteC1.id(), setup.character2Id());

        // 2. C2 supprime de son espace shared
        deleteCharacterNote(setup.tokenPlayer2(), setup.character2Id(), noteC1.id());

        // 3. C2 ne la voit plus
        List<NoteSummaryDto> c2Notes = getCharacterNotes(setup.tokenPlayer2(), setup.character2Id());
        assertTrue(c2Notes.isEmpty());

        // 4. C1 la voit toujours (Originale intacte)
        List<NoteSummaryDto> c1Notes = getCharacterNotes(setup.tokenPlayer1(), setup.character1Id());
        assertEquals(1, c1Notes.size());

        // 5. C1 supprime la note originale
        deleteCharacterNote(setup.tokenPlayer1(), setup.character1Id(), noteC1.id());

        // 6. C1 ne la voit plus non plus
        List<NoteSummaryDto> c1NotesAfter = getCharacterNotes(setup.tokenPlayer1(), setup.character1Id());
        assertTrue(c1NotesAfter.isEmpty());
    }

    @Test
    @DisplayName("Scénario 8 : Révocation explicite du partage par le propriétaire (Unshare)")
    void scenario8_unshareExplicit() {
        SetupData setup = createFullCampaignSetup("s8");

        // 1. MJ crée et partage avec C1
        CreateNoteDto dto = new CreateNoteDto("Alliances", "BOS vs Enclave", NoteType.BACKGROUND, (long) setup.campaignId(), null, Collections.emptyList(), "Histoire");
        NoteResponseDto noteGm = createCampaignNote(setup.tokenGm(), setup.campaignId(), dto);
        shareCampaignNoteWithCharacter(setup.tokenGm(), setup.campaignId(), noteGm.id(), setup.character1Id());

        // C1 la voit
        assertEquals(1, getCharacterNotes(setup.tokenPlayer1(), setup.character1Id()).size());

        // 2. MJ révoque le partage
        unshareCampaignNoteFromCharacter(setup.tokenGm(), setup.campaignId(), noteGm.id(), setup.character1Id());

        // 3. C1 ne la voit plus
        assertTrue(getCharacterNotes(setup.tokenPlayer1(), setup.character1Id()).isEmpty());

        // 4. Accès direct refusé
        getCharacterNoteUnauthorized(setup.tokenPlayer1(), setup.character1Id(), noteGm.id(), 403);
    }
}