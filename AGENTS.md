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
