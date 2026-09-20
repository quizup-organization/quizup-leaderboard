package io.github.quizup.leaderboard.application.handler.event;

import io.github.quizup.leaderboard.domain.event.LeaderboardEvent;
import io.github.quizup.leaderboard.domain.port.out.LeaderboardMetricsPort;
import org.axonframework.eventhandling.DisallowReplay;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

/**
 * Alimente les KPI métier du classement à partir de {@code XpRecordedEvent}.
 * <p>{@link DisallowReplay} : un replay ne réincrémente pas les compteurs.
 */
@Component
public class LeaderboardMetricsEventHandler {

    private final LeaderboardMetricsPort metrics;

    public LeaderboardMetricsEventHandler(LeaderboardMetricsPort metrics) {
        this.metrics = metrics;
    }

    @EventHandler
    @DisallowReplay
    public void on(LeaderboardEvent.XpRecordedEvent event) {
        metrics.xpRecorded(event.topicId(), event.xp());
    }
}
