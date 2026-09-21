-- V1: Création des tables du classement par thème
--
-- Projection read-only du classement d'un joueur dans un thème :
-- XP all-time et XP du mois courant (reset au 1er du mois).

CREATE TABLE IF NOT EXISTS topic_leaderboard_entry (
    entry_id VARCHAR(520) PRIMARY KEY,
    topic_id VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    total_xp INTEGER NOT NULL DEFAULT 0,
    monthly_xp INTEGER NOT NULL DEFAULT 0,
    month VARCHAR(7),
    country VARCHAR(60),
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_topic_leaderboard_user UNIQUE (topic_id, user_id)
);

CREATE INDEX IF NOT EXISTS idx_topic_lb_all_time ON topic_leaderboard_entry(topic_id, total_xp DESC);
CREATE INDEX IF NOT EXISTS idx_topic_lb_monthly ON topic_leaderboard_entry(topic_id, month, monthly_xp DESC);
CREATE INDEX IF NOT EXISTS idx_topic_lb_country ON topic_leaderboard_entry(topic_id, country, total_xp DESC);

COMMENT ON TABLE topic_leaderboard_entry IS 'Classement par thème : XP all-time et XP du mois par (thème, joueur)';

-- Journal des XP de classement déjà pris en compte (idempotence au rejeu).
CREATE TABLE IF NOT EXISTS topic_leaderboard_awarded_game (
    topic_id VARCHAR(255) NOT NULL,
    user_id  VARCHAR(255) NOT NULL,
    game_id  VARCHAR(255) NOT NULL,
    PRIMARY KEY (topic_id, user_id, game_id)
);
