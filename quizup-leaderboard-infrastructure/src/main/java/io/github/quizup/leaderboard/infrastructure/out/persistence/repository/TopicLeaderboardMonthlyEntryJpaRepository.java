package io.github.quizup.leaderboard.infrastructure.out.persistence.repository;

import io.github.quizup.leaderboard.infrastructure.out.persistence.entity.TopicLeaderboardMonthlyEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TopicLeaderboardMonthlyEntryJpaRepository
        extends JpaRepository<TopicLeaderboardMonthlyEntryEntity, String>,
        JpaSpecificationExecutor<TopicLeaderboardMonthlyEntryEntity> {

    Optional<TopicLeaderboardMonthlyEntryEntity> findByTopicIdAndUserIdAndMonth(String topicId,
                                                                                String userId,
                                                                                String month);

    @Modifying
    @Query("""
            update TopicLeaderboardMonthlyEntryEntity entry
               set entry.displayName = :displayName,
                   entry.country = :country,
                   entry.avatarOptions = :avatarOptions
             where entry.userId = :userId
            """)
    int refreshIdentity(@Param("userId") String userId,
                        @Param("displayName") String displayName,
                        @Param("country") String country,
                        @Param("avatarOptions") String avatarOptions);
}
