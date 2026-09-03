# Règles métier de l'API Notes

## 1. Propriété d'une note

Une note appartient **obligatoirement à un seul propriétaire** :

* soit une **Campaign** ;
* soit un **Character**.

Une note **ne peut jamais appartenir simultanément** à une campagne et à un character.

L'auteur technique (`DomainUser`) n'est donc pas considéré comme le propriétaire métier de la note.

### Note de campagne

Une note appartenant à une campagne est une note du **MJ**.

Elle est initialement **privée au MJ**.

### Note de character

Une note appartenant à un character est une note du **joueur propriétaire de ce character**.

Elle est initialement **privée au character**.

---

# 2. Accès à une note

Une personne peut accéder à une note dans deux situations :

### Elle est propriétaire

Elle peut toujours :

* consulter la note ;
* modifier la note ;
* la partager ;
* la copier ;
* l'organiser dans son arborescence.

### La note lui est partagée

Elle peut :

* consulter la note ;
* la copier.

Elle **ne peut pas modifier la note originale**.

Elle **ne peut pas modifier les partages de la note originale**.

Elle peut en revanche créer sa propre copie.

---

# 3. Partage d'une note de campagne

Une note appartenant à une campagne appartient donc au MJ.

Le MJ peut partager cette note avec **un ou plusieurs characters de cette même campagne**.

Il n'est pas possible de partager une note de campagne avec :

* un character extérieur à la campagne ;
* un character appartenant à une autre campagne.

Le partage est donc toujours contrôlé par l'appartenance :

> `Character ∈ Campaign`

Le MJ peut donc par exemple partager :

> « Informations sur le village »

avec :

* Character A ;
* Character B ;

mais pas avec Character C qui appartient à une autre campagne.

---

# 4. Partage d'une note de character

Une note appartenant à un character peut être partagée de deux manières.

### Vers un autre character

Le propriétaire peut partager la note avec **un ou plusieurs autres characters appartenant à la même campagne**.

Il n'est donc pas possible de partager directement la note avec :

* un character extérieur à la campagne ;
* un character appartenant à une autre campagne.

Le propriétaire n'a évidemment pas besoin de partager la note avec lui-même.

### Vers la campagne

Un character peut également partager une note avec **la campagne à laquelle il appartient**.

Mais ce partage possède une règle particulière :

> Une note de character partagée avec la campagne est visible par **le MJ de cette campagne uniquement**.

Elle n'est **pas automatiquement visible par tous les characters** de la campagne.

---

# 5. Partager une note de character avec toute la table

Si un joueur souhaite rendre sa note accessible à **tout le monde autour de la table**, il doit donc partager la note :

* avec la campagne → pour le MJ ;
* avec les autres characters → pour les joueurs.

La campagne n'est donc **pas un groupe de partage implicite pour les characters**.

C'est volontairement explicite.

Cela permet par exemple :

> Note de Character A
> → partagée avec Campaign X
> → visible par le MJ
>
> → partagée avec Character B
> → visible par B
>
> → partagée avec Character C
> → visible par C

Mais Character D ne la voit pas tant qu'il n'est pas lui aussi destinataire.

---

# 6. Cohérence des destinataires

Les partages doivent respecter la campagne d'origine de la note.

### Note appartenant à une Campaign

Elle ne peut être partagée qu'avec des **characters membres de cette Campaign**.

### Note appartenant à un Character

Le character doit lui-même appartenir à une Campaign.

Il peut alors partager :

* avec la Campaign à laquelle il appartient ;
* avec des Characters appartenant à cette même Campaign.

Un character ne peut donc pas utiliser le système de partage pour transmettre une note à quelqu'un qui n'est pas autour de sa table.

---

# 7. Lecture des notes

Pour un character, la liste des notes accessibles contient :

* ses propres notes ;
* les notes de campagne qui lui ont été explicitement partagées ;
* les notes d'autres characters qui lui ont été explicitement partagées.

Pour une campagne/MJ, la liste contient :

* ses propres notes de campagne ;
* les notes de characters qui ont explicitement partagé leur note avec la campagne.

Une note simplement présente dans une campagne **n'est pas automatiquement visible par tous les characters**.

---

# 8. `NoteType`

Chaque note possède un type :

* `LOCATION`
* `NPC`
* `QUEST`
* `FREE_NOTE`
* `MAP`
* `BACKGROUND`

Le type constitue une **catégorie métier**.

La récupération des notes se fait par :

* campagne + type ;
* character + type.

Il n'y a pas de besoin métier de récupérer « toutes les notes » sans type.

---

# 9. Partage et statut de lecture

Un partage possède également un état `read`.

Cela permet de distinguer :

* une note partagée mais jamais consultée ;
* une note partagée qui a déjà été consultée.

Une note passe à `read = true` lorsque le destinataire **ouvre réellement la note**.

Le simple fait qu'elle apparaisse dans une liste de notes ne suffit pas.

