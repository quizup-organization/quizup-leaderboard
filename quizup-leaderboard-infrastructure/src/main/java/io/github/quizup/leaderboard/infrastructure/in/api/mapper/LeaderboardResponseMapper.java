package io.github.quizup.leaderboard.infrastructure.in.api.mapper;

import io.github.quizup.leaderboard.domain.model.LeaderboardRank;
import io.github.quizup.leaderboard.domain.model.TopicLeaderboardEntry;
import io.github.quizup.leaderboard.infrastructure.in.api.response.TopicLeaderboardEntryResponse;

import java.util.ArrayList;
import java.util.List;

public final class LeaderboardResponseMapper {

    private LeaderboardResponseMapper() {
    }

    public static TopicLeaderboardEntryResponse toResponse(TopicLeaderboardEntry entry, int rank) {
        return new TopicLeaderboardEntryResponse(
                rank,
                entry.topicId(),
                entry.userId(),
                entry.displayName(),
                entry.country(),
                entry.totalXp(),
                entry.monthlyXp(),
                entry.level()
        );
    }

    public static TopicLeaderboardEntryResponse toResponse(LeaderboardRank ranked) {
        return toResponse(ranked.entry(), ranked.rank());
    }

    public static List<TopicLeaderboardEntryResponse> toResponse(List<TopicLeaderboardEntry> entries) {
        List<TopicLeaderboardEntryResponse> responses = new ArrayList<>(entries.size());

        for (int i = 0; i < entries.size(); i++) {
            responses.add(toResponse(entries.get(i), i + 1));
        }

        return responses;
    }
}
