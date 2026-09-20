package io.github.quizup.leaderboard.infrastructure.out.metrics.adapter;

import io.github.quizup.leaderboard.domain.port.out.LeaderboardMetricsPort;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

/**
 * Adapter Micrometer du {@link LeaderboardMetricsPort}.
 * <p>Tags communs {@code application}/{@code environment}/{@code version} ajoutés par le SDK.
 */
@Component
public class LeaderboardMetricsAdapter implements LeaderboardMetricsPort {

    private final MeterRegistry registry;

    public LeaderboardMetricsAdapter(MeterRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void xpRecorded(String topicId, int xp) {
        Counter.builder("quizup.leaderboard.xp.recorded")
                .tag("topic", topicId == null || topicId.isBlank() ? "unknown" : topicId)
                .register(registry)
                .increment();

        DistributionSummary.builder("quizup.leaderboard.xp.amount")
                .register(registry)
                .record(Math.max(xp, 0));
    }
}
