package io.github.quizup.leaderboard.application.handler.query;

import io.github.quizup.leaderboard.domain.model.LeaderboardPage;
import io.github.quizup.leaderboard.domain.model.LeaderboardRank;
import io.github.quizup.leaderboard.domain.model.TopicLeaderboardEntry;
import io.github.quizup.leaderboard.domain.port.out.LeaderboardRepositoryPort;
import io.github.quizup.leaderboard.domain.query.LeaderboardQuery;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LeaderboardQueryHandlerTest {

    private final LeaderboardRepositoryPort repository = mock(LeaderboardRepositoryPort.class);
    private final LeaderboardQueryHandler handler = new LeaderboardQueryHandler(repository);

    @Test
    void page_delegates_scope_and_pagination() {
        TopicLeaderboardEntry entry = TopicLeaderboardEntry.builder()
                .entryId("topic-1::user-1")
                .topicId("topic-1")
                .userId("user-1")
                .totalXp(120)
                .build();
        LeaderboardPage page = LeaderboardPage.builder()
                .entries(List.of(entry))
                .page(2)
                .size(10)
                .totalElements(42)
                .totalPages(5)
                .build();
        when(repository.findPage("topic-1", false, "2026-09", List.of("u1"), null, 2, 10))
                .thenReturn(page);

        LeaderboardPage result = handler.handle(new LeaderboardQuery.TopByTopicQuery(
                "topic-1", false, "2026-09", 2, 10, List.of("u1"), null));

        assertThat(result.entries()).containsExactly(entry);
        verify(repository).findPage("topic-1", false, "2026-09", List.of("u1"), null, 2, 10);
    }

    @Test
    void rank_delegates_without_scan_limit() {
        TopicLeaderboardEntry entry = TopicLeaderboardEntry.builder()
                .entryId("topic-1::user-1")
                .topicId("topic-1")
                .userId("user-1")
                .totalXp(120)
                .build();
        LeaderboardRank rank = new LeaderboardRank(entry, 137);
        when(repository.findRank("topic-1", "user-1", false, "2026-09", null, "FR"))
                .thenReturn(Optional.of(rank));

        Optional<LeaderboardRank> result = handler.handle(new LeaderboardQuery.GetTopicRankQuery(
                "topic-1", "user-1", false, "2026-09", null, "FR"));

        assertThat(result).contains(rank);
        verify(repository).findRank("topic-1", "user-1", false, "2026-09", null, "FR");
    }
}
