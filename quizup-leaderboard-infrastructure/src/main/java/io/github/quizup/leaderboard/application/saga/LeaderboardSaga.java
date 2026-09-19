package io.github.quizup.leaderboard.application.saga;

import io.github.quizup.leaderboard.domain.command.LeaderboardCommand;
import io.github.quizup.leaderboard.domain.model.LeaderboardRules;
import io.github.quizup.leaderboard.domain.model.PlayerIdentity;
import io.github.quizup.leaderboard.domain.port.out.LeaderboardProfilePort;
import io.github.quizup.profile.domain.event.ProgressionEvent;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * LeaderboardSaga — reporte chaque attribution d'XP (`quizup-profile`) dans le
 * classement **du thème** concerné (all-time + mensuel). L'idempotence par
 * {@code gameId} est portée par l'agrégat.
 */
@Saga
public class LeaderboardSaga {

    private static final Logger logger = LoggerFactory.getLogger(LeaderboardSaga.class);

    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private transient LeaderboardProfilePort leaderboardProfilePort;

    @StartSaga
    @SagaEventHandler(associationProperty = "userId")
    public void on(ProgressionEvent.XpAwardedEvent event) {
        commandGateway.send(new LeaderboardCommand.RecordXpCommand(
                LeaderboardRules.entryId(event.topicId(), event.userId()),
                event.topicId(),
                event.userId(),
                event.gameId(),
                event.xp(),
                LeaderboardRules.monthKey(event.awardedAt()),
                resolveCountry(event.userId()),
                event.awardedAt()
        ));

        logger.info("Classement thème mis à jour: topicId={}, userId={}, +{} XP",
                event.topicId(), event.userId(), event.xp());

        SagaLifecycle.end();
    }

    private String resolveCountry(String userId) {
        try {
            return leaderboardProfilePort.findIdentities(List.of(userId)).stream()
                    .map(PlayerIdentity::country)
                    .findFirst()
                    .orElse(null);
        } catch (Exception exception) {
            logger.warn("Pays introuvable pour {} : {}", userId, exception.getMessage());
            return null;
        }
    }
}
