package io.github.quizup.leaderboard.domain.port.in;

import io.github.quizup.leaderboard.domain.model.LeaderboardRank;
import io.github.quizup.leaderboard.domain.model.LeaderboardScope;
import io.github.quizup.leaderboard.domain.model.TopicLeaderboardEntry;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Port entrant — lecture du classement d'un thème (all-time / mensuel), dans une
 * portée donnée (monde / amis / pays), et du rang d'un joueur dans ce thème.
 */
public interface GetLeaderboardUseCase {

    CompletableFuture<List<TopicLeaderboardEntry>> topByTopic(
            String topicId,
            boolean monthly,
            int limit,
            LeaderboardScope scope,
            String requesterId
    );

    CompletableFuture<Optional<LeaderboardRank>> rankOf(
            String topicId,
            String userId,
            boolean monthly,
            LeaderboardScope scope
    );
}
