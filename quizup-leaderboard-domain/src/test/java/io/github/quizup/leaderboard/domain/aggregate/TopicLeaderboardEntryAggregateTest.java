package io.github.quizup.leaderboard.domain.aggregate;

import io.github.quizup.axon.test.QuizUpAxonMatchers;
import io.github.quizup.leaderboard.domain.command.LeaderboardCommand;
import io.github.quizup.leaderboard.domain.event.LeaderboardEvent;
import io.github.quizup.leaderboard.domain.model.LeaderboardRules;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.jupiter.api.Test;

import java.time.Instant;

class TopicLeaderboardEntryAggregateTest {

    private final AggregateTestFixture<TopicLeaderboardEntryAggregate> fixture =
            new AggregateTestFixture<>(TopicLeaderboardEntryAggregate.class);

    private static final String ENTRY_ID = LeaderboardRules.entryId("topic-1", "user-1");

    @Test
    void firstRecord_createsAggregateAndAppliesEvent() {
        fixture
                .givenNoPriorActivity()
                .when(new LeaderboardCommand.RecordXpCommand(
                        ENTRY_ID, "topic-1", "user-1", "game-1", 42, "2026-09", "FR", Instant.now()))
                .expectEventsMatching(QuizUpAxonMatchers.hasPayloadMatching(
                        LeaderboardEvent.XpRecordedEvent.class,
                        e -> {
                            LeaderboardEvent.XpRecordedEvent xp = (LeaderboardEvent.XpRecordedEvent) e;
                            return ENTRY_ID.equals(xp.entryId())
                                    && "topic-1".equals(xp.topicId())
                                    && "user-1".equals(xp.userId())
                                    && xp.xp() == 42
                                    && "2026-09".equals(xp.month());
                        }));
    }

    @Test
    void sameGameId_isIdempotent() {
        fixture
                .given(new LeaderboardEvent.XpRecordedEvent(
                        ENTRY_ID, "topic-1", "user-1", "game-1", 42, "2026-09", "FR", Instant.now()))
                .when(new LeaderboardCommand.RecordXpCommand(
                        ENTRY_ID, "topic-1", "user-1", "game-1", 42, "2026-09", "FR", Instant.now()))
                .expectNoEvents();
    }

    @Test
    void secondGame_appliesAnotherEvent() {
        fixture
                .given(new LeaderboardEvent.XpRecordedEvent(
                        ENTRY_ID, "topic-1", "user-1", "game-1", 42, "2026-09", "FR", Instant.now()))
                .when(new LeaderboardCommand.RecordXpCommand(
                        ENTRY_ID, "topic-1", "user-1", "game-2", 100, "2026-09", "FR", Instant.now()))
                .expectEventsMatching(QuizUpAxonMatchers.singlePayloadMatching(
                        LeaderboardEvent.XpRecordedEvent.class,
                        e -> "game-2".equals(((LeaderboardEvent.XpRecordedEvent) e).gameId())));
    }
}
