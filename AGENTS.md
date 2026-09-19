# AGENTS.md — quizup-leaderboard

> Service de **classements par thème** : all-time (progression cumulée) et mensuel
> (reset au 1er du mois). Architecture : Axon Framework (CQRS/EDA) + JPA (projection).
> Pour les règles de patterns :
> [`../../best-practices/.backend/hexagonal-architecture.md`](../../best-practices/.backend/hexagonal-architecture.md).

---

## 1. Rôle

Classement d'un joueur **dans un thème** : chaque thème a son propre classement (être
excellent en géographie ne fait pas monter au classement cinéma). L'XP d'un duel
(`XpAwardedEvent` de `quizup-profile`) alimente l'entrée `(thème, joueur)`.

Deux horizons :
- **All-time** : XP cumulée dans le thème (détermine le niveau et la position).
- **Mensuel** : XP du mois courant, réinitialisée au changement de mois (clé `YYYY-MM`).

Portées : **monde**, **abonnements** (`scope=following` / alias `friends`) et **pays** —
abonnements via `quizup-social` (`UserFollowerQuery.GetFollowingIdsQuery`), pays via le profil.

**Package** : `io.github.quizup.leaderboard` · **Port** : `8091` · **DB** : `quizup_leaderboard`

---

## 2. Endpoints REST

### `LeaderboardController` — `/api/leaderboard` (`@CrossOrigin`)

| Méthode | Chemin                                      | Handler                     | Response                                 |
|---------|---------------------------------------------|-----------------------------|------------------------------------------|
| GET     | `/api/leaderboard/topics/{topicId}`         | `topByTopic(...)`           | `List<TopicLeaderboardEntryResponse>`    |
| GET     | `/api/leaderboard/topics/{topicId}/me`      | `myRank(...)`               | `TopicLeaderboardEntryResponse` (204 si aucun duel dans le thème) |

Query params : `period` = `all-time` (défaut) · `monthly` ; `limit` (1–100, défaut 50).

**DTO** : `TopicLeaderboardEntryResponse(rank, topicId, userId, totalXp, monthlyXp, level)`
— le nom d'affichage n'est pas renvoyé : le frontend le résout via `quizup-profile`.

---

## 3. Use cases (ports entrants — `domain/port/in/`)

- `GetLeaderboardUseCase` — top d'un thème (all-time/mensuel) + rang d'un joueur.

**Queries** (`LeaderboardQuery.java`) : `TopByTopicQuery`, `GetTopicRankQuery`.
**Ports sortants locaux** : `LeaderboardRepositoryPort` (projection).

---

## 4. Dépendances inter-services

| Port / consommateur | Service source    | Event Axon consommé            |
|---------------------|-------------------|--------------------------------|
| `LeaderboardSaga`   | `quizup-profile`  | `ProgressionEvent.XpAwardedEvent` |

Dépendance Maven `quizup-profile-domain` (artifact). La saga envoie un
`LeaderboardCommand.RecordXpCommand` (idempotent par `gameId`) ; l'agrégat
`TopicLeaderboardEntryAggregate` est identifié par `topicId::userId` (namespacé).

### Enrichissements front

- **Portées** `scope=world|following|country` (alias historique `friends`) : abonnements via
  `LeaderboardFollowingPort` (`quizup-social-domain`, `SearchUserFollowerQuery` filtrée sur
  `followerId`), pays via `country` dénormalisé sur l'entrée (résolu par la saga via
  `LeaderboardProfilePort`). Le rang est recalculé dans la portée (scan borné).
- `TopicLeaderboardEntryResponse` renvoie `displayName` + `country` (résolus via profile).
