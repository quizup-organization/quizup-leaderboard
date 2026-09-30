package io.github.quizup.leaderboard.domain.model;

import lombok.Builder;

import java.time.Instant;

/**
 * Modèle domaine (read model) d'une entrée de classement **par thème**.
 *
 * <p>Deux lignes par joueur : l'entrée all-time ({@code month == null}, {@code totalXp} + identité
 * dénormalisée) et une entrée par mois ({@code month != null}, {@code monthlyXp}) qui conserve
 * l'historique des mois passés. {@code level} est dénormalisé (niveau = f(totalXp) au dernier
 * enregistrement) pour que les pages mensuelles n'aient pas à résoudre la progression.</p>
 */
@Builder(toBuilder = true)
public record TopicLeaderboardEntry(
        String entryId,
        String topicId,
        String userId,
        int totalXp,
        int monthlyXp,
        String month,
        int level,
        Instant updatedAt,
        String pseudonym,
        String avatarOptions,
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
                .level(1)
                .updatedAt(Instant.now())
                .build();
    }

    public static TopicLeaderboardEntry emptyMonthly(String topicId, String userId, String month) {
        return TopicLeaderboardEntry.builder()
                .entryId(LeaderboardRules.monthlyEntryId(topicId, userId, month))
                .topicId(topicId)
                .userId(userId)
                .totalXp(0)
                .monthlyXp(0)
                .month(month)
                .level(1)
                .updatedAt(Instant.now())
                .build();
    }
}
