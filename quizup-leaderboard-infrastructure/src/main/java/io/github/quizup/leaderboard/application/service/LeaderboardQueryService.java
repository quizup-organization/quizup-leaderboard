package io.github.quizup.leaderboard.application.service;

import io.github.quizup.microservice.core.infrastructure.axon.QueryResponseTypes;
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
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service applicatif — implémente {@link GetLeaderboardUseCase}.
 *
 * <p>Résout la portée (monde / amis / pays) puis enrichit les entrées avec
 * l'identité publique (quizup-profile).</p>
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
                                                                     int limit,
                                                                     LeaderboardScope scope,
                                                                     String requesterId) {
        ScopeFilter filter = resolveScope(scope, requesterId);

        return queryGateway.query(
                        new LeaderboardQuery.TopByTopicQuery(
                                topicId, monthly, LeaderboardRules.currentMonth(), limit,
                                filter.memberIds(), filter.country()),
                        QueryResponseTypes.multipleInstancesOf(TopicLeaderboardEntry.class))
                .thenApply(this::enrich);
    }

    @Override
    public CompletableFuture<Optional<LeaderboardRank>> rankOf(String topicId,
                                                               String userId,
                                                               boolean monthly,
                                                               LeaderboardScope scope) {
        ScopeFilter filter = resolveScope(scope, userId);

        return queryGateway.query(
                        new LeaderboardQuery.GetTopicRankQuery(
                                topicId, userId, monthly, LeaderboardRules.currentMonth(),
                                filter.memberIds(), filter.country()),
                        QueryResponseTypes.optionalInstanceOf(LeaderboardRank.class))
                .thenApply(optional -> optional.map(this::enrich));
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

        return new ScopeFilter(null, country);
    }

    private List<TopicLeaderboardEntry> enrich(List<TopicLeaderboardEntry> entries) {
        if (entries.isEmpty()) {
            return entries;
        }

        Map<String, PlayerIdentity> identities = identitiesOf(
                entries.stream().map(TopicLeaderboardEntry::userId).distinct().toList());

        return entries.stream()
                .map(entry -> applyIdentity(entry, identities.get(entry.userId())))
                .toList();
    }

    private LeaderboardRank enrich(LeaderboardRank ranked) {
        PlayerIdentity identity = identitiesOf(List.of(ranked.entry().userId()))
                .get(ranked.entry().userId());

        return new LeaderboardRank(applyIdentity(ranked.entry(), identity), ranked.rank());
    }

    private Map<String, PlayerIdentity> identitiesOf(List<String> userIds) {
        return leaderboardProfilePort.findIdentities(userIds).stream()
                .collect(Collectors.toMap(PlayerIdentity::userId, Function.identity(), (a, b) -> a));
    }

    private TopicLeaderboardEntry applyIdentity(TopicLeaderboardEntry entry, PlayerIdentity identity) {
        if (identity == null) {
            return entry;
        }

        return entry.toBuilder()
                .displayName(identity.displayName())
                .build();
    }

    private record ScopeFilter(List<String> memberIds, String country) {
    }
}
