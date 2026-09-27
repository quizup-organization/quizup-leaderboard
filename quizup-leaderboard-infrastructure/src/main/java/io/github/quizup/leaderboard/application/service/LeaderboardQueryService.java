package io.github.quizup.leaderboard.application.service;

import io.github.quizup.microservice.core.infrastructure.axon.QueryResponseTypes;
import io.github.quizup.leaderboard.domain.model.LeaderboardPage;
import io.github.quizup.leaderboard.domain.model.LeaderboardRank;
import io.github.quizup.leaderboard.domain.model.LeaderboardRules;
import io.github.quizup.leaderboard.domain.model.LeaderboardScope;
import io.github.quizup.leaderboard.domain.model.PlayerIdentity;
import io.github.quizup.leaderboard.domain.model.TopicLeaderboardEntry;
import io.github.quizup.leaderboard.domain.port.in.GetLeaderboardUseCase;
import io.github.quizup.leaderboard.domain.port.out.LeaderboardFollowingPort;
import io.github.quizup.leaderboard.domain.port.out.LeaderboardProfilePort;
import io.github.quizup.leaderboard.domain.query.LeaderboardQuery;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Service applicatif — implémente {@link GetLeaderboardUseCase}.
 *
 * <p>Résout la portée (monde / amis / pays) ; l'identité et le niveau des entrées sont déjà
 * dénormalisés dans le read model (aucune résolution de profil par page).</p>
 */
@Service
public class LeaderboardQueryService implements GetLeaderboardUseCase {

    private final QueryGateway queryGateway;
    private final LeaderboardProfilePort leaderboardProfilePort;
    private final LeaderboardFollowingPort leaderboardFollowingPort;

    public LeaderboardQueryService(QueryGateway queryGateway,
                                   LeaderboardProfilePort leaderboardProfilePort,
                                   LeaderboardFollowingPort leaderboardFollowingPort) {
        this.queryGateway = queryGateway;
        this.leaderboardProfilePort = leaderboardProfilePort;
        this.leaderboardFollowingPort = leaderboardFollowingPort;
    }

    @Override
    public CompletableFuture<List<TopicLeaderboardEntry>> topByTopic(String topicId,
                                                                     boolean monthly,
                                                                     String month,
                                                                     int limit,
                                                                     LeaderboardScope scope,
                                                                     String requesterId) {
        ScopeFilter filter = resolveScope(scope, requesterId);

        return queryGateway.query(
                        new LeaderboardQuery.TopByTopicQuery(
                                topicId, monthly, monthOrCurrent(monthly, month), 0, limit,
                                filter.memberIds(), filter.country()),
                        QueryResponseTypes.instanceOf(LeaderboardPage.class))
                .thenApply(LeaderboardPage::entries);
    }

    @Override
    public CompletableFuture<Optional<LeaderboardRank>> rankOf(String topicId,
                                                               String userId,
                                                               boolean monthly,
                                                               String month,
                                                               LeaderboardScope scope) {
        ScopeFilter filter = resolveScope(scope, userId);

        return queryGateway.query(
                new LeaderboardQuery.GetTopicRankQuery(
                        topicId, userId, monthly, monthOrCurrent(monthly, month),
                        filter.memberIds(), filter.country()),
                QueryResponseTypes.optionalInstanceOf(LeaderboardRank.class));
    }

    /** Un classement mensuel sans mois explicite porte sur le mois courant. */
    private String monthOrCurrent(boolean monthly, String month) {
        if (!monthly) {
            return month;
        }
        return month == null || month.isBlank() ? LeaderboardRules.currentMonth() : month;
    }

    private ScopeFilter resolveScope(LeaderboardScope scope, String requesterId) {
        if (scope == null || requesterId == null || scope == LeaderboardScope.WORLD) {
            return new ScopeFilter(null, null);
        }

        if (scope == LeaderboardScope.FRIENDS) {
            Set<String> memberIds = new LinkedHashSet<>(leaderboardFollowingPort.getFollowingIds(requesterId));
            memberIds.add(requesterId);

            return new ScopeFilter(new ArrayList<>(memberIds), null);
        }

        String country = leaderboardProfilePort.findIdentities(List.of(requesterId)).stream()
                .map(PlayerIdentity::country)
                .findFirst()
                .orElse(null);

        return country == null ? new ScopeFilter(null, null) : new ScopeFilter(null, country);
    }

    private record ScopeFilter(List<String> memberIds, String country) {
    }
}
