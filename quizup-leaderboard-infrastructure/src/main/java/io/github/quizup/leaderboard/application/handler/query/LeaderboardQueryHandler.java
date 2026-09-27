package io.github.quizup.leaderboard.application.handler.query;

import io.github.quizup.leaderboard.domain.model.LeaderboardPage;
import io.github.quizup.leaderboard.domain.model.LeaderboardRank;
import io.github.quizup.leaderboard.domain.port.out.LeaderboardRepositoryPort;
import io.github.quizup.leaderboard.domain.query.LeaderboardQuery;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Handler Axon — queries de classement par thème, déléguées au port de projection.
 */
@Component
public class LeaderboardQueryHandler {

    private final LeaderboardRepositoryPort leaderboardRepositoryPort;

    public LeaderboardQueryHandler(LeaderboardRepositoryPort leaderboardRepositoryPort) {
        this.leaderboardRepositoryPort = leaderboardRepositoryPort;
    }

    @QueryHandler
    public LeaderboardPage handle(LeaderboardQuery.TopByTopicQuery query) {
        return leaderboardRepositoryPort.findPage(
                query.topicId(),
                query.monthly(),
                query.month(),
                query.memberIds(),
                query.country(),
                query.page(),
                query.size()
        );
    }

    @QueryHandler
    public Optional<LeaderboardRank> handle(LeaderboardQuery.GetTopicRankQuery query) {
        return leaderboardRepositoryPort.findRank(
                query.topicId(),
                query.userId(),
                query.monthly(),
                query.month(),
                query.memberIds(),
                query.country()
        );
    }
}
