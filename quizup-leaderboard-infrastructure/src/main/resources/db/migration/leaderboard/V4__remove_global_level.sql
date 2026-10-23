ALTER TABLE topic_leaderboard_entry
    DROP COLUMN IF EXISTS level;

ALTER TABLE topic_leaderboard_monthly_entry
    DROP COLUMN IF EXISTS level;
