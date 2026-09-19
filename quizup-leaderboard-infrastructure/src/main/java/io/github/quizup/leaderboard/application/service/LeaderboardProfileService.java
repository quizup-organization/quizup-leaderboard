package io.github.quizup.leaderboard.application.service;

import io.github.quizup.microservice.core.infrastructure.axon.QueryResponseTypes;
import io.github.quizup.leaderboard.domain.model.PlayerIdentity;
import io.github.quizup.leaderboard.domain.port.out.LeaderboardProfilePort;
import io.github.quizup.profile.domain.model.Profile;
import io.github.quizup.profile.domain.query.ProfileQuery;
import org.axonframework.queryhandling.QueryGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Adaptateur sortant inter-module : résout les identités via quizup-profile.
 */
@Service
public class LeaderboardProfileService implements LeaderboardProfilePort {

    private static final Logger logger = LoggerFactory.getLogger(LeaderboardProfileService.class);

    private final QueryGateway queryGateway;

    public LeaderboardProfileService(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    @Override
    public List<PlayerIdentity> findIdentities(List<String> userIds) {
        return userIds.stream()
                .map(this::resolve)
                .toList();
    }

    private PlayerIdentity resolve(String userId) {
        try {
            Profile profile = queryGateway.query(
                    new ProfileQuery.GetProfileQuery(userId),
                    QueryResponseTypes.instanceOf(Profile.class)
            ).join();

            return new PlayerIdentity(userId, profile.displayName(), profile.country());
        } catch (Exception exception) {
            logger.warn("Impossible de résoudre le profil {} : {}", userId, exception.getMessage());
            return new PlayerIdentity(userId, null, null);
        }
    }
}
