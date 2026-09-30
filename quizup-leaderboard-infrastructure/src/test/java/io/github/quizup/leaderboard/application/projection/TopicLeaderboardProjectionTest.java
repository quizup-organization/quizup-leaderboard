package io.github.quizup.leaderboard.application.projection;

import io.github.quizup.leaderboard.domain.model.PlayerIdentity;
import io.github.quizup.leaderboard.domain.model.TopicLeaderboardEntry;
import io.github.quizup.leaderboard.domain.port.out.LeaderboardAwardedGameRepositoryPort;
import io.github.quizup.leaderboard.domain.port.out.LeaderboardProfilePort;
import io.github.quizup.leaderboard.domain.port.out.LeaderboardRepositoryPort;
import io.github.quizup.profile.domain.event.ProfileEvent;
import io.github.quizup.profile.domain.event.ProgressionEvent;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class TopicLeaderboardProjectionTest {

    private final LeaderboardRepositoryPort repository = mock(LeaderboardRepositoryPort.class);
    private final LeaderboardAwardedGameRepositoryPort awardedGames = mock(LeaderboardAwardedGameRepositoryPort.class);
    private final LeaderboardProfilePort profiles = mock(LeaderboardProfilePort.class);

    private final TopicLeaderboardProjection projection =
            new TopicLeaderboardProjection(repository, awardedGames, profiles);

    @Test
    void botGame_isNotRanked() {
        projection.on(xpAwarded(true));

        verifyNoInteractions(awardedGames, profiles);
        verify(repository, never()).saveAllTime(any());
        verify(repository, never()).saveMonthly(any());
    }

    @Test
    void firstAward_writesAllTimeAndMonthlyEntries() {
        when(awardedGames.record("topic-1", "user-1", "game-1")).thenReturn(true);
        when(profiles.findIdentities(List.of("user-1")))
                .thenReturn(List.of(new PlayerIdentity("user-1", "Alicia", "FR", "{\"hair\":\"full\"}")));
        when(repository.findByTopicAndUser("topic-1", "user-1")).thenReturn(Optional.empty());
        when(repository.findMonthlyByTopicAndUser("topic-1", "user-1", "2026-09")).thenReturn(Optional.empty());

        projection.on(xpAwarded(false));

        ArgumentCaptor<TopicLeaderboardEntry> allTime = ArgumentCaptor.forClass(TopicLeaderboardEntry.class);
        verify(repository).saveAllTime(allTime.capture());
        assertThat(allTime.getValue().totalXp()).isEqualTo(150);
        assertThat(allTime.getValue().level()).isEqualTo(2);
        assertThat(allTime.getValue().pseudonym()).isEqualTo("Alicia");
        assertThat(allTime.getValue().avatarOptions()).isEqualTo("{\"hair\":\"full\"}");
        assertThat(allTime.getValue().country()).isEqualTo("FR");

        ArgumentCaptor<TopicLeaderboardEntry> monthly = ArgumentCaptor.forClass(TopicLeaderboardEntry.class);
        verify(repository).saveMonthly(monthly.capture());
        assertThat(monthly.getValue().month()).isEqualTo("2026-09");
        assertThat(monthly.getValue().monthlyXp()).isEqualTo(150);
        assertThat(monthly.getValue().level()).isEqualTo(2);
    }

    @Test
    void replay_doesNotDoubleCount() {
        when(awardedGames.record("topic-1", "user-1", "game-1")).thenReturn(false);

        projection.on(xpAwarded(false));

        verify(repository, never()).saveAllTime(any());
        verify(repository, never()).saveMonthly(any());
    }

    @Test
    void unresolvedProfile_keepsStoredIdentity() {
        when(awardedGames.record("topic-1", "user-1", "game-1")).thenReturn(true);
        when(profiles.findIdentities(List.of("user-1"))).thenReturn(List.of());
        TopicLeaderboardEntry stored = TopicLeaderboardEntry.builder()
                .entryId("topic-1::user-1")
                .topicId("topic-1")
                .userId("user-1")
                .totalXp(30)
                .level(1)
                .pseudonym("Ancien nom")
                .country("BE")
                .updatedAt(Instant.parse("2026-09-01T00:00:00Z"))
                .build();
        when(repository.findByTopicAndUser("topic-1", "user-1")).thenReturn(Optional.of(stored));
        when(repository.findMonthlyByTopicAndUser("topic-1", "user-1", "2026-09")).thenReturn(Optional.empty());

        projection.on(xpAwarded(false));

        ArgumentCaptor<TopicLeaderboardEntry> allTime = ArgumentCaptor.forClass(TopicLeaderboardEntry.class);
        verify(repository).saveAllTime(allTime.capture());
        assertThat(allTime.getValue().pseudonym()).isEqualTo("Ancien nom");
        assertThat(allTime.getValue().country()).isEqualTo("BE");
        assertThat(allTime.getValue().totalXp()).isEqualTo(180);
    }

    @Test
    void profilePseudonymUpdated_refreshesDenormalizedPseudonym() {
        projection.on(new ProfileEvent.ProfilePseudonymUpdatedEvent(
                "user-1", "user-1", "Alicia", Instant.parse("2026-09-20T10:00:00Z")));

        verify(repository).refreshPseudonym("user-1", "Alicia");
    }

    @Test
    void profileCountryUpdated_refreshesDenormalizedCountry() {
        projection.on(new ProfileEvent.ProfileCountryUpdatedEvent(
                "user-1", "user-1", "FR", Instant.parse("2026-09-20T10:00:00Z")));

        verify(repository).refreshCountry("user-1", "FR");
    }

    @Test
    void profileAvatarUpdated_refreshesDenormalizedAvatar() {
        projection.on(new ProfileEvent.ProfileAvatarUpdatedEvent(
                "user-1", "user-1", "{\"hair\":\"full\"}", Instant.parse("2026-09-20T10:00:00Z")));

        verify(repository).refreshAvatarOptions("user-1", "{\"hair\":\"full\"}");
    }

    private ProgressionEvent.XpAwardedEvent xpAwarded(boolean botGame) {
        return new ProgressionEvent.XpAwardedEvent(
                "user-1", "game-1", "topic-1", 150, 100, true, false, botGame, 5, 3,
                Instant.parse("2026-09-15T10:00:00Z"));
    }
}
