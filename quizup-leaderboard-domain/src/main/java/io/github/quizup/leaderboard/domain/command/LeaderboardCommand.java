package io.github.quizup.leaderboard.domain.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.time.Instant;

public interface LeaderboardCommand {

    /**
     * Enregistre l'XP d'un duel dans le classement d'un thème pour un joueur.
     * Idempotente : un même {@code gameId} n'est crédité qu'une fois.
     */
    record RecordXpCommand(
            @TargetAggregateIdentifier String entryId,
            String topicId,
            String userId,
            String gameId,
            int xp,
            String month,
            String country,
            Instant occurredAt
    ) implements LeaderboardCommand {
    }
}
