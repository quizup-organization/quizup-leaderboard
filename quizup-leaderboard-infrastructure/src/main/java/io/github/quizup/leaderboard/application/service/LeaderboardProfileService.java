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
 * Une seule requête batch {@link ProfileQuery.GetProfilesByIdsQuery} ; les profils introuvables
 * sont omis (l'appelant conserve l'identité déjà dénormalisée).
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
        if (userIds == null || userIds.isEmpty()) {
            return List.of();
        }

        try {
            return queryGateway.query(
                            new ProfileQuery.GetProfilesByIdsQuery(userIds),
                            QueryResponseTypes.multipleInstancesOf(Profile.class))
                    .join().stream()
                    .map(profile -> new PlayerIdentity(
                            profile.userId(),
                            profile.displayName(),
                            profile.country(),
                            profile.avatarOptions()))
                    .toList();
        } catch (Exception exception) {
            logger.warn("Impossible de résoudre les profils {} : {}", userIds, exception.getMessage());
            return List.of();
        }
    }
}
