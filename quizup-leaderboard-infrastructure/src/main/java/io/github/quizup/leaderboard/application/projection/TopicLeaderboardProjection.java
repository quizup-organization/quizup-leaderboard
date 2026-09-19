package io.github.quizup.leaderboard.application.projection;

import io.github.quizup.leaderboard.domain.event.LeaderboardEvent;
import io.github.quizup.leaderboard.domain.model.TopicLeaderboardEntry;
import io.github.quizup.leaderboard.domain.port.out.LeaderboardRepositoryPort;
import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * TopicLeaderboardProjection — maintient la projection read-only du classement
 * par thème (XP all-time + XP du mois courant).
 */
@Component
public class TopicLeaderboardProjection {

    private static final Logger logger = LoggerFactory.getLogger(TopicLeaderboardProjection.class);

    private final LeaderboardRepositoryPort leaderboardRepositoryPort;

    public TopicLeaderboardProjection(LeaderboardRepositoryPort leaderboardRepositoryPort) {
        this.leaderboardRepositoryPort = leaderboardRepositoryPort;
    }

    @EventHandler
    @Transactional
    public void on(LeaderboardEvent.XpRecordedEvent event) {
        TopicLeaderboardEntry current = leaderboardRepositoryPort
                .findByTopicAndUser(event.topicId(), event.userId())
                .orElseGet(() -> TopicLeaderboardEntry.empty(event.topicId(), event.userId()));

        int monthly = event.month().equals(current.month()) ? current.monthlyXp() : 0;

        leaderboardRepositoryPort.save(current.toBuilder()
                .totalXp(current.totalXp() + event.xp())
                .monthlyXp(monthly + event.xp())
                .month(event.month())
                .country(event.country())
                .updatedAt(event.recordedAt())
                .build());

        logger.info("Classement thème projeté: topicId={}, userId={}, +{} XP, total={}, mois={}",
                event.topicId(), event.userId(), event.xp(),
                current.totalXp() + event.xp(), event.month());
    }
}
