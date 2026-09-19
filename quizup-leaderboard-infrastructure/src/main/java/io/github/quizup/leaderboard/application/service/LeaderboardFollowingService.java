package io.github.quizup.leaderboard.application.service;

import io.github.quizup.microservice.core.infrastructure.axon.QueryResponseTypes;
import io.github.quizup.leaderboard.domain.port.out.LeaderboardFollowingPort;
import io.github.quizup.microservice.core.domain.model.search.DefaultPageCriteria;
import io.github.quizup.microservice.core.domain.model.search.FilterCriteria;
import io.github.quizup.microservice.core.domain.model.search.FilterOperator;
import io.github.quizup.microservice.core.domain.model.search.PageResult;
import io.github.quizup.microservice.core.infrastructure.in.api.request.FilterRequest;
import io.github.quizup.social.domain.model.UserFollower;
import io.github.quizup.social.domain.query.UserFollowerQuery;
import org.axonframework.queryhandling.QueryGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Adaptateur sortant inter-module : joueurs suivis via quizup-social, en réutilisant la
 * **recherche** (`SearchUserFollowerQuery` + filtre `followerId`) — pas de query dédiée.
 */
@Service
public class LeaderboardFollowingService implements LeaderboardFollowingPort {

    private static final Logger logger = LoggerFactory.getLogger(LeaderboardFollowingService.class);
    private static final int FOLLOWING_LIMIT = 200;

    private final QueryGateway queryGateway;

    public LeaderboardFollowingService(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    @Override
    public List<String> getFollowingIds(String userId) {
        try {
            List<FilterCriteria> filters = List.of(
                    new FilterRequest("followerId", FilterOperator.EQUALS, userId, null, null)
            );

            PageResult<UserFollower> page = queryGateway.query(
                    new UserFollowerQuery.SearchUserFollowerQuery(
                            filters, List.of(), new DefaultPageCriteria(FOLLOWING_LIMIT, 0)),
                    QueryResponseTypes.pageResultOf(UserFollower.class)
            ).join();

            return page.content().stream()
                    .map(UserFollower::followedId)
                    .distinct()
                    .toList();
        } catch (Exception exception) {
            logger.warn("Abonnements introuvables pour {} : {}", userId, exception.getMessage());
            return List.of();
        }
    }
}
