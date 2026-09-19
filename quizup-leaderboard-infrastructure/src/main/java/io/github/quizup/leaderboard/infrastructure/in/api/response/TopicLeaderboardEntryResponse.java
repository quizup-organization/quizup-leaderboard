package io.github.quizup.leaderboard.infrastructure.in.api.response;

import java.io.Serializable;

/**
 * DTO d'une entrée de classement par thème.
 */
public record TopicLeaderboardEntryResponse(
        int rank,
        String topicId,
        String userId,
        String displayName,
        String country,
        int totalXp,
        int monthlyXp,
        int level
) implements Serializable {
}
