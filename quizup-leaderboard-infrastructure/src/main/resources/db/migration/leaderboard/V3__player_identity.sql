-- V3 : identité publique locale
--
-- Le leaderboard ne résout plus l'identité publique par une query cross-service bloquante
-- dans ses projections (`queryGateway.query(...).join()` sur le thread de l'event processor,
-- anti-pattern documenté dans best-practices/.backend/anti-patterns.md).
-- L'identité (pseudonyme, pays, avatar) est dénormalisée localement et alimentée par les
-- événements du profil : ProfileCreatedEvent + événements de champ (pseudonyme, pays, avatar).

CREATE TABLE IF NOT EXISTS player_identity_entry (
    user_id VARCHAR(255) PRIMARY KEY,
    pseudonym VARCHAR(255),
    country VARCHAR(60),
    avatar_options TEXT,
    updated_at TIMESTAMP NOT NULL
);

COMMENT ON TABLE player_identity_entry IS
    'Identité publique locale (alimentée par les événements profile) — aucune query de profil dans les projections';
