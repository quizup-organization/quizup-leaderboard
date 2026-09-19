package io.github.quizup.leaderboard.domain.model;

/**
 * Entrée de classement accompagnée de son rang (1-based) dans le thème.
 */
public record LeaderboardRank(
        TopicLeaderboardEntry entry,
        int rank
) {
}
