-- V2 : historique mensuel + identité/niveau dénormalisés
--
-- Le classement mensuel quitte `topic_leaderboard_entry` (où chaque nouveau mois
-- écrasait le précédent) pour une table dédiée par (thème, joueur, mois) : les mois
-- passés restent consultables. L'entrée all-time porte désormais l'identité publique
-- dénormalisée (pseudonym, avatar_options) et le niveau, rafraîchis par les
-- événements profile — plus aucune résolution de profil au fil des pages.

ALTER TABLE topic_leaderboard_entry
    ADD COLUMN IF NOT EXISTS pseudonym VARCHAR(255),
    ADD COLUMN IF NOT EXISTS avatar_options TEXT,
    ADD COLUMN IF NOT EXISTS level INTEGER NOT NULL DEFAULT 1;

UPDATE topic_leaderboard_entry
SET level = CASE
    WHEN total_xp <= 0 THEN 1
    ELSE (1 + floor(sqrt(total_xp / 100.0)))::int
END;

CREATE TABLE IF NOT EXISTS topic_leaderboard_monthly_entry (
    entry_id VARCHAR(550) PRIMARY KEY,
    topic_id VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    month VARCHAR(7) NOT NULL,
    monthly_xp INTEGER NOT NULL DEFAULT 0,
    level INTEGER NOT NULL DEFAULT 1,
    pseudonym VARCHAR(255),
    avatar_options TEXT,
    country VARCHAR(60),
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_topic_leaderboard_monthly_user UNIQUE (topic_id, user_id, month)
);

CREATE INDEX IF NOT EXISTS idx_topic_lb_monthly_xp
    ON topic_leaderboard_monthly_entry(topic_id, month, monthly_xp DESC);
CREATE INDEX IF NOT EXISTS idx_topic_lb_monthly_country
    ON topic_leaderboard_monthly_entry(topic_id, month, country, monthly_xp DESC);

INSERT INTO topic_leaderboard_monthly_entry
    (entry_id, topic_id, user_id, month, monthly_xp, level, pseudonym, avatar_options, country, updated_at)
SELECT topic_id || '::' || user_id || '::' || month,
       topic_id, user_id, month, monthly_xp, level, pseudonym, avatar_options, country, updated_at
FROM topic_leaderboard_entry
WHERE month IS NOT NULL AND monthly_xp > 0
ON CONFLICT (entry_id) DO NOTHING;

DROP INDEX IF EXISTS idx_topic_lb_monthly;

ALTER TABLE topic_leaderboard_entry
    DROP COLUMN IF EXISTS monthly_xp,
    DROP COLUMN IF EXISTS month;

COMMENT ON TABLE topic_leaderboard_entry IS 'Classement all-time par thème : XP cumulée + identité/niveau dénormalisés';
COMMENT ON TABLE topic_leaderboard_monthly_entry IS 'Classement mensuel par thème : XP du mois, historique conservé';
