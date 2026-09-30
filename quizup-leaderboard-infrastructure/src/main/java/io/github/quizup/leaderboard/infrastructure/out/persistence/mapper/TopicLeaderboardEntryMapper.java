package io.github.quizup.leaderboard.infrastructure.out.persistence.mapper;

import io.github.quizup.leaderboard.domain.model.TopicLeaderboardEntry;
import io.github.quizup.leaderboard.infrastructure.out.persistence.entity.TopicLeaderboardEntryEntity;
import io.github.quizup.leaderboard.infrastructure.out.persistence.entity.TopicLeaderboardMonthlyEntryEntity;

/**
 * Mapper infrastructure — entités JPA (all-time, mensuelle) ⇄ {@link TopicLeaderboardEntry}.
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
                .monthlyXp(0)
                .month(null)
                .level(entity.getLevel())
                .pseudonym(entity.getPseudonym())
                .avatarOptions(entity.getAvatarOptions())
                .country(entity.getCountry())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static TopicLeaderboardEntry toDomain(TopicLeaderboardMonthlyEntryEntity entity) {
        return TopicLeaderboardEntry.builder()
                .entryId(entity.getEntryId())
                .topicId(entity.getTopicId())
                .userId(entity.getUserId())
                .totalXp(0)
                .monthlyXp(entity.getMonthlyXp())
                .month(entity.getMonth())
                .level(entity.getLevel())
                .pseudonym(entity.getPseudonym())
                .avatarOptions(entity.getAvatarOptions())
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
        entity.setLevel(entry.level());
        entity.setPseudonym(entry.pseudonym());
        entity.setAvatarOptions(entry.avatarOptions());
        entity.setCountry(entry.country());
        entity.setUpdatedAt(entry.updatedAt());
        return entity;
    }

    public static TopicLeaderboardMonthlyEntryEntity toMonthlyEntity(TopicLeaderboardEntry entry) {
        TopicLeaderboardMonthlyEntryEntity entity = new TopicLeaderboardMonthlyEntryEntity();
        entity.setEntryId(entry.entryId());
        entity.setTopicId(entry.topicId());
        entity.setUserId(entry.userId());
        entity.setMonth(entry.month());
        entity.setMonthlyXp(entry.monthlyXp());
        entity.setLevel(entry.level());
        entity.setPseudonym(entry.pseudonym());
        entity.setAvatarOptions(entry.avatarOptions());
        entity.setCountry(entry.country());
        entity.setUpdatedAt(entry.updatedAt());
        return entity;
    }
}
