package io.github.quizup.leaderboard.infrastructure.out.persistence.mapper;

import io.github.quizup.leaderboard.domain.model.TopicLeaderboardEntry;
import io.github.quizup.leaderboard.infrastructure.out.persistence.entity.TopicLeaderboardEntryEntity;

/**
 * Mapper infrastructure — TopicLeaderboardEntryEntity (JPA) ⇄ TopicLeaderboardEntry (domaine).
 */
public final class TopicLeaderboardEntryMapper {

    private TopicLeaderboardEntryMapper() {
    }

    public static TopicLeaderboardEntry toDomain(TopicLeaderboardEntryEntity entity) {
        return TopicLeaderboardEntry.builder()
                .entryId(entity.getEntryId())
                .topicId(entity.getTopicId())
                .userId(entity.getUserId())
                .totalXp(entity.getTotalXp())
                .monthlyXp(entity.getMonthlyXp())
                .month(entity.getMonth())
                .country(entity.getCountry())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static TopicLeaderboardEntryEntity toEntity(TopicLeaderboardEntry entry) {
        TopicLeaderboardEntryEntity entity = new TopicLeaderboardEntryEntity();
        entity.setEntryId(entry.entryId());
        entity.setTopicId(entry.topicId());
        entity.setUserId(entry.userId());
        entity.setTotalXp(entry.totalXp());
        entity.setMonthlyXp(entry.monthlyXp());
        entity.setMonth(entry.month());
        entity.setCountry(entry.country());
        entity.setUpdatedAt(entry.updatedAt());
        return entity;
    }
}
