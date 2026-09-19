package io.github.quizup.leaderboard.application.handler.query;

import io.github.quizup.leaderboard.domain.model.LeaderboardRank;
import io.github.quizup.leaderboard.domain.model.TopicLeaderboardEntry;
import io.github.quizup.leaderboard.domain.port.out.LeaderboardRepositoryPort;
import io.github.quizup.leaderboard.domain.query.LeaderboardQuery;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * Handler Axon — queries de classement par thème, déléguées au port de projection.
 */
@Component
public class LeaderboardQueryHandler {

    /** Profondeur de scan pour résoudre le rang d'un joueur dans une portée. */
    private static final int RANK_SCAN_LIMIT = 100;

    private final LeaderboardRepositoryPort leaderboardRepositoryPort;

    public LeaderboardQueryHandler(LeaderboardRepositoryPort leaderboardRepositoryPort) {
        this.leaderboardRepositoryPort = leaderboardRepositoryPort;
    }

    @QueryHandler
    public List<TopicLeaderboardEntry> handle(LeaderboardQuery.TopByTopicQuery query) {
        return leaderboardRepositoryPort.findTop(
                query.topicId(),
                query.monthly(),
                query.month(),
                query.memberIds(),
                query.country(),
                query.limit()
        );
    }

    @QueryHandler
    public Optional<LeaderboardRank> handle(LeaderboardQuery.GetTopicRankQuery query) {
        List<TopicLeaderboardEntry> entries = leaderboardRepositoryPort.findTop(
                query.topicId(),
                query.monthly(),
                query.month(),
                query.memberIds(),
                query.country(),
                RANK_SCAN_LIMIT
        );

        return IntStream.range(0, entries.size())
                .filter(index -> entries.get(index).userId().equals(query.userId()))
                .mapToObj(index -> new LeaderboardRank(entries.get(index), index + 1))
                .findFirst();
    }
}
