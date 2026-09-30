package io.github.quizup.leaderboard.application.projection;

import io.github.quizup.leaderboard.domain.model.PlayerIdentity;
import io.github.quizup.leaderboard.domain.model.TopicLeaderboardEntry;
import io.github.quizup.leaderboard.domain.model.LeaderboardRules;
import io.github.quizup.leaderboard.domain.port.out.LeaderboardAwardedGameRepositoryPort;
import io.github.quizup.leaderboard.domain.port.out.LeaderboardProfilePort;
import io.github.quizup.leaderboard.domain.port.out.LeaderboardRepositoryPort;
import io.github.quizup.profile.domain.event.ProfileEvent;
import io.github.quizup.profile.domain.event.ProgressionEvent;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * TopicLeaderboardProjection — maintient les projections read-only du classement par thème :
 * entrée all-time (XP cumulée) et entrée mensuelle par mois (historique conservé).
 *
 * <p>Consomme directement {@link ProgressionEvent.XpAwardedEvent} (l'ancien agrégat relais a été
 * supprimé : plus aucun événement local n'est écrit, l'event store ne croît plus avec les duels).
 * Les duels contre bot sont ignorés. L'identité publique (pseudonyme, avatar, pays) et le niveau
 * sont dénormalisés dans le read model ; les événements de champ du profil les rafraîchissent pour
 * qu'un changement de profil ne fige jamais le classement.</p>
 */
@Component
@ProcessingGroup("topic-leaderboard-projection")
public class TopicLeaderboardProjection {

    private static final Logger logger = LoggerFactory.getLogger(TopicLeaderboardProjection.class);

    private final LeaderboardRepositoryPort leaderboardRepositoryPort;
    private final LeaderboardAwardedGameRepositoryPort awardedGameRepositoryPort;
    private final LeaderboardProfilePort leaderboardProfilePort;

    public TopicLeaderboardProjection(LeaderboardRepositoryPort leaderboardRepositoryPort,
                                      LeaderboardAwardedGameRepositoryPort awardedGameRepositoryPort,
                                      LeaderboardProfilePort leaderboardProfilePort) {
        this.leaderboardRepositoryPort = leaderboardRepositoryPort;
        this.awardedGameRepositoryPort = awardedGameRepositoryPort;
        this.leaderboardProfilePort = leaderboardProfilePort;
    }

    @EventHandler
    @Transactional
    public void on(ProgressionEvent.XpAwardedEvent event) {
        if (event.botGame()) {
            logger.debug("Duel contre bot non classé: topicId={}, userId={}, gameId={}",
                    event.topicId(), event.userId(), event.gameId());
            return;
        }

        // Idempotence par clé métier (topicId, userId, gameId) : un rejeu ne recompte pas l'XP.
        if (!awardedGameRepositoryPort.record(event.topicId(), event.userId(), event.gameId())) {
            return;
        }

        Optional<PlayerIdentity> identity = resolveIdentity(event.userId());
        String month = LeaderboardRules.monthKey(event.awardedAt());

        TopicLeaderboardEntry allTime = leaderboardRepositoryPort
                .findByTopicAndUser(event.topicId(), event.userId())
                .orElseGet(() -> TopicLeaderboardEntry.empty(event.topicId(), event.userId()));
        int totalXp = allTime.totalXp() + event.xp();
        int level = LeaderboardRules.levelFor(totalXp);

        leaderboardRepositoryPort.saveAllTime(allTime.toBuilder()
                .totalXp(totalXp)
                .level(level)
                .pseudonym(identity.map(PlayerIdentity::pseudonym).orElse(allTime.pseudonym()))
                .avatarOptions(identity.map(PlayerIdentity::avatarOptions).orElse(allTime.avatarOptions()))
                .country(identity.map(PlayerIdentity::country).orElse(allTime.country()))
                .updatedAt(event.awardedAt())
                .build());

        TopicLeaderboardEntry monthly = leaderboardRepositoryPort
                .findMonthlyByTopicAndUser(event.topicId(), event.userId(), month)
                .orElseGet(() -> TopicLeaderboardEntry.emptyMonthly(event.topicId(), event.userId(), month));

        leaderboardRepositoryPort.saveMonthly(monthly.toBuilder()
                .monthlyXp(monthly.monthlyXp() + event.xp())
                .level(level)
                .pseudonym(identity.map(PlayerIdentity::pseudonym).orElse(monthly.pseudonym()))
                .avatarOptions(identity.map(PlayerIdentity::avatarOptions).orElse(monthly.avatarOptions()))
                .country(identity.map(PlayerIdentity::country).orElse(monthly.country()))
                .updatedAt(event.awardedAt())
                .build());

        logger.info("Classement thème projeté: topicId={}, userId={}, +{} XP, total={}, mois={}",
                event.topicId(), event.userId(), event.xp(), totalXp, month);
    }

    @EventHandler
    @Transactional
    public void on(ProfileEvent.ProfilePseudonymUpdatedEvent event) {
        leaderboardRepositoryPort.refreshPseudonym(event.userId(), event.pseudonym());

        logger.debug("Pseudonyme de classement rafraîchi: userId={}", event.userId());
    }

    @EventHandler
    @Transactional
    public void on(ProfileEvent.ProfileCountryUpdatedEvent event) {
        leaderboardRepositoryPort.refreshCountry(event.userId(), event.country());

        logger.debug("Pays de classement rafraîchi: userId={}", event.userId());
    }

    @EventHandler
    @Transactional
    public void on(ProfileEvent.ProfileAvatarUpdatedEvent event) {
        leaderboardRepositoryPort.refreshAvatarOptions(event.userId(), event.avatarOptions());

        logger.debug("Avatar de classement rafraîchi: userId={}", event.userId());
    }

    private Optional<PlayerIdentity> resolveIdentity(String userId) {
        try {
            return leaderboardProfilePort.findIdentities(List.of(userId)).stream().findFirst();
        } catch (Exception exception) {
            logger.warn("Profil introuvable pour {} : {}", userId, exception.getMessage());
            return Optional.empty();
        }
    }
}
