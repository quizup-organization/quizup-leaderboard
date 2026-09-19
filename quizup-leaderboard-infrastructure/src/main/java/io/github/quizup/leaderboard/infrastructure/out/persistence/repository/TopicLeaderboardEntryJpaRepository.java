package io.github.quizup.leaderboard.infrastructure.out.persistence.repository;

import io.github.quizup.leaderboard.infrastructure.out.persistence.entity.TopicLeaderboardEntryEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface TopicLeaderboardEntryJpaRepository
        extends JpaRepository<TopicLeaderboardEntryEntity, String> {

    Optional<TopicLeaderboardEntryEntity> findByTopicIdAndUserId(String topicId, String userId);

    @Query("""
            select e from TopicLeaderboardEntryEntity e
            where e.topicId = :topicId
            order by e.totalXp desc
            """)
    List<TopicLeaderboardEntryEntity> findTopByTopicAllTime(@Param("topicId") String topicId, Pageable pageable);

    @Query("""
            select e from TopicLeaderboardEntryEntity e
            where e.topicId = :topicId and e.month = :month
            order by e.monthlyXp desc
            """)
    List<TopicLeaderboardEntryEntity> findTopByTopicMonthly(
            @Param("topicId") String topicId,
            @Param("month") String month,
            Pageable pageable);

    @Query("""
            select e from TopicLeaderboardEntryEntity e
            where e.topicId = :topicId and e.userId in :userIds
            order by e.totalXp desc
            """)
    List<TopicLeaderboardEntryEntity> findTopByTopicAllTimeForUsers(
            @Param("topicId") String topicId,
            @Param("userIds") Collection<String> userIds,
            Pageable pageable);

    @Query("""
            select e from TopicLeaderboardEntryEntity e
            where e.topicId = :topicId and e.month = :month and e.userId in :userIds
            order by e.monthlyXp desc
            """)
    List<TopicLeaderboardEntryEntity> findTopByTopicMonthlyForUsers(
            @Param("topicId") String topicId,
            @Param("month") String month,
            @Param("userIds") Collection<String> userIds,
            Pageable pageable);

    @Query("""
            select e from TopicLeaderboardEntryEntity e
            where e.topicId = :topicId and e.country = :country
            order by e.totalXp desc
            """)
    List<TopicLeaderboardEntryEntity> findTopByTopicAllTimeForCountry(
            @Param("topicId") String topicId,
            @Param("country") String country,
            Pageable pageable);

    @Query("""
            select e from TopicLeaderboardEntryEntity e
            where e.topicId = :topicId and e.month = :month and e.country = :country
            order by e.monthlyXp desc
            """)
    List<TopicLeaderboardEntryEntity> findTopByTopicMonthlyForCountry(
            @Param("topicId") String topicId,
            @Param("month") String month,
            @Param("country") String country,
            Pageable pageable);
}