Cela permet notamment au front d'afficher les nouvelles notes partagées différemment des anciennes.

---

# 10. Répertoire des notes

Chaque note possède également un `directory`.

Il représente son emplacement dans l'arborescence personnelle du propriétaire.

Par exemple :

> `NPC/Sanctuary`

permet au front de construire une arborescence :

```text
NPC
└── Sanctuary
    └── Preston Garvey
```

Le `NoteType` et le `directory` ont donc deux rôles différents :

* **NoteType** → catégorie métier ;
* **directory** → organisation personnelle.

Le directory peut être modifié par le propriétaire, notamment lors d'un déplacement en drag & drop.

---

# 11. Organisation des notes partagées

Une note reçue d'un autre propriétaire n'est pas déplacée dans l'arborescence originale.

Pour le destinataire, elle apparaît virtuellement dans :

> `shared`

Le directory original de la note reste inchangé.

Autrement dit :

> Propriétaire
> `NPC/Sanctuary/Preston`

> Destinataire
> `shared/Preston`

Le `shared` est donc **une représentation propre au destinataire**, pas une modification de la note originale.

---

# 12. Déplacement d'une note partagée

Si le destinataire déplace une note reçue depuis `shared` vers son arborescence personnelle :

> **il ne modifie pas la note originale.**

Cela entraîne la création d'une **copie appartenant au destinataire**.

Par exemple :

> Note originale
> Character A → `NPC/Sanctuary`

Character B la reçoit dans :

> `shared`

B la déplace vers :

> `NPC/Settlers`

Résultat :

* la note de A reste inchangée ;
* le partage existe toujours ;
* B possède maintenant sa propre copie dans `NPC/Settlers`.

---

# 13. Suppression d'une note partagée

Si un destinataire supprime une note depuis son espace `shared`, il ne supprime **pas la note originale**.

Il supprime son **partage à lui**.

Cela permet notamment d'éviter qu'un espace `shared` accumule indéfiniment les notes reçues.

La suppression depuis `shared` signifie donc :

> « Je ne veux plus avoir accès à cette note. »

et non :

> « Détruire la note. »

---

# 14. Copie d'une note

**Toute personne ayant accès à une note peut la copier.**

Cela concerne aussi bien :

* le propriétaire ;
* un destinataire d'un partage.

La copie devient une **nouvelle note indépendante**.

Et surtout :

> la copie appartient au contexte de celui qui la crée.

Donc :

### MJ copie une note

→ nouvelle note appartenant à sa Campaign.

### Character copie une note

→ nouvelle note appartenant à son Character.

La copie conserve les informations utiles de la note originale, notamment son contenu et son type, mais elle devient une note indépendante.

Les partages de la note originale ne sont pas transférés automatiquement à la copie.

---

# 15. Modification d'une note

Seul le **propriétaire** peut modifier une note.

Ainsi :

* MJ → peut modifier ses notes de campagne ;
* Character → peut modifier ses notes ;
* destinataire d'un partage → **ne peut pas modifier l'original**.

Un destinataire qui souhaite modifier le contenu d'une note partagée doit d'abord en faire une copie.

---

# 16. Suppression d'une note

La suppression d'une note originale est réservée à son propriétaire.

La suppression d'une note partagée par un destinataire correspond à la suppression de **son accès/partage**, pas à la suppression de l'original.

---

# 17. Appartenance des characters aux campagnes

Un character ne peut appartenir qu'à **une seule campagne**.

Cette règle est également garantie au niveau BDD.

Cela simplifie plusieurs règles métier :

* déterminer la campagne d'un character ;
* vérifier qu'un partage est autorisé ;
* savoir à quelle campagne appartient une note de character ;
* déterminer les characters avec lesquels un partage est possible.

---

# 18. Résumé des matrices de partage

| Propriétaire  | Destinataire autorisé          | Résultat               |
| ------------- | ------------------------------ | ---------------------- |
| **Campaign**  | Character de cette campagne    | Le character peut lire |
| **Campaign**  | Character d'une autre campagne | ❌ Interdit             |
| **Character** | Character de sa campagne       | Le character peut lire |
| **Character** | Character d'une autre campagne | ❌ Interdit             |
| **Character** | Sa Campaign                    | Le MJ peut lire        |
| **Character** | Campaign d'une autre campagne  | ❌ Interdit             |

Et surtout :

> **Campaign → Character ≠ Campaign → tous les Characters**

Le partage est toujours explicite et ciblé.

---

### La règle fondamentale

Je pense qu'on peut résumer tout le modèle avec cette phrase :

> **Une note appartient soit à une Campaign, soit à un Character. Son propriétaire est le seul à pouvoir la modifier et la partager. Les autres utilisateurs n'obtiennent qu'un droit de lecture et de copie via un partage explicitement autorisé par les relations d'appartenance à la campagne.**

Et ça donne un modèle assez propre : **propriété → accès → partage → copie**, avec le `directory` qui reste une problématique d'organisation personnelle par-dessus.
