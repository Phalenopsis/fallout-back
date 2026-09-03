# Documentation Technique — API Notes

Cette documentation détaille l'ensemble des endpoints HTTP, des modèles de données (DTOs) et des enums régissant le module de gestion des notes.

---

## 1. Modèles de Données & Data Transfer Objects (DTOs)

### `NoteType` (Enum)

Catégorie métier obligatoire attribuée à chaque note.

```java
public enum NoteType {
    LOCATION,
    NPC,
    QUEST,
    FREE_NOTE,
    MAP,
    BACKGROUND
}

```

### `ShareTargetType` (Enum)

Type d'entité destinataire d'un partage.

```java
public enum ShareTargetType {
    CAMPAIGN,
    CHARACTER
}

```

---

### `CreateNoteDto` (Record)

Payload envoyé lors de la création d'une note.

```java
public record CreateNoteDto(
    String title,
    String content,
    NoteType type,
    Long campaignId,
    Long characterId,
    List<NoteShareTargetDto> shareTargets,
    String directory
) {}

```

### `UpdateNoteDto` (Record)

Payload envoyé lors de la mise à jour des informations principales d'une note.

```java
public record UpdateNoteDto(
    String title,
    String content,
    NoteType type,
    List<NoteShareTargetDto> shareTargets,
    String directory
) {}

```

### `UpdateNoteDirectoryDto` (Record)

Payload envoyé lors du déplacement d'une note dans l'arborescence personnelle.

```java
public record UpdateNoteDirectoryDto(
    String directory
) {}

```

### `NoteShareTargetDto` (Record)

Représentation d'une cible de partage.

```java
public record NoteShareTargetDto(
    ShareTargetType type,
    Long id,
    String name
) {}

```

### `NoteSummaryDto` (Record)

Représentation allégée d'une note pour l'affichage en liste.

```java
public record NoteSummaryDto(
    Long id,
    String title,
    NoteType type,
    String directory,
    boolean ownerNote,
    boolean read
) {}

```

### `NoteResponseDto` (Record)

Représentation complète d'une note (consultation détaillée ou retour de création/modification).

```java
public record NoteResponseDto(
    Long id,
    String title,
    String content,
    NoteType type,
    Long campaignId,
    Long characterId,
    String directory,
    boolean ownerNote,
    List<NoteShareTargetDto> sharedWith,
    Boolean read,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}

```

---

## 2. Spécification des Endpoints HTTP

**Prefixe de base :** `/api/notes`

**Authentification :** Requis (`Authorization: Bearer <token>`)

| Catégorie | Méthode | Endpoint                                                                | Description | Statut HTTP |
| --- | --- |-------------------------------------------------------------------------| --- | --- |
| **Création** | `POST` | `/character/{characterId}`                                              | Crée une note appartenant à un personnage. | `200 OK` |
|  | `POST` | `/campaign/{campaignId}`                                                | Crée une note de campagne (MJ). | `200 OK` |
| **Lecture** | `GET` | `/character/{characterId}/{noteId}`                                     | Récupère une note pour un personnage (passe `read` à `true` si partagée). | `200 OK` |
|  | `GET` | `/campaign/{campaignId}/{noteId}`                                       | Récupère une note pour une campagne. | `200 OK` |
| **Listes** | `GET` | `/character/{characterId}/all`                                          | Liste les notes d'un personnage (propriété + partages). | `200 OK` |
|  | `GET` | `/campaign/{campaignId}/all`                                            | Liste les notes d'une campagne (propriété + partages). | `200 OK` |
| **Modification** | `PUT` | `/character/{characterId}/{noteId}`                                     | Modifie une note de personnage *(Propriétaire uniquement)*. | `200 OK` / `403` |
|  | `PUT` | `/campaign/{campaignId}/{noteId}`                                       | Modifie une note de campagne *(MJ uniquement)*. | `200 OK` / `403` |
| **Partage** | `POST` | `/campaign/{campaignId}/{noteId}/share/character/{characterId}`         | MJ partage sa note de campagne avec un personnage membre. | `200 OK` / `400` |
|  | `POST` | `/character/{characterId}/{noteId}/share/character/{targetCharacterId}` | Un personnage partage sa note avec un autre membre de la table. | `200 OK` / `400` |
|  | `POST` | `/character/{characterId}/{noteId}/share/campaign/{campaignId}`         | Un personnage partage sa note avec sa campagne (visible par le MJ). | `200 OK` / `400` |
| **Révocation** | `DELETE` | `/character/{characterId}/{noteId}/share/character/{targetCharacterId}` | Supprime le partage vers un autre personnage. | `204 No Content` |
|  | `DELETE` | `/character/{characterId}/{noteId}/share/campaign/{campaignId}`         | Supprime le partage vers la campagne. | `204 No Content` |
|  | `DELETE` | `/campaign/{campaignId}/{noteId}/share/character/{characterId}`         | Supprime le partage d'une note MJ vers un personnage. | `204 No Content` |
| **Copie** | `POST` | `/character/{characterId}/{noteId}/copy`                                | Copie une note accessible vers le contexte du personnage. | `200 OK` |
|  | `POST` | `/campaign/{campaignId}/{noteId}/copy`                                  | Copie une note accessible vers le contexte de la campagne. | `200 OK` |
| **Directory** | `PUT` | `/character/{characterId}/{noteId}/directory`                           | Modifie le répertoire. *(Si partagée, déclenche une auto-copie)*. | `200 OK` |
|  | `PUT` | `/campaign/{campaignId}/{noteId}/directory`                             | Modifie le répertoire d'une note de campagne. | `200 OK` |
| **Suppression** | `DELETE` | `/character/{characterId}/{noteId}`                                     | Supprime la note *(ou supprime le partage si reçue dans `shared`)*. | `204 No Content` |
|  | `DELETE` | `/campaign/{campaignId}/{noteId}`                                       | Supprime la note de campagne *(ou supprime le partage si reçue)*. | `204 No Content` |

---

## 3. Synthèse des Effets de Bord Technico-Métier

1. **Auto-copie sur déplacement (`PUT /directory`)** :
* Si la note déplacée est une note propriétaire (`ownerNote = true`), seul le champ `directory` de l'entité est mis à jour.
* Si la note est une note partagée reçue (dans `shared`), le backend instancie une nouvelle note appartenant au destinataire, affecte le nouveau `directory`, conserve le contenu initial et retourne la nouvelle `NoteResponseDto` avec son nouvel ID.


2. **Consultation et état de lecture (`GET /{id}`)** :
* La première lecture d'une note reçue via partage par un destinataire fait basculer le flag `read` de `false` à `true` en base de données.


3. **Dissociation Suppression vs Révocation (`DELETE`)** :
* Un appel sur `/character/{characterId}/{noteId}` par le **propriétaire** supprime la ressource et cascade la suppression de tous les partages associés.
* Un appel par un **destinataire** ne supprime que la ligne de jointure/partage associée à ce destinataire.