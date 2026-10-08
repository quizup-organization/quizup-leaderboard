# AGENTS.md — quizup-leaderboard

> Service de **classements par thème** : all-time (progression cumulée) et mensuel
> (reset au 1er du mois). Architecture : Axon Framework (CQRS/EDA) + JPA (projection).
> Pour les règles de patterns :
> [`../../best-practices/.backend/folder-structure.md`](../../best-practices/.backend/folder-structure.md).

---

## 1. Rôle

Classement d'un joueur **dans un thème** : chaque thème a son propre classement (être
excellent en géographie ne fait pas monter au classement cinéma). L'XP d'un duel
(`XpAwardedEvent` de `quizup-profile`) alimente l'entrée `(thème, joueur)`.

Deux horizons :
- **All-time** : XP cumulée dans le thème (détermine le niveau et la position).
- **Mensuel** : XP du mois (`YYYY-MM`), **historique conservé** — chaque mois a sa propre ligne,
  consultable via le paramètre `month` de la surface BFF.

Portées : **monde**, **abonnements** (`scope=following` / alias `friends`) et **pays** —
abonnements via `quizup-social` (`UserFollowerQuery.GetFollowingIdsQuery`), pays via le profil.

**Package** : `io.github.quizup.leaderboard` · **Port** : `8091` · **DB** : `quizup_leaderboard`

---

## 2. Surface (headless)

Service **headless** : aucun contrôleur REST ni WebSocket. La surface applicative unique est le
**`quizup-bff`** (`/api/**` + `/ws`) ; il interroge ce service via le **query bus** Axon et consomme
ses événements. Les handlers de requête/commande, sagas et projections restent la seule surface
exposée par le service.
## 3. Use cases (ports entrants — `domain/port/in/`)

- `GetLeaderboardUseCase` — top d'un thème (all-time/mensuel) + rang d'un joueur.

**Queries** (`LeaderboardQuery.java`) : `TopByTopicQuery`, `GetTopicRankQuery`.
**Ports sortants locaux** : `LeaderboardRepositoryPort` (projection).

---

## 4. Dépendances inter-services

| Consommateur                  | Service source    | Event Axon consommé                 |
|-------------------------------|-------------------|-------------------------------------|
| `TopicLeaderboardProjection`  | `quizup-profile`  | `ProgressionEvent.XpAwardedEvent`   |
| `TopicLeaderboardProjection`  | `quizup-profile`  | `ProfileEvent.ProfilePseudonymUpdatedEvent` |
| `TopicLeaderboardProjection`  | `quizup-profile`  | `ProfileEvent.ProfileCountryUpdatedEvent`   |
| `TopicLeaderboardProjection`  | `quizup-profile`  | `ProfileEvent.ProfileAvatarUpdatedEvent`    |

Dépendance Maven `quizup-profile-domain` (artifact). **Plus de saga ni d'agrégat relais** : la
projection consomme directement les événements profile (pattern `ActivityProjection`), écrit
l'entrée all-time + l'entrée mensuelle, et l'idempotence par `(topicId, userId, gameId)` est portée
par le journal `topic_leaderboard_awarded_game`. Aucun événement local n'est produit : l'event
store du service ne croît plus avec les duels (lot C3, rétention résolue par suppression de
l'écriture inutile).

**Les duels contre bot (`XpAwardedEvent.botGame`) sont ignorés** : ils comptent pour l'XP, le
niveau et les badges du joueur, jamais pour le classement.

### Modèle de lecture (lot C3)

- **Historique mensuel** : table `topic_leaderboard_monthly_entry` par `(thème, joueur, mois)` —
  chaque mois a sa ligne, jamais écrasée. `TopByTopicQuery`/`GetTopicRankQuery` prennent un
  `month` (`YYYY-MM`), `null` = mois courant. Backfill du mois courant à la migration V2.
- **Identité/niveau dénormalisés** : `pseudonym`, `avatar_options`, `country` et `level` sont
  stockés sur les entrées (all-time et mensuelles) — plus aucune résolution de profil par page
  côté BFF. Les événements de champ du profil rafraîchissent l'identité sur toutes les entrées du
  joueur (`refreshPseudonym` / `refreshCountry` / `refreshAvatarOptions`), le pays ne fige donc
  jamais ; le niveau est recalculé à chaque XP.

### Enrichissements front

- **Portées** `scope=world|following|country` (alias historique `friends`) : abonnements via
  `LeaderboardFollowingPort` (`quizup-social-domain`, query dédiée `GetUserFollowsQuery`), pays
  filtré sur le `country` dénormalisé ; la portée pays résout uniquement le pays du demandeur via
  `LeaderboardProfilePort` (requête batch `GetProfilesByIdsQuery`).
- **Pagination et rang sans scan borné** : `TopByTopicQuery(topicId, monthly, month, page, size,
  memberIds, country)` → `LeaderboardPage` (Specification dynamique) ; `GetTopicRankQuery` renvoie
  le rang **au-delà du top 100** via un `COUNT` des entrées classées avant le joueur
  (XP supérieure, départage `userId` identique à l'ordre de la page).
- `SearchLobbyQuery`/recherches restent pour les futures surfaces d'administration.
