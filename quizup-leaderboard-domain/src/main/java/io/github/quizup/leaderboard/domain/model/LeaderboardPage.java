package io.github.quizup.leaderboard.domain.model;

import lombok.Builder;

import java.util.List;

/**
 * Page de classement d'un thème (query dédiée, sans {@code SearchRequest}).
 */
@Builder(toBuilder = true)
public record LeaderboardPage(
        List<TopicLeaderboardEntry> entries,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
