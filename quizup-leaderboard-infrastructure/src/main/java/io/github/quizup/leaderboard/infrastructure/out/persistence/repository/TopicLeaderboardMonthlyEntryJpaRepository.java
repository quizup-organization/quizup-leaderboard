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
               set entry.pseudonym = :pseudonym
             where entry.userId = :userId
            """)
    int refreshPseudonym(@Param("userId") String userId,
                         @Param("pseudonym") String pseudonym);

    @Modifying
    @Query("""
            update TopicLeaderboardMonthlyEntryEntity entry
               set entry.country = :country
             where entry.userId = :userId
            """)
    int refreshCountry(@Param("userId") String userId,
                       @Param("country") String country);

    @Modifying
    @Query("""
            update TopicLeaderboardMonthlyEntryEntity entry
               set entry.avatarOptions = :avatarOptions
             where entry.userId = :userId
            """)
    int refreshAvatarOptions(@Param("userId") String userId,
                             @Param("avatarOptions") String avatarOptions);
}
