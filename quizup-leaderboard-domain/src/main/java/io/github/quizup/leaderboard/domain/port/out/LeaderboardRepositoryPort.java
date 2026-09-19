package io.github.quizup.leaderboard.domain.port.out;

import io.github.quizup.leaderboard.domain.model.TopicLeaderboardEntry;

import java.util.List;
import java.util.Optional;

/**
 * Port sortant — lecture/écriture de la projection de classement par thème.
 */
public interface LeaderboardRepositoryPort {

    void save(TopicLeaderboardEntry entry);

    Optional<TopicLeaderboardEntry> findByTopicAndUser(String topicId, String userId);

    /**
     * Top d'un thème, filtré par portée : {@code memberIds} (amis) et/ou
     * {@code country} (pays). {@code null} = pas de filtre (monde).
     */
    List<TopicLeaderboardEntry> findTop(
            String topicId,
            boolean monthly,
            String month,
            List<String> memberIds,
            String country,
            int limit
    );
}
