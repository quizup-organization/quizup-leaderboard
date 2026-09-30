package io.github.quizup.leaderboard.infrastructure.out.persistence.repository;

import io.github.quizup.leaderboard.infrastructure.out.persistence.entity.TopicLeaderboardEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TopicLeaderboardEntryJpaRepository
        extends JpaRepository<TopicLeaderboardEntryEntity, String>,
        JpaSpecificationExecutor<TopicLeaderboardEntryEntity> {

    Optional<TopicLeaderboardEntryEntity> findByTopicIdAndUserId(String topicId, String userId);

    @Modifying
    @Query("""
            update TopicLeaderboardEntryEntity entry
               set entry.pseudonym = :pseudonym
             where entry.userId = :userId
            """)
    int refreshPseudonym(@Param("userId") String userId,
                         @Param("pseudonym") String pseudonym);

    @Modifying
    @Query("""
            update TopicLeaderboardEntryEntity entry
               set entry.country = :country
             where entry.userId = :userId
            """)
    int refreshCountry(@Param("userId") String userId,
                       @Param("country") String country);

    @Modifying
    @Query("""
            update TopicLeaderboardEntryEntity entry
               set entry.avatarOptions = :avatarOptions
             where entry.userId = :userId
            """)
    int refreshAvatarOptions(@Param("userId") String userId,
                             @Param("avatarOptions") String avatarOptions);
}
