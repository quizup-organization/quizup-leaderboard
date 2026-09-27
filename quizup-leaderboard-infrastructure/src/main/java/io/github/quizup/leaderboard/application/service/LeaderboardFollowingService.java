package io.github.quizup.leaderboard.application.service;

import io.github.quizup.microservice.core.infrastructure.axon.QueryResponseTypes;
import io.github.quizup.leaderboard.domain.port.out.LeaderboardFollowingPort;
import io.github.quizup.social.domain.model.FollowDirection;
import io.github.quizup.social.domain.model.UserFollower;
import io.github.quizup.social.domain.query.UserFollowerQuery;
import org.axonframework.queryhandling.QueryGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Adaptateur sortant inter-module : joueurs suivis via quizup-social, par la **query dédiée**
 * {@link UserFollowerQuery.GetUserFollowsQuery} (plus aucune utilisation de la recherche).
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
            return queryGateway.query(
                            new UserFollowerQuery.GetUserFollowsQuery(userId, FollowDirection.FOLLOWING, FOLLOWING_LIMIT),
                            QueryResponseTypes.multipleInstancesOf(UserFollower.class))
                    .join().stream()
                    .map(UserFollower::followedId)
                    .distinct()
                    .toList();
        } catch (Exception exception) {
            logger.warn("Abonnements introuvables pour {} : {}", userId, exception.getMessage());
            return List.of();
        }
    }
}
