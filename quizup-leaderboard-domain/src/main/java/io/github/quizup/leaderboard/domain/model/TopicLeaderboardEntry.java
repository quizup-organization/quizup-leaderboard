package io.github.quizup.leaderboard.domain.model;

import lombok.Builder;

import java.time.Instant;

/**
 * Modèle domaine (read model) d'une entrée de classement **par thème** :
 * XP all-time et XP du mois courant d'un joueur dans ce thème.
 */
@Builder(toBuilder = true)
public record TopicLeaderboardEntry(
        String entryId,
        String topicId,
        String userId,
        int totalXp,
        int monthlyXp,
        String month,
        Instant updatedAt,
        String displayName,
        String country
) {

    public static TopicLeaderboardEntry empty(String topicId, String userId) {
        return TopicLeaderboardEntry.builder()
                .entryId(LeaderboardRules.entryId(topicId, userId))
                .topicId(topicId)
                .userId(userId)
                .totalXp(0)
                .monthlyXp(0)
                .month(null)
                .updatedAt(Instant.now())
                .build();
    }

    public int level() {
        return LeaderboardRules.levelFor(totalXp);
    }
}
